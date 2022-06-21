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
    private final int minTemperature;
    private final int pressure;
    private final int humidity;
    private final String windSpeed;
    private final int cloudiness;
    private final String precipitation;
    private final String uviIndex;
    private final String sunrise;
    private final String sunset;
    private final int rain;

    public DailyData(DailyInfo data, String city, String country, String timezone) {
        this.city = city;
        this.country = country;
        this.sunrise = setTime(data.getSunrise(), timezone);
        this.sunset = setTime(data.getSunset(), timezone);
        this.day = setDate(data.getDt());
        this.temperature = (int) Math.round(data.getTemperatures().getDay());
        this.minTemperature = (int) Math.round(data.getTemperatures().getMin());
        this.icon = data.getWeather().get(0).getIcon();
        this.precipitation = (double) Math.round(data.getRain() * 100) / 100 + "";
        this.description = setDescription(data.getWeather().get(0).getDescription());
        this.cloudiness = data.getClouds();
        this.windSpeed = (double) Math.round(data.getWindSpeed() * 36) / 10 + "km/h";
        this.humidity = data.getHumidity();
        this.uviIndex = data.getUvi() + "";
        this.pressure = data.getPressure();
        this.rain = (int) (data.getPop() * 100);
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

    private String setTime(long time, String timezone) {
        Instant instant = Instant.ofEpochSecond(time);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));
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

    public int getMinTemperature() {
        return minTemperature;
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

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public int getRain() {
        return rain;
    }

    public String getRecommendation() {

        int temperature = getTemperature();
        int rain = getRain();
        String temprec = "Uživajte v prijetnem dnevu";
        //String temprec = null;
        if (temperature <= 17) {
            temprec = "Hladno bo, vzemite jakno!";
        }
        if (temperature >= 27) {
            temprec = "Zelo visoke temperature, izogibajte se soncu!";
        }
        if (rain > 25) {
            temprec = "Visoka možnost padavin, vzemite dežnik!";
        }
        if (temperature <= 17 && rain > 25) {
            temprec = "Hladno bo z visoko možnostjo padavin. Ne pozabite jakne ter dežnika!";
        }
        if (temperature >= 27 && rain > 25) {
            temprec = "Visoke temperature ter možnost padavin. Izogibajte se soncu ter imejte s sabo dežnik.";
        }
        return temprec;

    }
}
