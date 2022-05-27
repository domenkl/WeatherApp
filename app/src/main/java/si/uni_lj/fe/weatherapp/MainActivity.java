package si.uni_lj.fe.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.data.CurrentData;
import si.uni_lj.fe.weatherapp.models.CurrentDataModel;
import si.uni_lj.fe.weatherapp.util.CallbackFuture;
import si.uni_lj.fe.weatherapp.util.UrlBuilder;

public class MainActivity extends AppCompatActivity {

    private static final OkHttpClient CLIENT;
    private double longitude, latitude;
    private boolean permissionGranted = false;
    private LocationManager locationManager;

    static {
        CLIENT = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button search = findViewById(R.id.search);
        Button useLocation = findViewById(R.id.use_location);

        search.setOnClickListener(this::onSearch);
        useLocation.setOnClickListener(this::onUseLocation);

        checkLocationPermission();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
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
            String dailyUrl = UrlBuilder.getCurrentWeatherUrl(cityName);
            Response response = getWeatherResponse(dailyUrl);
            handleCurrentDataResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUseLocation(View v) {
        if (!permissionGranted) {
            Toast.makeText(this, R.string.location_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        double currentLongitude = Math.floor(longitude * 100) / 100;
        double currentLatitude = Math.floor(latitude * 100) / 100;
        try {
            String dailyUrl = UrlBuilder.getCurrentWeatherUrl(currentLatitude, currentLongitude);
            Response response = getWeatherResponse(dailyUrl);
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
            Gson gson = new Gson();
            CurrentDataModel currentDataModel = gson.fromJson(response.body().string(), CurrentDataModel.class);
            CurrentData currentData = new CurrentData(currentDataModel);
            String currentDataJson = gson.toJson(currentData);

            saveCurrentDataToSharedPreferences(currentData, currentDataJson);
        }
    }

    private Response getWeatherResponse(String url) throws ExecutionException, InterruptedException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        CallbackFuture future = new CallbackFuture();
        CLIENT.newCall(request).enqueue(future);
        return future.get();
    }

    private void saveCurrentDataToSharedPreferences(CurrentData currentData, String currentDataJson) {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String savedCity = preferences.getString("savedCity", "");

        editor.putString("savedData", currentDataJson);
        editor.putString("savedCity", currentData.getName());
        editor.putLong("lastSavedCurrent", System.currentTimeMillis());
        editor.apply();

        long lastSaved = preferences.getLong("lastSavedOneCall", 0L);

        if (!(savedCity.equals(currentData.getName())) || (System.currentTimeMillis() - lastSaved) / 1000 / 3600 / 10 >= 1) {
            saveWeeklyDataToSharedPreferences(currentData.getLat(), currentData.getLon());
        }
    }

    private void saveWeeklyDataToSharedPreferences(double lat, double lon) {
        Request request = new Request.Builder()
                .url(UrlBuilder.getOneCallWeatherUrl(lat, lon))
                .build();
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("oneCallData", response.body().string());
                    editor.putLong("lastSavedOneCall", System.currentTimeMillis());
                    editor.commit();
                    runOnUiThread( () -> startWeeklyActivity());
                }
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
            getAndSetLocation();
        } else {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void startWeeklyActivity() {
        Intent intent = new Intent(MainActivity.this, WeeklyActivity.class);
        removeLocationUpdates();
        startActivity(intent);
    }

    private void removeLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };
}