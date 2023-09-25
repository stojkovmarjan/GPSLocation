package maryan.stoykov.gpslocation;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;

import maryan.stoykov.gpslocation.EventListeners.GPSListenerOnChange;

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider for android >=11
 * and from network or gps for android 10
 */
public class GPSListener implements LocationListener {
    private final String className = this.getClass().getSimpleName();
    private final Context context;
    private final LocationManager locationManager;
    private Location location = null;
    private Long updateInterval = 1000L*60*5;
    private Long minUpdateInterval = updateInterval / 3;
    private float minUpdateDistance = 5f;
    private Location lastGPSLocation = null;
    private Location lastNetworkLocation = null;
    public Location getLocation(){
        return this.location;
    }
    private void setLocation(Location location) {
        this.location = location;
    }

    private final GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)
    private FusedLocationProviderClient fusedLocationClient;

    // listeners for FusedLocationProviderClient
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            setLocation(locationResult.getLastLocation());
            gpsListenerOnChange.onLocationSubmit(locationResult.getLastLocation(),"");
        }
    };
    public GPSListener (Context context, GPSListenerOnChange gpsListenerOnChange){

        Log.d(className,className+" constructor");
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    @SuppressLint("WakelockTimeout")
    protected void requestLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
        }

        updateInterval = LocationParams.getUpdateInterval(context)*1000L;
        minUpdateInterval = LocationParams.getMinUpdateInterval(context)*1000L;
        minUpdateDistance = LocationParams.getMinUpdateDistance(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            startUsingFusedProvider();
        } else {
            startUsingNetworkProvider();
            startUsingGpsProvider();
        }

    }

    /* permission is checked in requestLocation() method before the next 3 methods are called,
    should be ok for now, ignore @SuppressLint("MissingPermission")
     */
    @SuppressLint("MissingPermission")
    private void startUsingNetworkProvider(){
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, updateInterval, minUpdateDistance, this);
    }
    @SuppressLint("MissingPermission")
    private void startUsingGpsProvider(){
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, updateInterval, minUpdateDistance, this);
    }

    @SuppressLint("MissingPermission")
    public void startUsingFusedProvider(){

        Log.i(className,"updateInterval "+updateInterval);
        Log.i(className,"min updateInterval "+minUpdateInterval);
        Log.i(className,"updateDistance "+minUpdateDistance);

        LocationRequest locationRequest = new LocationRequest.Builder(updateInterval)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(minUpdateInterval-3000)
                .setMinUpdateDistanceMeters(minUpdateDistance)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setMaxUpdateDelayMillis(0)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /* listener for GPS and Network provider is SDK < S
     won't call chooseBetterLocation until both network and gps location are obtained
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.i("GPSListener:", "GPS onLocationChanged event"+" "+location.getProvider());

        if (LocationManager.GPS_PROVIDER.equals(location.getProvider())){
            lastGPSLocation = location;
        } else if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())){
            lastNetworkLocation = location;
        }

        if (this.location != null && chooseBetterLocation().equals(this.location)){
            Log.i("GPSListener:", "GPS onLocationChanged same as last "+" "
                    +this.location.getProvider());
            return;
        } else {
            this.location = chooseBetterLocation();
        }

        if (this.location == null) this.location = location;

        Log.i("GPSListener:", "GPS onLocationChanged best provider "+" "
                +this.location.getProvider());

        gpsListenerOnChange.onLocationSubmit(this.location, "");// emit event to sticky service
    }

    // choose between Network and Gps provided locations
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

        return chosenLocation;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            fusedLocationClient.flushLocations();
            fusedLocationClient.removeLocationUpdates(locationCallback);
            fusedLocationClient = null;
        } else {
            locationManager.removeUpdates(this);
        }
    }
}
