package si.uni_lj.fe.weatherapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.AlertsActivity;
import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.AlertData;
import si.uni_lj.fe.weatherapp.models.Coordinates;
import si.uni_lj.fe.weatherapp.models.DailyInfo;
import si.uni_lj.fe.weatherapp.models.HourlyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;
import si.uni_lj.fe.weatherapp.models.RainOccurrence;
import si.uni_lj.fe.weatherapp.models.Weather;
import si.uni_lj.fe.weatherapp.util.Util;

public class AlertReceiver extends BroadcastReceiver {

    private Context context;
    private AlertData receivedAlert;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm", "received" + " " + LocalDateTime.now().atZone(ZoneId.systemDefault()));
        int id = intent.getIntExtra("id", 0);
        this.context = context;
        if (id == 0) return;

        // received instantly after clicking on forecast button
        // no need to check and turn off the alert, no need for fused provider either
        if (id == Util.FORECAST_REQUEST_CODE) {
            receivedAlert = new AlertData(0, LocalTime.now(), "", false, false);
            useLastResort();
            return;
        }

        receivedAlert = turnOffAlertIfNotDaily(id);
        if (receivedAlert == null) return;

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (!checkLocationPrivileges()) return;
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener( location -> {
                    if (location != null) checkWeatherReport(location);
                    else useLastResort();
                });
    }

    private void useLastResort() {
        Log.i("Location", "Using last resort...");
        SharedPreferences preferences = context.getSharedPreferences("lastSavedLocation", Context.MODE_PRIVATE);
        String locationString = preferences.getString("location", "");
        Coordinates coordinates = new Gson().fromJson(locationString, Coordinates.class);

        if (coordinates == null) return;

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLongitude(coordinates.getLongitude());
        location.setLatitude(coordinates.getLatitude());
        checkWeatherReport(location);
    }

    private AlertData turnOffAlertIfNotDaily(long id) {
        List<AlertData> alertData = getAlertData();
        AlertData receivedAlert = null;
        for (AlertData alert : alertData) {
            if (alert.getId() == id) receivedAlert = alert;
        }

        if (receivedAlert != null && !receivedAlert.isDaily()) {
            receivedAlert.setActive(false);
            updateAlertData(alertData);
        }
        return receivedAlert;
    }

    private List<AlertData> getAlertData() {
        List<AlertData> alertData = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences("savedAlerts", Context.MODE_PRIVATE);
        String savedAlerts = preferences.getString("savedAlertsData", null);
        Gson gson = new Gson();

        if (savedAlerts != null) {
            Type type = new TypeToken<List<AlertData>>() {}.getType();
            alertData = gson.fromJson(savedAlerts, type);
        }
        return alertData;
    }

    private void updateAlertData(List<AlertData> alertData) {
        SharedPreferences preferences = context.getSharedPreferences("savedAlerts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedAlertsData", new Gson().toJson(alertData));
        editor.apply();
    }

    private boolean checkLocationPrivileges() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkWeatherReport(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    handleWeatherResponse(response.body().string());
                }
            }
        };
        Util.makeOneCallRequest(latitude, longitude, callback);
    }

    private void handleWeatherResponse(String body) throws IOException {
        Locale.setDefault(new Locale("sl", "SI"));
        OneCallDataModel oneCallDataModel = new Gson().fromJson(body, OneCallDataModel.class);
        HourlyInfo current = oneCallDataModel.getCurrent();
        List<HourlyInfo> hourly = oneCallDataModel.getHourly().subList(0, 24);
        DailyInfo daily = oneCallDataModel.getDaily().get(0);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d.M");
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());
        String city = receivedAlert.getCity();
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

        showNotification(R.drawable.ic_notification, currentIcon, title, text, bigText);
    }

    private List<RainOccurrence> getRainOccurrences(List<HourlyInfo> hourly) {
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
    private void showNotification(int smallIcon, String largeIcon, String title, String text, String bigText) throws IOException {
        int alertId = receivedAlert.getId();
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, alertId,
                new Intent(context, AlertsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, context.getString(R.string.channel_id));
        }

        InputStream is = context.getAssets().open(String.format("weather/%s.png", largeIcon));

        builder.setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeStream(is))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(bigText))
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(alertId, builder.build());
    }
}
