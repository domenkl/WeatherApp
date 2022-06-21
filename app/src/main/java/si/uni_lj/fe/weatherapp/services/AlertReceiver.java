package si.uni_lj.fe.weatherapp.services;

import static si.uni_lj.fe.weatherapp.util.ForecastReport.ForecastData;
import static si.uni_lj.fe.weatherapp.util.ForecastReport.handleWeatherResponse;
import static si.uni_lj.fe.weatherapp.util.ForecastReport.showNotification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.data.AlertData;
import si.uni_lj.fe.weatherapp.models.Coordinates;
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
                    ForecastData data = handleWeatherResponse(response.body().string(), receivedAlert.getCity());
                    showNotification(context, receivedAlert.getId(), data);
                }
            }
        };
        Util.makeOneCallRequest(latitude, longitude, callback);
    }
}
