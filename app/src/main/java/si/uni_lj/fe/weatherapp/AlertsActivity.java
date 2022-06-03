package si.uni_lj.fe.weatherapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.adapters.AlertAdapter;
import si.uni_lj.fe.weatherapp.data.AlertData;

public class AlertsActivity extends AppCompatActivity {

    private boolean isDaily = false;
    private AlertDialog dialog;
    private TimePicker timePicker;
    private EditText selectedCity;
    private AlertAdapter alertAdapter;
    private View addAlertView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        Button addAlerts = findViewById(R.id.add_alerts);

        alertAdapter = new AlertAdapter(this);
        ListView alertsView = findViewById(R.id.alert_items);
        alertsView.setAdapter(alertAdapter);
        addAlerts.setOnClickListener(this::createAddAlertDialog);
        Locale.setDefault(new Locale("sl", "SI"));
    }

    @SuppressLint("InflateParams")
    private void createAddAlertDialog(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        addAlertView = getLayoutInflater().inflate(R.layout.add_alert_popup, null);
        dialogBuilder.setView(addAlertView);

        dialog = dialogBuilder.create();
        dialog.show();

        setAlertViewFields();
    }

    private void setAlertViewFields() {
        (addAlertView.findViewById(R.id.cancel_alert)).setOnClickListener(this::cancelDialog);
        (addAlertView.findViewById(R.id.save_alert)).setOnClickListener(this::addAlert);
        selectedCity = addAlertView.findViewById(R.id.select_city);
        timePicker = addAlertView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        MaterialButtonToggleGroup toggleButton = addAlertView.findViewById(R.id.repeat_group);
        toggleButton.addOnButtonCheckedListener(this::onButtonChecked);
    }

    private void addAlert(View view) {
        List<AlertData> alertData = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("savedAlerts", MODE_PRIVATE);
        String savedAlerts = preferences.getString("savedAlertsData", null);
        Gson gson = new Gson();

        if (savedAlerts != null) {
            Type type = new TypeToken<List<AlertData>>() {}.getType();
            alertData = gson.fromJson(savedAlerts, type);
        }

        AlertData newAlert = createNewAlertData();
        alertData.add(newAlert);
        alertAdapter.addAlertNotification(newAlert);
        alertAdapter.updateAdapter(alertData);
        cancelDialog(view);
    }

    private AlertData createNewAlertData() {
        int position = ((Long) (System.currentTimeMillis() / 10)).intValue();
        String city = selectedCity.getText().toString();
        if (city.equals("")) city = null;
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        LocalTime localTime = LocalTime.of(hour, minute);
        return new AlertData(position, localTime, city, isDaily, true);
    }

    private void cancelDialog(View view) {
        (addAlertView.findViewById(R.id.cancel_alert)).setOnClickListener(null);
        (addAlertView.findViewById(R.id.save_alert)).setOnClickListener(null);
        dialog.dismiss();
    }

    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (!isChecked) return;
        isDaily = checkedId != R.id.button_once;
    }
}
