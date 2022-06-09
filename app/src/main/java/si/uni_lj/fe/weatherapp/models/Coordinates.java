package si.uni_lj.fe.weatherapp.models;

import com.google.gson.annotations.SerializedName;

public class Coordinates {

    @SerializedName(value = "lon", alternate = "longitude")
    private final double longitude;

    @SerializedName(value = "lat", alternate = "latitude")
    private final double latitude;

    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
