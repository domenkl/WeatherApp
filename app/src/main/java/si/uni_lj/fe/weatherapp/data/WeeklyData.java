package si.uni_lj.fe.weatherapp.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import si.uni_lj.fe.weatherapp.models.DailyInfo;

public class WeeklyData {

    private String day;
    private String date;
    private final String tempDay;
    private final String tempMorning;
    private final String weatherIcon;
    private final String description;
    private final String precipitation;

    public WeeklyData(DailyInfo dailyInfo) {
        setDayAndDate(dailyInfo.getDt());
        this.tempDay = (int) Math.round(dailyInfo.getTemperatures().getDay()) + "\u2103";
        this.tempMorning = (int) Math.round(dailyInfo.getTemperatures().getMorn()) + "\u2103";
        this.weatherIcon = dailyInfo.getWeather().get(0).getIcon();
        this.description = startDescription(dailyInfo.getWeather().get(0).getDescription());
        this.precipitation = (int) (dailyInfo.getPop() * 100) + "%";
    }

    private void setDayAndDate(long dt) {
        Locale.setDefault(new Locale("sl", "SI"));
        Instant instant = Instant.ofEpochSecond(dt);
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.M.");
        this.day = formatter.format(date);
        this.date = dateFormatter.format(date);
    }

    private String startDescription(String desc) {
        return desc.substring(0,1).toUpperCase() + desc.substring(1);
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
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

    public String getDescription() {
        return description;
    }

    public String getPrecipitation() {
        return precipitation;
    }
}
