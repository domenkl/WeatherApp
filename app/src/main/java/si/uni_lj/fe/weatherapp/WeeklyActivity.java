package si.uni_lj.fe.weatherapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import si.uni_lj.fe.weatherapp.data.CurrentData;
import si.uni_lj.fe.weatherapp.databinding.ActivityWeeklyBinding;
import si.uni_lj.fe.weatherapp.models.CurrentDataModel;
import si.uni_lj.fe.weatherapp.util.CallbackFuture;
import si.uni_lj.fe.weatherapp.util.OkHttpSingleton;
import si.uni_lj.fe.weatherapp.util.UrlBuilder;

public class WeeklyActivity extends AppCompatActivity {

    private final OkHttpSingleton clientSingleton = OkHttpSingleton.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        Log.e("App", cityName);

        setCurrentDataBinding(cityName);
    }

    private void setCurrentDataBinding(String cityName) {
        try {
            Response response = getDailyWeatherResponse(cityName);
            if (!response.isSuccessful()) {
                runOnUiThread(() ->
                        Toast.makeText(WeeklyActivity.this, R.string.city_not_found, Toast.LENGTH_SHORT).show());
                return;
            }
            if (response.body() != null) {
                Gson gson = new Gson();
                CurrentDataModel currentDataModel = gson.fromJson(response.body().string(), CurrentDataModel.class);
                CurrentData currentData = new CurrentData(currentDataModel);
                ActivityWeeklyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_weekly);
                binding.setCurrentData(currentData);
                setImageResource(R.id.weather_icon, currentData.getIcon(), "weather");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("SameParameterValue")
    private void setImageResource(int viewId, String imageName, String directory) {
        try {

            ImageView view = findViewById(viewId);
            InputStream is = getAssets().open(String.format("%s/%s.png", directory, imageName));
            Drawable drawable = Drawable.createFromStream(is, null);
            view.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Response getDailyWeatherResponse(String cityName) throws ExecutionException, InterruptedException {
        Request request = new Request.Builder()
                .url(UrlBuilder.getCurrentWeatherUrl(cityName))
                .build();
        OkHttpClient client = clientSingleton.getClient();
        CallbackFuture future = new CallbackFuture();
        client.newCall(request).enqueue(future);
        return future.get();
    }
}
