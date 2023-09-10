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

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider
 */
public class GPSListener implements LocationListener {
    private final String className = this.getClass().getSimpleName();
    private final Context context;
    private LocationManager locationManager;
    private Location location = null;
    private final Long updateTime = 1000L*60*1;
    private final Long minUpdateTime = updateTime / 3;
    private final float minUpdateDistance = 0f;
    private Location lastGPSLocation = null;
    private Location lastNetworkLocation = null;
    public Location getLocation(){
        return this.location;
    }
    private void setLocation(Location location) {
        this.location = location;
    }

    private final GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)
    private final FusedLocationProviderClient fusedLocationClient;

    // listeners for FusedLocationProviderClient
    private final LocationCallback locationCallback  = new LocationCallback() {
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

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    protected void requestLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
        }

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
                LocationManager.NETWORK_PROVIDER, updateTime, minUpdateDistance, this);
    }
    @SuppressLint("MissingPermission")
    private void startUsingGpsProvider(){
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, updateTime, minUpdateDistance, this);
    }

    @SuppressLint("MissingPermission")
    public void startUsingFusedProvider(){
        LocationRequest locationRequest = new LocationRequest.Builder(updateTime)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(minUpdateTime)
                .setMinUpdateDistanceMeters(minUpdateDistance)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /* listener for GPS and Network provider
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
            fusedLocationClient.removeLocationUpdates(locationCallback);
        } else {
            locationManager.removeUpdates(this);
        }
    }
}
