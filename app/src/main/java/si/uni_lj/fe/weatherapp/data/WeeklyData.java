package si.uni_lj.fe.weatherapp.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.models.DailyInfo;

public class WeeklyData {

    private final String day;
    private final String tempDay;
    private final String tempMorning;
    private final String weatherIcon;
    private final String cloudiness;
    private final String precipitation;
    private final String wind;

    public WeeklyData(DailyInfo dailyInfo) {
        this.day = setDay(dailyInfo.getDt());
        this.tempDay = (int) Math.round(dailyInfo.getTemperatures().getDay()) + "\u2103";
        this.tempMorning = (int) Math.round(dailyInfo.getTemperatures().getMorn()) + "\u2103";
        this.weatherIcon = dailyInfo.getWeather().get(0).getIcon();
        this.cloudiness = dailyInfo.getClouds() + "%";
        this.precipitation = (int)(dailyInfo.getPop() * 100) + "%";
        this.wind = (double) Math.round(dailyInfo.getWindSpeed() * 36) / 10 + "";
    }

    @SuppressWarnings("newApi")
    private String setDay(long dt) {
        Locale.setDefault(new Locale("sl", "SI"));
        Instant instant = Instant.ofEpochSecond(dt);
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd.MM");
        return formatter.format(date);
    }

    public String getDay() {
        return day;
    }

    public String getTempDay() {
        return tempDay;
    }

    public String getTempMorning() {
        return tempMorning;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getWind() {
        return wind;
    }
}
