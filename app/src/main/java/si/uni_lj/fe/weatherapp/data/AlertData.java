package si.uni_lj.fe.weatherapp.data;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class AlertData {

    private int id;
    private LocalDateTime dateTime;
    private String date;
    private String time;
    private String city;
    private boolean isActive;

    public AlertData(int id, LocalDateTime dateTime, String date, String time, String city, boolean isActive) {
        this.id = id;
        this.dateTime = dateTime;
        this.date = date;
        this.time = "Ob " + time;
        this.city = city;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
