package si.uni_lj.fe.weatherapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.adapters.AlertAdapter;
import si.uni_lj.fe.weatherapp.data.AlertData;

public class AlertsActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private int hour, minute, year, month, day;
    private Button setTimeButton, setDateButton;
    private EditText selectedCity;
    private AlertAdapter alertAdapter;
    private View addAlertView;
    private boolean dateSet = false, timeSet = false;

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
        setTimeButton = addAlertView.findViewById(R.id.set_alert_time);
        setTimeButton.setOnClickListener(this::openTimePickerDialog);
        setDateButton = addAlertView.findViewById(R.id.set_alert_date);
        setDateButton.setOnClickListener(this::openDatePickerDialog);
        selectedCity = addAlertView.findViewById(R.id.select_city);
    }

    private void addAlert(View view) {
        if (!(dateSet && timeSet)) {
            Toast.makeText(this, R.string.set_time_date, Toast.LENGTH_SHORT).show();
            return;
        }
        List<AlertData> alertData = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("savedAlerts", MODE_PRIVATE);
        String savedAlerts = preferences.getString("savedAlertsData", null);
        Gson gson = new Gson();

        if (savedAlerts != null) {
            Type type = new TypeToken<List<AlertData>>() {}.getType();
            alertData = gson.fromJson(savedAlerts, type);
        }

        alertData.add(createNewAlertData(alertData.size()));
        alertAdapter.updateAdapter(alertData);
        cancelDialog(view);
    }

    private AlertData createNewAlertData(int position) {
        String city = selectedCity.getText().toString();
        if (city.equals("")) city = "Ljubljana";
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
        String date = getDate(day, month, year);
        String time = getTime(hour, minute);
        return new AlertData(position, localDateTime, date, time, city, true);
    }

    private void cancelDialog(View view) {
        (addAlertView.findViewById(R.id.cancel_alert)).setOnClickListener(null);
        (addAlertView.findViewById(R.id.save_alert)).setOnClickListener(null);
        dateSet = false;
        timeSet = false;
        dialog.dismiss();
    }

    private void openTimePickerDialog(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, timeHour, timeMinute) -> {
            hour = timeHour;
            minute = timeMinute;
            setTimeButton.setText(getTime(hour, minute));
            timeSet = true;
        };
        int style = TimePickerDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timeDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timeDialog.setTitle(getString(R.string.select_time));
        timeDialog.show();
    }

    private void openDatePickerDialog(View view) {
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, yyyy, mm, dd) -> {
            year = yyyy;
            month = mm;
            day = dd;
            setDateButton.setText(String.format(Locale.getDefault(), "%s", getDate(dd, mm, yyyy)));
            dateSet = true;
        };
        int style = DatePickerDialog.THEME_HOLO_LIGHT;
        DatePickerDialog dateDialog = new DatePickerDialog(this, style, onDateSetListener, year, month, day);
        dateDialog.setTitle(getString(R.string.select_date));
        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dateDialog.show();
    }

    private String getDate(int dd, int mm, int yyyy) {
        LocalDateTime date = LocalDateTime.of(yyyy, mm + 1, dd, 10, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy");
        return formatter.format(date);
    }

    private String getTime(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }
}
