package maryan.stoykov.gpslocation;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.PowerManager;

import java.util.TimeZone;

public class DeviceStatus {

    private static final String TAG = "DeviceStatus";

    protected static int getBatteryLevel(Context context) {
        BatteryManager batteryManager =
                (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    protected static boolean isWifiEnabled(Context context) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network network = connectivityManager.getActiveNetwork();

            if (network != null) {

                NetworkCapabilities networkCapabilities =
                        connectivityManager.getNetworkCapabilities(network);

                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                } else {
                    return false;
                }

            } else {
                return false;
            }
    }

    protected static boolean isMobileNetworkInternetEnabled(Context context) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network network = connectivityManager.getActiveNetwork();

            if (network != null) {
                NetworkCapabilities networkCapabilities =
                        connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                } else {
                    return false;
                }
            } else {
                return false;
            }

    }

    // Get the device's current time zone
    protected static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }
    // Returns the time zone offset in hours.
    protected static int getTimeZoneOffsetInHours() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getOffset(System.currentTimeMillis()) / (1000 * 60 * 60);
    }

    protected static boolean isLocationEnabled(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    protected static boolean hasLocationPermission(Context context) {
        return
                context.checkSelfPermission(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean isPowerSaveOn(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isPowerSaveMode();
    }
}

