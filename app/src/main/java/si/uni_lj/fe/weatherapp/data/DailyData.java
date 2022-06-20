package si.uni_lj.fe.weatherapp.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.models.DailyInfo;

public class DailyData {

    private final String city;
    private final String country;
    private final String day;
    private final String icon;
    private final String description;
    private final int temperature;
    private final int pressure;
    private final int humidity;
    private final String windSpeed;
    private final int cloudiness;
    private final String precipitation;
    private final String uviIndex;
    private final long sunrise;
    private final long sunset;
    private final String visibility;

    public DailyData(DailyInfo data, String city, String country) {
        this.city = city;
        this.country = country;
        this.sunrise = data.getSunrise();
        this.sunset = data.getSunset();
        this.day = setDate(data.getDt());
        this.temperature = (int) Math.round(data.getTemperatures().getDay());
        this.icon = data.getWeather().get(0).getIcon();
        this.precipitation = (double) Math.round(data.getRain() * 100) / 100 + "";
        this.description = setDescription(data.getWeather().get(0).getDescription());
        this.cloudiness = data.getClouds();
        this.windSpeed = (double) Math.round(data.getWindSpeed() * 36) / 10 + "km/h";
        this.humidity = data.getHumidity();
        this.uviIndex = data.getUvi() + "";
        this.pressure = data.getPressure();
        this.visibility = "10km";
    }

    private String setDescription(String description) {
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    private String setDate(long dt) {
        Locale.setDefault(new Locale("sl", "SI"));
        Instant instant = Instant.ofEpochSecond(dt);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E d.M");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getDay() {
        return day;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getUviIndex() {
        return uviIndex;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public String getVisibility() {
        return visibility;
    }
}
