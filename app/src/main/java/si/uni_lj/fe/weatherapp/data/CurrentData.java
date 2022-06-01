package si.uni_lj.fe.weatherapp.data;

import android.util.Log;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import si.uni_lj.fe.weatherapp.models.HourlyInfo;
import si.uni_lj.fe.weatherapp.models.OneCallDataModel;

public class CurrentData {

    private final String city;
    private final String country;
    private final String localTime;
    private final String icon;
    private final String description;
    private final int temperature;
    private final int pressure;
    private final int humidity;
    private final String windSpeed;
    private final int cloudiness;
    private final String precipitation;
    private final String uviIndex;
    private final String visibility;

    public CurrentData(OneCallDataModel oneCallData, String city, String country) {
        HourlyInfo data = oneCallData.getCurrent();
        this.city = city;
        this.country = country;
        this.localTime = setLocalTime(oneCallData.getTimezone());
        this.temperature = (int) Math.round(data.getTemp());
        this.icon = data.getWeather().get(0).getIcon();
        this.precipitation = setPrecipitation(data);
        this.description = setDescription(data.getWeather().get(0).getDescription());
        this.cloudiness = data.getCloudiness();
        this.windSpeed = (double) Math.round(data.getWindSpeed() * 36) / 10 + "km/h";
        this.humidity = data.getHumidity();
        this.uviIndex = data.getUviIndex() + "";
        this.visibility = (double)(data.getVisibility() / 100) / 10 + "";
        this.pressure = data.getPressure();
    }

    private String setDescription(String description) {
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    private String setLocalTime(String timezone) {
        Log.i("Timezone", timezone);
        ZoneId zoneId = ZoneId.of(timezone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Instant instant = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return dateTimeFormatter.format(localDateTime);
    }

    private String setPrecipitation(HourlyInfo info) {
        if (info.getRain() != null) return info.getRain().getLastHour() + "";
        if (info.getSnow() != null) return info.getSnow().getLastHour() + "";
        return "0.0";
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getLocalTime() {
        return localTime;
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

    public String getVisibility() {
        return visibility;
    }
}
