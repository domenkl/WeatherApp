package si.uni_lj.fe.weatherapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.weatherapp.data.AlertData;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);
        if (id == 0) return;

        AlertData receivedAlert = turnOffAlertIfNotDaily(context, id);
        if (receivedAlert == null) return;

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
}
