package maryan.stoykov.gpslocation;

import android.location.Location;

/**
 * interface is used as a location change event emitter from GPSListener
 */
public interface GPSListenerOnChange {
    public void onLocationSubmit(Location location);
}
