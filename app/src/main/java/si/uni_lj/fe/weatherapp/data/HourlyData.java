package si.uni_lj.fe.weatherapp.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import si.uni_lj.fe.weatherapp.models.HourlyInfo;

public class HourlyData {

    private final String time;
    private final String temperature;
    private final String weatherIcon;
    private final String precipitation;

    public HourlyData(HourlyInfo info, ZoneId zoneId) {
        time = setTime(info.getDt(), zoneId);
        temperature = (int) Math.round(info.getTemp()) + "\u2103";
        weatherIcon = info.getWeather().get(0).getIcon();
        precipitation = setPrecipitation(info);
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    private String setTime(long dt, ZoneId zoneId) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Instant instant = Instant.ofEpochSecond(dt);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return dateTimeFormatter.format(localDateTime);
    }

    private String setPrecipitation(HourlyInfo info) {
        if (info.getRain() != null) return info.getRain().getLastHour() + "";
        if (info.getSnow() != null) return info.getSnow().getLastHour() + "";
        return "0.0";
    }
}
