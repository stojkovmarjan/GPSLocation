package maryan.stoykov.gpslocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

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

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider
 */
public class GPSListener  {
    private final String className = this.getClass().getSimpleName();
    private final Context context;
    private LocationManager locationManager;
    private Location location = null;
    private final Long updateTime = 1000L*60*1;
    private final Long minUpdateTime = updateTime / 3;
    private final float minUpdateDistance = 4.5f;
    public Location getLocation(){
        return this.location;
    }
    private void setLocation(Location location) {
        this.location = location;
    }

    GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)
    private final FusedLocationProviderClient fusedLocationClient;
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

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
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

//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                updateTime, updateDistance, this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                LocationRequest locationRequest = new LocationRequest.Builder(updateTime)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setMinUpdateIntervalMillis(minUpdateTime)
                        .setMinUpdateDistanceMeters(minUpdateDistance)
                        .setGranularity(Granularity.GRANULARITY_FINE)
                        .build();

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }

        }
    public void stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
