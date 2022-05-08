package si.uni_lj.fe.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class CurrentDataModel {

    private String name;
    private int timezone;

    @SerializedName("coord")
    private Coordinates coordinates;
    private List<Weather> weather;
    private Sys sys;

    @SerializedName("main")
    private MainData data;
    private Wind wind;
    private Cloud clouds;

    public static class Coordinates {
        @SerializedName("lon")
        private double longitude;

        @SerializedName("lat")
        private double latitude;

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

    public static class Sys {
        private String country;
        private long sunrise;
        private long sunset;

        public String getCountry() {
            return country;
        }

        public long getSunrise() {
            return sunrise;
        }

        public long getSunset() {
            return sunset;
        }
    }

    public static class Wind {
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    public static class MainData {
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;
        private int pressure;
        private int humidity;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

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

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }
    }

    public static class Cloud {
        @SerializedName("all")
        private int cloudiness;

        public int getCloudiness() {
            return cloudiness;
        }
    }

    public String getName() {
        return name;
    }

    public int getTimezone() {
        return timezone;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Sys getSys() {
        return sys;
    }

    public Wind getWind() {
        return wind;
    }

    public MainData getData() {
        return data;
    }

    public Cloud getClouds() {
        return clouds;
    }
}
