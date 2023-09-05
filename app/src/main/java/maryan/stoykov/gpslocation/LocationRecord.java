package maryan.stoykov.gpslocation;

import android.location.Location;

import java.util.Date;

public class LocationRecord {
    private Date date;
    private String time;
    private Location location;
    private String message;

    // Empty constructor
    public LocationRecord() {
        // Default constructor with no arguments
    }

    // Constructor to initialize all fields
    public LocationRecord(Date locationDate, String locationTime, Location location, String message, float accuracy) {
        this.date = locationDate;
        this.time = locationTime;
        this.location = location;
        this.message = message;
    }

    // Getter and setter for LocationDate
    public Date getLocationDate() {
        return date;
    }

    public void setLocationDate(Date locationDate) {
        this.date = locationDate;
    }

    // Getter and setter for LocationTime
    public String getLocationTime() {
        return time;
    }

    public void setLocationTime(String locationTime) {
        this.time = locationTime;
    }

    // Getter and setter for GPSLocation
    public Location Location() {
        return location;
    }

    public void setGpsLocation(Location location) {
        this.location = location;
    }

    // Getter and setter for Message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

