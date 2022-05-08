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

}
