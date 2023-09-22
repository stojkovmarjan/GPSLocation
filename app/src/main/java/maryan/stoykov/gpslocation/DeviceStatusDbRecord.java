package maryan.stoykov.gpslocation;

public class DeviceStatusDbRecord {
    private int batteryLevel;
    private int tZoneOffset;
    private String timeZone;

    public DeviceStatusDbRecord(int batteryLevel, int tZoneOffset, String timeZone) {
        this.batteryLevel = batteryLevel;
        this.tZoneOffset = tZoneOffset;
        this.timeZone = timeZone;
    }

    // Getter for batteryLevel
    public int getBatteryLevel() {
        return batteryLevel;
    }

    // Setter for batteryLevel
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    // Getter for tZoneOffset
    public int getTZoneOffset() {
        return tZoneOffset;
    }

    // Setter for tZoneOffset
    public void setTZoneOffset(int tZoneOffset) {
        this.tZoneOffset = tZoneOffset;
    }

    // Getter for timeZone
    public String getTimeZone() {
        return timeZone;
    }

    // Setter for timeZone
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
