package si.uni_lj.fe.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.AlertData;
import si.uni_lj.fe.weatherapp.services.AlertReceiver;

public class AlertAdapter extends BaseAdapter {

    private final Context context;
    private final List<AlertData> alertData;

    public AlertAdapter(@NonNull Context context) {
        this.context = context;
        this.alertData = getAlertData();
    }

    @Override
    public int getCount() {
        return alertData.size();
    }

    @Override
    public AlertData getItem(int i) {
        return alertData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.alerts_layout, parent, false);
        }

        AlertData alertData = getItem(position);

        int max = getCount();
        int rStart = 125, rStop = 193, gStart = 160, gStop = 218;
        int r, g, b = 255;
        if (max > 10) {
            r = (int) (rStop - Math.floor((rStop - rStart) / (max - 1.0)) * position);
            g = (int) (gStop - Math.floor((gStop - gStart) / (max - 1.0)) * position);
        } else {
            r = rStop - 5 * position;
            g = gStop - 5 * position;
        }
        try {
            RelativeLayout layout = convertView.findViewById(R.id.alert);
            layout.setBackgroundColor(Color.rgb(r, g, b));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            String time = dtf.format(alertData.getTime());
            long millisDifference = getMillisFromLocalTime(alertData.getTime()) - System.currentTimeMillis();
            long hours = millisDifference / 3_600_000;
            long minutes = (long) Math.ceil((millisDifference - hours * 3_600_000) / 60_000.0);

            String repeatType = alertData.isDaily() ? context.getString(R.string.daily) : context.getString(R.string.once);
            if (alertData.isActive())
                repeatType = String.format("%s | Alarm čez %s h %s min", repeatType, hours, minutes);

            TextView alertCity = convertView.findViewById(R.id.alert_city);
            alertCity.setText(alertData.getCity());

            ((TextView) convertView.findViewById(R.id.alert_time)).setText(time);
            ((TextView) convertView.findViewById(R.id.alert_repeat)).setText(repeatType);
            SwitchCompat alertToggle = convertView.findViewById(R.id.toggle_alert);
            alertToggle.setChecked(alertData.isActive());
            alertToggle.setOnClickListener(this::toggleAlert);
            convertView.findViewById(R.id.remove_alert).setOnClickListener(this::removeAlert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void toggleAlert(View view) {
        int position = getPosition(view);
        AlertData data = alertData.get(position);
        boolean toggleActive = !data.isActive();
        data.setActive(toggleActive);
        if (toggleActive) addAlertNotification(data);
        else removeAlertNotification(data.getId());
        updateAlertData();
        this.notifyDataSetChanged();
    }

    private void removeAlert(View view) {
        int position = getPosition(view);
        removeAlertNotification(alertData.get(position).getId());
        alertData.remove(position);
        updateAlertData();
        this.notifyDataSetChanged();
    }

    private List<AlertData> getAlertData() {
        List<AlertData> alertData = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences("savedAlerts", Context.MODE_PRIVATE);
        String savedAlerts = preferences.getString("savedAlertsData", null);
        if (savedAlerts != null) {
            Type type = new TypeToken<List<AlertData>>() {}.getType();
            alertData = new Gson().fromJson(savedAlerts, type);
        }
        return alertData;
    }

    private void updateAlertData() {
        SharedPreferences preferences = context.getSharedPreferences("savedAlerts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedAlertsData", new Gson().toJson(alertData));
        editor.apply();
    }

    public void updateAdapter(List<AlertData> alertData) {
        this.alertData.clear();
        this.alertData.addAll(alertData);
        updateAlertData();
        this.notifyDataSetChanged();
    }

    private int getPosition(View view) {
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        return listView.getPositionForView(parentRow);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void addAlertNotification(AlertData alertData) {
        Intent alarmIntent = new Intent(context, AlertReceiver.class);
        alarmIntent.putExtra("id", alertData.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alertData.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(Instant.ofEpochMilli(getMillisFromLocalTime(alertData.getTime()))));
        if (!alertData.isDaily()) {
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(context, "Enkratni alarm je prižgan", Toast.LENGTH_SHORT).show();
            return;
        }
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 86_400_000L, pendingIntent);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Dnevni alarm je prižgan", Toast.LENGTH_SHORT).show();
    }

    private long getMillisFromLocalTime(LocalTime time) {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), time);
        long timeMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (timeMillis - System.currentTimeMillis() < 1000) {
            timeMillis += 86_400_000L;
        }
        return timeMillis;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void removeAlertNotification(int alertId) {
        Intent alarmIntent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alertId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
