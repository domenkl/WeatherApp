package si.uni_lj.fe.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.models.Coordinates;
import si.uni_lj.fe.weatherapp.models.CurrentDataModel;
import si.uni_lj.fe.weatherapp.services.AlertReceiver;
import si.uni_lj.fe.weatherapp.util.Util;

public class MainActivity extends AppCompatActivity {

    private double longitude, latitude;
    private Coordinates cityCoordinates;
    private boolean permissionGranted = false;
    private boolean savedLocation = false;
    private LocationManager locationManager;
    private Button search, useLocation, forecast;
    private long lastClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        useLocation = findViewById(R.id.use_location);
        forecast = findViewById(R.id.fast_forecast);
        EditText input = findViewById(R.id.search_bar);

        input.addTextChangedListener(searchBarWatcher);

        search.setEnabled(false);
        search.setOnClickListener(this::onSearch);
        useLocation.setOnClickListener(this::onUseLocation);

        checkLocationPermission();
        createNotificationChannel();

        if (!permissionGranted) setPermissionNeededButtons(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    @Override
    protected void onPostResume() {
        cityCoordinates = null;
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.goto_alerts) {
            Intent intent = new Intent(this, AlertsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.notify) {
            sendForecastNotification();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                setPermissionNeededButtons(true);
                getAndSetLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getAndSetLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 20, locationListener);
    }

    private void onSearch(View v) {
        String cityName = ((EditText) findViewById(R.id.search_bar)).getText().toString();
        if (cityName.equals("")) {
            Toast.makeText(this, R.string.city_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Response response = Util.getCurrentWeatherResponse(cityName);
            handleCurrentDataResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCurrentDataResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            Toast.makeText(this, R.string.unsuccessful, Toast.LENGTH_SHORT).show();
            return;
        }
        if (response.body() != null) {
            CurrentDataModel currentDataModel = new Gson().fromJson(response.body().string(), CurrentDataModel.class);
            cityCoordinates = currentDataModel.getCoordinates();
            removeLocationUpdates();
            String currentCity = currentDataModel.getName();

            getAndSaveLocationAsync(currentCity);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendForecastNotification() {
        if (lastClicked != 0 && System.currentTimeMillis() - lastClicked < 60_000) {
            Toast.makeText(this, R.string.forecast_paused, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        alarmIntent.putExtra("id", Util.FORECAST_REQUEST_CODE);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        lastClicked = System.currentTimeMillis();
        alarmManager.set(AlarmManager.RTC, lastClicked, pendingIntent);

    }

    private void onUseLocation(View v) {
        // update location
        saveLocationToPreferences();
        getAndSaveLocationAsync(null);
    }

    private void saveLocationToPreferences() {
        SharedPreferences preferences = getSharedPreferences("lastSavedLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Coordinates coordinates = new Coordinates(longitude, latitude);

        editor.putString("location", new Gson().toJson(coordinates));
        editor.apply();
    }

    private void getAndSaveLocationAsync(String currentCity) {
        new Thread(() -> getAndSaveLocation(currentCity)).start();
    }

    private void getAndSaveLocation(String currentCity) {
        String currentCountry = null;
        try {
            Locale.setDefault(new Locale("en", "US"));
            Address address;
            if (cityCoordinates == null) address = getAddressFromCoordinates(latitude, longitude);
            else address = getAddressFromCoordinates(cityCoordinates.getLatitude(), cityCoordinates.getLongitude());
            currentCountry = address.getCountryCode();
            String addressCity = address.getLocality() != null ? address.getLocality() : address.getAdminArea();
            currentCity = currentCity != null ? currentCity : addressCity;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (currentCountry == null) currentCountry = "?";
        if (currentCity == null) currentCity = getString(R.string.unknown);

        boolean shouldSave = shouldSaveCityAndCountry(currentCity, currentCountry);
        if (shouldSave) {
            saveOneCallData();
            return;
        }
        startWeeklyActivity();
    }

    private Address getAddressFromCoordinates(double lat, double lon) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
        return addresses.get(0);
    }

    private boolean shouldSaveCityAndCountry(String currentCity, String currentCountry) {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        long lastSavedOneCall = preferences.getLong("lastSavedOneCall", 0L);
        String savedCity = preferences.getString("savedCity", "");

        //if city is not the same or more than 5 minutes have passed since last use
        if (!savedCity.equals(currentCity) || (System.currentTimeMillis() - lastSavedOneCall) / 300_000 >= 1) {
            editor.putString("savedCity", currentCity);
            editor.putString("savedCountry", currentCountry);
            editor.apply();
            return true;
        }
        return false;
    }

    private void saveOneCallData() {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String oneCallData = response.body().string();
                    saveOneCallData(oneCallData);
                    runOnUiThread(() -> startWeeklyActivity());
                }
            }
        };
        Util.makeOneCallRequest(latitude, longitude, callback);
    }

    @SuppressLint("ApplySharedPref")
    private void saveOneCallData(String data) {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("oneCallData", data);
        editor.putLong("lastSavedOneCall", System.currentTimeMillis());
        editor.commit();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
            getAndSetLocation();
        } else {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void startWeeklyActivity() {
        Intent intent = new Intent(MainActivity.this, WeeklyActivity.class);
        removeLocationUpdates();
        startActivity(intent);
    }

    private void removeLocationUpdates() {
        if (locationManager != null) locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (!savedLocation) {
                saveLocationToPreferences();
                savedLocation = true;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //override super to get rid of error
        }
    };

    private final TextWatcher searchBarWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("")) {
                search.setEnabled(false);
                return;
            }
            search.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setPermissionNeededButtons(boolean isEnabled) {
        useLocation.setEnabled(isEnabled);
        forecast.setEnabled(isEnabled);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}