package si.uni_lj.fe.weatherapp.models;

public class RainOccurrence {

    private int rainId;
    private long start;
    private long end;
    private double precipitation;

    public RainOccurrence(int rainId, long start, long end, double precipitation) {
        this.rainId = rainId;
        this.start = start;
        this.end = end;
        this.precipitation = precipitation;
    }

    public int getRainId() {
        return rainId;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setRainId(int rainId) {
        this.rainId = rainId;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }
}
