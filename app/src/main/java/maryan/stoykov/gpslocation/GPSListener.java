package maryan.stoykov.gpslocation;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider
 */
public class GPSListener implements LocationListener {
    Context context;
    LocationManager locationManager;
    Location location = null;
    Location lastGPSLocation = null;
    Location lastNetworkLocation = null;
    GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)

    LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i("GPSListener:", "GPS onLocationChanged event"+" "+location.getProvider());
            gpsListenerOnChange.onLocationSubmit(location);// emit event to sticky service
        }
    };
    LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i("NETListener:", "Network onLocationChanged event"+" "+location.getProvider());
            gpsListenerOnChange.onLocationSubmit(location);// emit event to sticky service
        }
    };
    LocationListener fusedLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i("FUSEDListener:", "FUSED onLocationChanged event"+" "+location.getProvider());
            gpsListenerOnChange.onLocationSubmit(location);// emit event to sticky service
        }
    };
    public GPSListener (Context context, GPSListenerOnChange gpsListenerOnChange){

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

//        Log.i("GPSListener:", "GPS onLocationChanged event"+" "+location.getProvider());
//
//        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)){
//            lastGPSLocation = location;
//        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
//                lastNetworkLocation = location;
//            }
//
//        if (this.location != null && chooseBetterLocation().equals(this.location)){
//            return;
//        } else {
//            this.location = chooseBetterLocation();
//        }
//
//        Log.i("GPSListener:", "GPS onLocationChanged provider "+" "
//                +this.location.getProvider());
//
//        gpsListenerOnChange.onLocationSubmit(this.location);// emit event to sticky service

    }

    /**
     * compares the accuracy of locations provided from
     * gps and network provider, returns the one with
     * better accuracy
     * @return location
     */
    Location chooseBetterLocation(){
        if (lastGPSLocation != null && lastNetworkLocation != null) {
            if (lastGPSLocation.getAccuracy() < lastNetworkLocation.getAccuracy()) {
                return lastGPSLocation;
            } else {
                return lastNetworkLocation;
            }
        } else if (lastGPSLocation != null) {
            return lastGPSLocation;
        } else if (lastNetworkLocation != null) {
            return lastNetworkLocation;
        } else {
            // Handle the case where no location data is available
            return null;
        }
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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000L * 10 * 1, 0f, networkLocationListener);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000L * 10 * 1, 0f, gpsLocationListener);

            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
                1000L * 10 * 1, 0f, fusedLocationListener);

        }

    public Location getLocation(){
        return this.location;
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void stopLocationUpdate(){
        locationManager.removeUpdates(networkLocationListener);
        locationManager.removeUpdates(gpsLocationListener);
        locationManager.removeUpdates(fusedLocationListener);
        locationManager = null;
    }

}
