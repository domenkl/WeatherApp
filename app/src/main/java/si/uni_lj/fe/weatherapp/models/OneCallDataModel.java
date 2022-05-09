package si.uni_lj.fe.weatherapp.models;

import java.util.List;

@SuppressWarnings("unused")
public class OneCallDataModel {

    private double lat;
    private double lon;
    private String timezone;
    private HourlyInfo current;
    private List<HourlyInfo> hourly;
    private List<DailyInfo> daily;
    private List<Alert> alerts;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public HourlyInfo getCurrent() {
        return current;
    }

    public List<HourlyInfo> getHourly() {
        return hourly;
    }

    public List<DailyInfo> getDaily() {
        return daily;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }
}
