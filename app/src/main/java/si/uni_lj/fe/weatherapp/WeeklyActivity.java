package si.uni_lj.fe.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.weatherapp.adapters.HourlyAdapter;
import si.uni_lj.fe.weatherapp.adapters.WeeklyAdapter;
import si.uni_lj.fe.weatherapp.data.CurrentData;
import si.uni_lj.fe.weatherapp.data.HourlyData;
import si.uni_lj.fe.weatherapp.data.WeeklyData;
import si.uni_lj.fe.weatherapp.databinding.ActivityWeeklyBinding;
import si.uni_lj.fe.weatherapp.models.DailyInfo;
import si.uni_lj.fe.weatherapp.models.HourlyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;
import si.uni_lj.fe.weatherapp.util.NonScrollListView;

public class WeeklyActivity extends AppCompatActivity {

    private OneCallDataModel oneCallDataModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCurrentDataBinding();
        setHourlyAdapter(convertHourlyData());
        setWeeklyAdapter(convertWeeklyData());
    }

    private void setCurrentDataBinding() {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        String oneCallDataString = preferences.getString("oneCallData", "");
        String city = preferences.getString("savedCity", "");
        String country = preferences.getString("savedCountry", "");

        oneCallDataModel = new Gson().fromJson(oneCallDataString, OneCallDataModel.class);
        ActivityWeeklyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_weekly);
        CurrentData currentData = new CurrentData(oneCallDataModel, city, country);
        binding.setCurrentData(currentData);

        setImageResource(this, R.id.weather_icon, currentData.getIcon());
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

    private List<HourlyData> convertHourlyData() {
        List<HourlyData> hourlyData = new ArrayList<>();
        ZoneId zoneOfLocation = ZoneId.of(oneCallDataModel.getTimezone());
        for (HourlyInfo hourlyInfo : oneCallDataModel.getHourly()) {
            hourlyData.add(new HourlyData(hourlyInfo, zoneOfLocation));
        }
        return hourlyData;
    }

    private void setHourlyAdapter(List<HourlyData> hourlyData) {
        RecyclerView recyclerView = findViewById(R.id.hourly_view);
        HourlyAdapter hourlyAdapter = new HourlyAdapter(hourlyData, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hourlyAdapter);
    }

    private List<WeeklyData> convertWeeklyData() {
        List<WeeklyData> weeklyData = new ArrayList<>();
        for (DailyInfo dailyInfo : oneCallDataModel.getDaily()) {
            weeklyData.add(new WeeklyData(dailyInfo));
        }
        return weeklyData;
    }

    private void setWeeklyAdapter(List<WeeklyData> weeklyData) {
        WeeklyAdapter weeklyAdapter = new WeeklyAdapter(this, R.layout.weekly_layout, weeklyData);
        NonScrollListView weeklyView = findViewById(R.id.weekly_view);
        weeklyView.setAdapter(weeklyAdapter);
    }
}
