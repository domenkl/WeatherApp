package si.uni_lj.fe.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class HourlyInfo {


    private long dt;
    private long sunrise;
    private long sunset;
    private double temp;

    @SerializedName("feels_like")
    private double feelsLike;
    private int pressure;
    private int humidity;

    @SerializedName("clouds")
    private int cloudiness;

    @SerializedName("uvi")
    private double uviIndex;
    private int visibility;

    @SerializedName("wind_speed")
    private double windSpeed;

    @SerializedName("wind_deg")
    private int windDeg;

    @SerializedName("wind_gust")
    private double windGust;

    private Precipitation rain;
    private Precipitation snow;
    private List<Weather> weather;

    private static class Precipitation {
        @SerializedName("1h")
        private double lastHour;

        public double getLastHour() {
            return lastHour;
        }
    }

    public long getDt() {
        return dt;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public double getUviIndex() {
        return uviIndex;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDeg() {
        return windDeg;
    }

    public double getWindGust() {
        return windGust;
    }

    public Precipitation getRain() {
        return rain;
    }

    public Precipitation getSnow() {
        return snow;
    }

    public List<Weather> getWeather() {
        return weather;
    }
}
