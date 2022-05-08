package si.uni_lj.fe.weatherapp.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class DailyInfo {

    private long dt;
    private long sunrise;
    private long sunset;
    private long moonrise;
    private long moonset;

    @SerializedName("moon_phase")
    private float moonPhase;

    @SerializedName("temp")
    private Temperature temperatures;

    @SerializedName("feels_like")
    private FeelsLike feelsLike;
    private int pressure;
    private int humidity;

    @SerializedName("wind_speed")
    private double windSpeed;

    @SerializedName("wind_deg")
    private int windDeg;

    @SerializedName("wind_gust")
    private double windGust;

    private Weather weather;
    private int clouds;
    private double pop;
    private double rain;
    private double uvi;

    private static class Temperature {
        private double day;
        private double min;
        private double max;
        private double night;
        private double eve;
        private double morn;

        public double getDay() {
            return day;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getNight() {
            return night;
        }

        public double getEve() {
            return eve;
        }

        public double getMorn() {
            return morn;
        }
    }

    private static class FeelsLike {
        private double day;
        private double night;
        private double eve;
        private double morn;

        public double getDay() {
            return day;
        }

        public double getNight() {
            return night;
        }

        public double getEve() {
            return eve;
        }

        public double getMorn() {
            return morn;
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

    public long getMoonrise() {
        return moonrise;
    }

    public long getMoonset() {
        return moonset;
    }

    public float getMoonPhase() {
        return moonPhase;
    }

    public Temperature getTemperatures() {
        return temperatures;
    }

    public FeelsLike getFeelsLike() {
        return feelsLike;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
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

    public Weather getWeather() {
        return weather;
    }

    public int getClouds() {
        return clouds;
    }

    public double getPop() {
        return pop;
    }

    public double getRain() {
        return rain;
    }

    public double getUvi() {
        return uvi;
    }
}
