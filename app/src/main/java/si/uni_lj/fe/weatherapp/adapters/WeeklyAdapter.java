package si.uni_lj.fe.weatherapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import si.uni_lj.fe.weatherapp.R;
import si.uni_lj.fe.weatherapp.data.WeeklyData;

public class WeeklyAdapter extends ArrayAdapter<WeeklyData> {

    private final int resourceLayout;
    private final Context mContext;

    public WeeklyAdapter(@NonNull Context context, int resource, @NonNull List<WeeklyData> objects) {
        super(context, resource, objects);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
        }

        WeeklyData data = getItem(position);

        try {
            ((TextView) convertView.findViewById(R.id.day)).setText(data.getDay());
            ((TextView) convertView.findViewById(R.id.date)).setText(data.getDate());
            ((TextView) convertView.findViewById(R.id.day_temperature)).setText(data.getTempDay());
            ((TextView) convertView.findViewById(R.id.morn_temperature)).setText(data.getTempMorning());
            ((TextView) convertView.findViewById(R.id.daily_description)).setText(data.getDescription());
            ((TextView) convertView.findViewById(R.id.precip_prob)).setText(String.valueOf(data.getPrecipitation()));
            ImageView view = convertView.findViewById(R.id.hour_weather_logo);
            InputStream is = mContext.getAssets().open(String.format("weather/%s.png", data.getWeatherIcon()));
            Drawable drawable = Drawable.createFromStream(is, null);
            view.setImageDrawable(drawable);

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
