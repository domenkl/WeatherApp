package si.uni_lj.fe.weatherapp.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.AlertsActivity;
import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.models.DailyInfo;
import si.uni_lj.fe.weatherapp.models.HourlyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;
import si.uni_lj.fe.weatherapp.models.RainOccurrence;
import si.uni_lj.fe.weatherapp.models.Weather;

public class ForecastReport {

    public static ForecastData handleWeatherResponse(String body, String city) {
        Locale.setDefault(new Locale("sl", "SI"));
        OneCallDataModel oneCallDataModel = new Gson().fromJson(body, OneCallDataModel.class);
        HourlyInfo current = oneCallDataModel.getCurrent();
        List<HourlyInfo> hourly = oneCallDataModel.getHourly().subList(0, 24);
        DailyInfo daily = oneCallDataModel.getDaily().get(0);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d.M");
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());
        city = city.equals("") ? city : " | " + city;
        String title = String.format("Vremenska napoved %s %s", dateTime, city);
        String currentIcon = current.getWeather().get(0).getIcon();

        String isRaining = current.getRain() == null && current.getCloudiness() >= 60 ? ", vendar ne dežuje" : "";

        String text = String.format("Trenutno vreme: %s%s.\n", current.getWeather().get(0).getDescription(), isRaining);
        String bigText = "" + text;
        bigText += String.format(Locale.getDefault(), "Trenutna temperatura je %d\u2103.\n", (int) current.getTemp());
        bigText += String.format("Dnevna napoved: %s.\n", daily.getWeather().get(0).getDescription());
        bigText += "Padavine v naslednjih 24 urah:\n";

        List<RainOccurrence> occurrences = getRainOccurrences(hourly);

        StringBuilder rainText = new StringBuilder("Dež: ");
        DateTimeFormatter dtf = DateTimeFormatter
                .ofPattern("H")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        for (RainOccurrence oc : occurrences) {
            Instant startInstant = Instant.ofEpochSecond(oc.getStart());
            Instant endInstant = Instant.ofEpochSecond(oc.getEnd());
            rainText.append(String.format("%s-%sh ", dtf.format(startInstant), dtf.format(endInstant)));
        }

        if (occurrences.size() == 0) bigText += "V naslednjih 24 urah naj ne bi bilo dežja.";
        else bigText += rainText;

        return new ForecastData(currentIcon, title, text, bigText);
    }

    private static List<RainOccurrence> getRainOccurrences(List<HourlyInfo> hourly) {
        List<RainOccurrence> occurrences = new ArrayList<>();
        boolean hasRained = false;
        for (HourlyInfo hour : hourly) {

            // if rain is not forecasted
            if (hour.getRain() == null) {
                hasRained = false;
                continue;
            }

            // rain is forecasted
            // if still uninitialized or has not rained before
            if (occurrences.size() == 0 || !hasRained) {
                Weather weather = hour.getWeather().get(0);
                int id = weather.getId();
                long start = hour.getDt();
                long end = start + 3_600;
                double precip = hour.getRain().getLastHour();
                RainOccurrence oc = new RainOccurrence(id, start, end, precip);
                occurrences.add(oc);
                hasRained = true;
            } else {
                // if it has rained before
                // update end, id
                int rainId = hour.getWeather().get(0).getId();
                RainOccurrence oc = occurrences.get(occurrences.size() - 1);
                if (oc.getRainId() < rainId) oc.setRainId(rainId);
                oc.setEnd(oc.getEnd() + 3_600);
            }
        }
        return occurrences;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void showNotification(Context context, int alertId, ForecastData data) throws IOException {
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, alertId,
                new Intent(context, AlertsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, context.getString(R.string.channel_id));
        }

        InputStream is = context.getAssets().open(String.format("weather/%s.png", data.getIcon()));

        builder.setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeStream(is))
                .setContentTitle(data.getTitle())
                .setContentText(data.getText())
                .setStyle(new Notification.BigTextStyle().bigText(data.getBigText()))
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(alertId, builder.build());
    }

    public static class ForecastData {
        private final String icon;
        private final String title;
        private final String text;
        private final String bigText;

        public ForecastData(String icon, String title, String text, String bigText) {
            this.icon = icon;
            this.title = title;
            this.text = text;
            this.bigText = bigText;
        }

        public String getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }

        public String getBigText() {
            return bigText;
        }
    }
}
