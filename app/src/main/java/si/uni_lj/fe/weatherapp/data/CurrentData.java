package si.uni_lj.fe.weatherapp.data;

import si.uni_lj.fe.weatherapp.models.CurrentDataModel;

@SuppressWarnings("unused")
public class CurrentData {

    private String name;
    private int timezone;
    private String icon;
    private String description;
    private String main;
    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
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
        this.temperature = data.getData().getTemp();
        this.feelsLike = data.getData().getFeelsLike();
        this.tempMin = data.getData().getTempMin();
        this.tempMax = data.getData().getTempMax();
        this.pressure = data.getData().getPressure();
        this.humidity = data.getData().getHumidity();
        this.windSpeed = data.getWind().getSpeed();
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
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
