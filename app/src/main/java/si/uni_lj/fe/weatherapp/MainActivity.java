package si.uni_lj.fe.weatherapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.util.CallbackFuture;
import si.uni_lj.fe.weatherapp.util.OkHttpSingleton;
import si.uni_lj.fe.weatherapp.util.UrlBuilder;

public class MainActivity extends AppCompatActivity {

    private final OkHttpSingleton clientSingleton = OkHttpSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button search = findViewById(R.id.search);
        Button useLocation = findViewById(R.id.use_location);

        search.setOnClickListener(this::onSearch);
        useLocation.setOnClickListener(this::onUseLocation);

        /* To check weekly activity */
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setText("Ljubljana");
        onSearch(searchBar);
        /* ************************ */
    }

    private void onSearch(View v) {
        EditText searchBar = findViewById(R.id.search_bar);
        if (searchBar.getText().toString().equals("")) {
            Toast.makeText(this, R.string.city_empty, Toast.LENGTH_SHORT).show();
        } else {
            Request request = new Request.Builder()
                    .url(UrlBuilder.getGeocodeUrl(searchBar.getText().toString()))
                    .build();
            OkHttpClient client = clientSingleton.getClient();
            try {
                CallbackFuture future = new CallbackFuture();
                client.newCall(request).enqueue(future);
                Response response = future.get();

                if (!response.isSuccessful()) {
                    Toast.makeText(this, R.string.city_not_found, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, WeeklyActivity.class);
                    intent.putExtra("cityName", searchBar.getText().toString());
                    startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onUseLocation(View v) {

    }
}