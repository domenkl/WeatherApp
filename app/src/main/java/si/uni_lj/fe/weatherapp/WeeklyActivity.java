package si.uni_lj.fe.weatherapp;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.weatherapp.adapters.WeeklyAdapter;
import si.uni_lj.fe.weatherapp.data.CurrentData;
import si.uni_lj.fe.weatherapp.data.WeeklyData;
import si.uni_lj.fe.weatherapp.databinding.ActivityWeeklyBinding;
import si.uni_lj.fe.weatherapp.models.DailyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;

public class WeeklyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCurrentDataBinding();
        getWeeklyDataAndSetAdapter();
    }

    private void setCurrentDataBinding() {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        String currentDataString = preferences.getString("savedData", "");
        CurrentData currentData = new Gson().fromJson(currentDataString, CurrentData.class);
        ActivityWeeklyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_weekly);
        binding.setCurrentData(currentData);

        setImageResource(R.id.weather_icon, currentData.getIcon(), "weather");
    }

    private void getWeeklyDataAndSetAdapter() {
        SharedPreferences preferences = getSharedPreferences("savedWeatherData", MODE_PRIVATE);
        String oneCallData = preferences.getString("oneCallData", "");
        OneCallDataModel oneCallDataModel = new Gson().fromJson(oneCallData, OneCallDataModel.class);
        List<WeeklyData> weeklyData = new ArrayList<>();
        for (DailyInfo dailyInfo : oneCallDataModel.getDaily()) {
            weeklyData.add(new WeeklyData(dailyInfo));
        }
        setWeeklyAdapter(weeklyData);
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

    private void setWeeklyAdapter(List<WeeklyData> weeklyData) {
        WeeklyAdapter adapter = new WeeklyAdapter(this, R.layout.weekly_layout, weeklyData);
        ListView weeklyList = findViewById(R.id.weekly_list);
        weeklyList.setAdapter(adapter);
        weeklyList.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
    }
}
