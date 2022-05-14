package si.uni_lj.fe.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        /* To check weekly activity */
        /*EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setText("Ljubljana");
        onSearch(searchBar);*/
        /* ************************ */
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

    private void onSearch(View v) {
        String cityName = ((EditText) findViewById(R.id.search_bar)).getText().toString();
        if (cityName.equals("")) {
            Toast.makeText(this, R.string.city_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Response response = getDailyWeatherResponse(cityName);
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

                Intent intent = new Intent(MainActivity.this, WeeklyActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUseLocation(View v) {

    }

    private Response getDailyWeatherResponse(String cityName) throws ExecutionException, InterruptedException {
        Request request = new Request.Builder()
                .url(UrlBuilder.getCurrentWeatherUrl(cityName))
                .build();
        CallbackFuture future = new CallbackFuture();
        CLIENT.newCall(request).enqueue(future);
        return future.get();
    }

    private void saveCurrentDataToSharedPreferences(CurrentData currentData, String currentDataJson) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentData", currentDataJson);
        editor.putString("selectedCity", currentData.getName());
        editor.apply();
        saveWeeklyDataToSharedPreferences(currentData.getLat(), currentData.getLon());
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

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("oneCallData", response.body().string());
                    editor.apply();
                }
            }
        });
    }
}