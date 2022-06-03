package si.uni_lj.fe.weatherapp.data;

import java.time.LocalTime;

@SuppressWarnings("unused")
public class AlertData {

    private int id;
    private LocalTime time;
    private String city;
    private boolean isDaily;
    private boolean isActive;

    public AlertData(int id, LocalTime time, String city, boolean isDaily, boolean isActive) {
        this.id = id;
        this.time = time;
        this.city = city;
        this.isDaily = isDaily;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isDaily() {
        return isDaily;
    }

    public void setDaily(boolean daily) {
        isDaily = daily;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
