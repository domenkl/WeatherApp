package si.uni_lj.fe.weatherapp.util;

public enum Forecast {

    HOURLY("forecast/hourly"),
    DAILY("forecast/daily"),
    CLIMATIC("forecast/climate");

    private final String apiPath;

    Forecast(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiPath() {
        return apiPath;
    }
}
