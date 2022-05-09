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
            ((TextView) convertView.findViewById(R.id.temp_day)).setText(data.getTempDay());
            ((TextView) convertView.findViewById(R.id.temp_morning)).setText(data.getTempMorning());
            ((TextView) convertView.findViewById(R.id.wind)).setText(data.getWind());
            ((TextView) convertView.findViewById(R.id.precip_probability)).setText(String.valueOf(data.getPrecipitation()));
            ((TextView) convertView.findViewById(R.id.cloudiness)).setText(data.getCloudiness());
            ImageView view = convertView.findViewById(R.id.weather_logo);
            InputStream is = mContext.getAssets().open(String.format("weather/%s.png", data.getWeatherIcon()));
            Drawable drawable = Drawable.createFromStream(is, null);
            view.setImageDrawable(drawable);
        }catch (NullPointerException| IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
