package si.uni_lj.fe.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Alert {

    @SerializedName("sender_name")
    private String sender;
    private String event;
    private long start;
    private long end;
    private String description;
    private List<String> tags;

    public String getSender() {
        return sender;
    }

    public String getEvent() {
        return event;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }
}
