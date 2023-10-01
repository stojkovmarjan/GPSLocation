package maryan.stoykov.gpslocation.Models;

public class LocationResponse {
    private String time;
    private String deviceId;
    private double latitude;
    private double longitude;
    private float accuracy;
    private String provider;
    private String message;
    private int batteryLevel;
    private String timeZone;
    private int tZoneOffset;

    @Override
    public String toString() {
        return "LocationResponse{" +
                "time='" + time + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", provider='" + provider + '\'' +
                ", message='" + message + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", timeZone='" + timeZone + '\'' +
                ", tZoneOffset=" + tZoneOffset +
                '}';
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void settZoneOffset(int tZoneOffset) {
        this.tZoneOffset = tZoneOffset;
    }

    public String getTime() {
        return time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public String getMessage() {
        return message;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public int gettZoneOffset() {
        return tZoneOffset;
    }

}
