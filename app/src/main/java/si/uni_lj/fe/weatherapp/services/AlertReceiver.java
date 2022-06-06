package si.uni_lj.fe.weatherapp.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import si.uni_lj.fe.weatherapp.AlertsActivity;
import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.AlertData;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);
        if (id == 0) return;

        AlertData receivedAlert = turnOffAlertIfNotDaily(context, id);
        if (receivedAlert == null) return;

        try {
            showNotification(context, receivedAlert);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Alarm", "received" + " " + LocalDateTime.now().atZone(ZoneId.systemDefault()));
    }

    private AlertData turnOffAlertIfNotDaily(Context context, long id) {
        List<AlertData> alertData = getAlertData(context);
        AlertData receivedAlert = null;
        for (AlertData alert : alertData) {
            if (alert.getId() == id) receivedAlert = alert;
        }
        if (receivedAlert == null) return null;

        if (!receivedAlert.isDaily()) {
            receivedAlert.setActive(false);
            updateAlertData(context, alertData);
        }
        return receivedAlert;
    }

    private List<AlertData> getAlertData(Context context) {
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

    private void updateAlertData(Context context, List<AlertData> alertData) {
        SharedPreferences preferences = context.getSharedPreferences("savedAlerts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedAlertsData", new Gson().toJson(alertData));
        editor.apply();
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void showNotification(Context context, AlertData alertData) throws IOException {
        int alertId = alertData.getId();
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, alertId,
                new Intent(context, AlertsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, context.getString(R.string.channel_id));
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d.M");
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());
        String city = alertData.getCity();
        city = city.equals("") ? city : " | " + city;
        String title = String.format("Vremenska napoved za %s %s", dateTime, city);

        InputStream is = context.getAssets().open(String.format("weather/%s.png", "01d"));

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeStream(is))
                .setContentTitle(title)
                .setContentText("Današnja vremenska napoved")
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(alertId, builder.build());
    }
}
