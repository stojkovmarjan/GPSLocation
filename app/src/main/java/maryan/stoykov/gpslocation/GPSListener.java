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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

/**
 * provides the main service with location data
 * taken from the FUSED LOCATION provider
 */
public class GPSListener implements LocationListener {
    Context context;
    LocationManager locationManager;
    Location location;
    GPSListenerOnChange gpsListenerOnChange; // event listener ( for sticky service only for now)
    public GPSListener (Context context, GPSListenerOnChange gpsListenerOnChange){

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.gpsListenerOnChange = gpsListenerOnChange; // event emitter / listener

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // TODO: if sdk>29 submit location to event handler
        this.location = location;
        Log.i("GPSListener:", "GPS onLocationChanged event"+" "+location.getProvider());
        gpsListenerOnChange.onLocationSubmit(location);// emit event to sticky service
        // TODO: if sdk<30 compare last locations from GPS and NETWORK and choose the better one

    }

    protected void requestLocation() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

            // if SDK > 30 then request location update from FUSED_PROVIDER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
                        1000L * 10 *1, 0f, this);
            } else {
                // if sdk < 31 then request location update from GPS and NETWORK_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000L * 10 *1, 0f, this);

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        1000L * 10 *1, 0f, this);
            }


        } else {
            Log.d("GPSListener:", "GPS NOT ENABLED!");
        }

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
        locationManager.removeUpdates(this);
    }
}
