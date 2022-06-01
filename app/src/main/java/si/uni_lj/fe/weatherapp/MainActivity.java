package si.uni_lj.fe.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.models.CurrentDataModel;
import si.uni_lj.fe.weatherapp.util.CallbackFuture;
import si.uni_lj.fe.weatherapp.util.UrlBuilder;

public class MainActivity extends AppCompatActivity {

    private static final OkHttpClient CLIENT;
    private double longitude, latitude;
    private boolean permissionGranted = false;
    private LocationManager locationManager;
    private Button search;

    static {
        CLIENT = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        Button useLocation = findViewById(R.id.use_location);
        EditText input = findViewById(R.id.search_bar);

        input.addTextChangedListener(searchBarWatcher);

        search.setEnabled(false);
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

    private Response getWeatherResponse(String url) throws ExecutionException, InterruptedException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        CallbackFuture future = new CallbackFuture();
        CLIENT.newCall(request).enqueue(future);
        return future.get();
    }

    private void handleCurrentDataResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            Toast.makeText(this, R.string.unsuccessful, Toast.LENGTH_SHORT).show();
            return;
        }
        if (response.body() != null) {
            CurrentDataModel currentDataModel = new Gson().fromJson(response.body().string(), CurrentDataModel.class);
            longitude = currentDataModel.getCoordinates().getLongitude();
            latitude = currentDataModel.getCoordinates().getLatitude();
            removeLocationUpdates();
            String currentCity = currentDataModel.getName();

            getAndSaveLocationAsync(currentCity);
        }
    }

    private void onUseLocation(View v) {
        if (!permissionGranted) {
            Toast.makeText(this, R.string.location_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        longitude = Math.floor(longitude * 100) / 100;
        latitude = Math.floor(latitude * 100) / 100;
        getAndSaveLocationAsync(null);
    }

    private void getAndSaveLocationAsync(String currentCity) {
        new Thread(() -> {
            try {
                getAndSaveLocation(currentCity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getAndSaveLocation(String currentCity) throws IOException {
        Address address = getAddressFromCoordinates(latitude, longitude);
        String currentCountry = address.getCountryCode();
        String addressCity = address.getLocality() != null ? address.getLocality() : address.getAdminArea();
        currentCity = currentCity != null ? currentCity : addressCity;

        saveCityAndCountry(currentCity, currentCountry);
        saveWeeklyDataToSharedPreferences();
    }

    private Address getAddressFromCoordinates(double lat, double lon) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
        return addresses.get(0);
    }

    private void saveCityAndCountry(String currentCity, String currentCountry) {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedCity", currentCity);
        editor.putString("savedCountry", currentCountry);
        editor.apply();
    }

    private void saveWeeklyDataToSharedPreferences() {
        Request request = new Request.Builder()
                .url(UrlBuilder.getOneCallWeatherUrl(latitude, longitude))
                .build();
        CLIENT.newCall(request).enqueue(new Callback() {
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
        });
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
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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
}