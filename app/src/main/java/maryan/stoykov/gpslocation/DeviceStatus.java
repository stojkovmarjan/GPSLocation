package maryan.stoykov.gpslocation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;

import java.util.TimeZone;

public class DeviceStatus {

    private static final String TAG = "DeviceStatus";

    public static int getBatteryLevel(Context context) {
        BatteryManager batteryManager =
                (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    public static boolean isWifiEnabled(Context context) {

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

    public static boolean isMobileNetworkInternetEnabled(Context context) {

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
    public static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }
    // Returns the time zone offset in hours.
    public static int getTimeZoneOffsetInHours() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getOffset(System.currentTimeMillis()) / (1000 * 60 * 60);
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean hasLocationPermission(Context context) {
        return
                context.checkSelfPermission(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }
}

