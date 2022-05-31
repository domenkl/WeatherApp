package si.uni_lj.fe.weatherapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.HourlyData;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private final List<HourlyData> hourlyInfo;
    private final Context context;

    public HourlyAdapter(List<HourlyData> hourlyInfo, Context context) {
        this.hourlyInfo = hourlyInfo;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyData hourlyData = hourlyInfo.get(position);
        holder.time.setText(hourlyData.getTime());
        holder.temperature.setText(hourlyData.getTemperature());
        holder.precipitation.setText(hourlyData.getPrecipitation());
        try {
            InputStream is = context.getAssets().open(String.format("weather/%s.png", hourlyData.getWeatherIcon()));
            Drawable drawable = Drawable.createFromStream(is, null);
            holder.weatherIcon.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return hourlyInfo.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView time, temperature, precipitation;
        private final ImageView weatherIcon;

        public ViewHolder(@NonNull View view) {
            super(view);
            time = view.findViewById(R.id.day_time);
            temperature = view.findViewById(R.id.day_temp);
            precipitation = view.findViewById(R.id.precipitation_value);
            weatherIcon = view.findViewById(R.id.day_weather);
        }
    }
}
