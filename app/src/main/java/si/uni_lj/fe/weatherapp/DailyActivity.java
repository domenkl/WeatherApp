package si.uni_lj.fe.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import si.uni_lj.fe.weatherapp.data.DailyData;
import si.uni_lj.fe.weatherapp.databinding.ActivityDailyBinding;
import si.uni_lj.fe.weatherapp.models.DailyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;

public class DailyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int position = intent.getIntExtra("day", 0);
        String city = intent.getStringExtra("city");
        String country = intent.getStringExtra("country");

        OneCallDataModel oneCallDataModel = getOneCallDataFromShared();
        DailyInfo dailyInfo = oneCallDataModel.getDaily().get(position);
        ActivityDailyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_daily);
        DailyData dailyData = new DailyData(dailyInfo, city, country, oneCallDataModel.getTimezone());
        binding.setDailyData(dailyData);
        setImageResource(this, R.id.weather_icon, dailyData.getIcon());
    }

    private OneCallDataModel getOneCallDataFromShared() {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        String oneCallData = preferences.getString("oneCallData", "");
        return new Gson().fromJson(oneCallData, OneCallDataModel.class);
    }

    @SuppressWarnings("SameParameterValue")
    private void setImageResource(Context context, int viewId, String imageName) {
        try {
            ImageView view = findViewById(viewId);
            InputStream is = context.getAssets().open(String.format("weather/%s.png", imageName));
            Drawable drawable = Drawable.createFromStream(is, null);
            view.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
