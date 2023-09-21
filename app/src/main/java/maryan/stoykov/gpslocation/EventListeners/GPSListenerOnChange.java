package maryan.stoykov.gpslocation.EventListeners;

import android.location.Location;

/**
 * interface is used as a location change event emitter from GPSListener
 */
public interface GPSListenerOnChange {
    public void onLocationSubmit(Location location, String message);
}
