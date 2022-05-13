package si.uni_lj.fe.weatherapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.AlertData;

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

        try {
            ((TextView) convertView.findViewById(R.id.alert_date)).setText(alertData.getDate());
            ((TextView) convertView.findViewById(R.id.alert_time)).setText(alertData.getTime());
            ToggleButton alertToggle = convertView.findViewById(R.id.alert_toggle);
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
        data.setActive(!data.isActive());
    }

    private void removeAlert(View view) {
        int position = getPosition(view);
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
}
