package si.uni_lj.fe.weatherapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AlertsActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private int hour, minute, year, month, day;
    private Button setTimeButton, setDateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        Button addAlerts = findViewById(R.id.add_alerts);

        addAlerts.setOnClickListener(this::createAddAlertDialog);
    }

    @SuppressLint("InflateParams")
    private void createAddAlertDialog(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View addAlertView = getLayoutInflater().inflate(R.layout.add_alert_popup, null);
        dialogBuilder.setView(addAlertView);
        dialog = dialogBuilder.create();
        dialog.show();

        Locale.setDefault(new Locale("sl", "SI"));
        setTimeButton = addAlertView.findViewById(R.id.set_alert_time);
        setTimeButton.setOnClickListener(this::openTimePickerDialog);
        setDateButton = addAlertView.findViewById(R.id.set_alert_date);
        setDateButton.setOnClickListener(this::openDatePickerDialog);
        (addAlertView.findViewById(R.id.cancel_alert)).setOnClickListener(this::cancelDialog);

    }

    private void cancelDialog(View view) {
        dialog.dismiss();
    }

    private void openTimePickerDialog(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, timeHour, timeMinute) -> {
            hour = timeHour;
            minute = timeMinute;
            setTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
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
            setDateButton.setText(String.format(Locale.getDefault(), "%s", getDay(dd, mm, yyyy)));
        };
        int style = DatePickerDialog.THEME_HOLO_LIGHT;
        DatePickerDialog dateDialog = new DatePickerDialog(this, style, onDateSetListener, year, month, day);
        dateDialog.setTitle(getString(R.string.select_date));
        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dateDialog.show();

    }

    private String getDay(int dd, int mm, int yyyy) {
        LocalDateTime date = LocalDateTime.of(yyyy, mm + 1, dd, 10, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy");
        return formatter.format(date);
    }

}
