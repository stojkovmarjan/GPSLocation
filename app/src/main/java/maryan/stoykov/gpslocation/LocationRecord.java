package maryan.stoykov.gpslocation;

import android.location.Location;

public class LocationRecord {
    private Location location;
    private String message;

    public LocationRecord() {

    }

    // Constructor to initialize all fields
    public LocationRecord(Location location,
                          String message) {
        this.location = location;
        this.message = message;
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

