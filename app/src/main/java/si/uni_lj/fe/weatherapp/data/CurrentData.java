package si.uni_lj.fe.weatherapp.data;

import si.uni_lj.fe.weatherapp.models.CurrentDataModel;

@SuppressWarnings("unused")
public class CurrentData {

    private String name;
    private int timezone;
    private String icon;
    private String description;
    private String main;
    private int temperature;
    private int feelsLike;
    private int tempMin;
    private int tempMax;
    private int pressure;
    private int humidity;
    private double windSpeed;
    private int cloudiness;
    private String country;
    private long sunrise;
    private long sunset;

    public CurrentData(CurrentDataModel data) {
        this.name = data.getName();
        this.timezone = data.getTimezone();
        this.icon = data.getWeather().get(0).getIcon();
        this.description = data.getWeather().get(0).getDescription();
        this.main = data.getWeather().get(0).getMain();
        this.temperature = (int) Math.round(data.getData().getTemp());
        this.feelsLike = (int) data.getData().getFeelsLike();
        this.tempMin = (int) data.getData().getTempMin();
        this.tempMax = (int) data.getData().getTempMax();
        this.pressure = data.getData().getPressure();
        this.humidity = data.getData().getHumidity();
        this.windSpeed = (double) Math.round(data.getWind().getSpeed() * 36) / 10;
        this.cloudiness = data.getClouds().getCloudiness();
        this.country = data.getSys().getCountry();
        this.sunrise = data.getSys().getSunrise();
        this.sunset = data.getSys().getSunset();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(int feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }
}
