package maryan.stoykov.gpslocation;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider
 */
public class GPSListener implements LocationListener {
    private final String className = this.getClass().getSimpleName();
    private final Context context;
    private LocationManager locationManager;
    private Location location = null;
    private Location lastGPSLocation = null;
    private Location lastNetworkLocation = null;
    private Location lastFusedLocation = null;
    GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)
    public GPSListener (Context context, GPSListenerOnChange gpsListenerOnChange){

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.i(className, "GPS onLocationChanged event"+" "+location.getProvider());

        if (LocationManager.GPS_PROVIDER.equals(location.getProvider())){
            lastGPSLocation = location;
        } else if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())){
                lastNetworkLocation = location;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (LocationManager.FUSED_PROVIDER.equals(location.getProvider())){
                this.lastFusedLocation = location;
            }
        }

        if (this.location != null && chooseBetterLocation().equals(this.location)){
            Log.i(className, "GPS onLocationChanged same as last "+" "
                    +this.location.getProvider());
            return;
        } else {
            this.location = chooseBetterLocation();
        }

        if (this.location == null) this.location = location;

        Log.i(className, "GPS onLocationChanged best provider "+" "
                +this.location.getProvider());

        gpsListenerOnChange.onLocationSubmit(this.location, "");// emit event to sticky service

    }

    /**
     * compares the accuracy of locations provided from
     * gps, fused and network provider, returns the one with
     * better accuracy
     * @return location
     */
    Location chooseBetterLocation(){

        Location chosenLocation = null;

        if (lastGPSLocation != null && lastNetworkLocation != null) {
            if (lastGPSLocation.getAccuracy() < lastNetworkLocation.getAccuracy()) {
                chosenLocation = lastGPSLocation;
            } else {
                chosenLocation = lastNetworkLocation;
            }
        } else if (lastGPSLocation != null) {
            chosenLocation = lastGPSLocation;
        } else if (lastNetworkLocation != null){
            chosenLocation = lastNetworkLocation;
        }

        if (lastFusedLocation != null && chosenLocation != null
                && chosenLocation.getAccuracy() > lastFusedLocation.getAccuracy()){
                chosenLocation = lastFusedLocation;
        } if (chosenLocation == null) {
            chosenLocation = lastNetworkLocation;
        }

        return chosenLocation;
    }

    protected void requestLocation() {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000L * 30 * 1, 0f, this);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000L * 30 * 1, 0f, this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
                        1000L * 30 * 1, 0f, this);
            }

        }

    public Location getLocation(){
        return this.location;
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.i(className,provider+" enabled");
        //LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.i(className,provider+" disabled");
        //LocationListener.super.onProviderDisabled(provider);
    }

    public void stopLocationUpdate(){
        locationManager.removeUpdates(this);
        locationManager = null;
    }

}
