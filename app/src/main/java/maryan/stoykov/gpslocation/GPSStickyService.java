package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class GPSStickyService extends Service implements  GPSListenerOnChange{
    GPSListener gpsListener;
    String serviceSignalMsg = "";

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d("GPSStickyService","SERVICE DESTROY");

        serviceSignalMsg = "SERVICE IS DESTROYED MSG";

        onLocationSubmit(gpsListener.getLocation(),serviceSignalMsg);

        gpsListener.stopLocationUpdate();

        gpsListener = null;

        stopForeground(true);

    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceSignalMsg = "SERVICE IS STARTED ON BOOT";

        if (intent.hasExtra("SIGNAL")){
            serviceSignalMsg = intent.getExtras().getString("SIGNAL");
        }

        Context context = this;

        gpsListener = new GPSListener(this, this);

        gpsListener.requestLocation();

        Log.d("GPSStickyService","START");
        startForeground(1001, SetNotification().build(), FOREGROUND_SERVICE_TYPE_LOCATION );
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification.Builder SetNotification () {
        final String CHANNEL_ID = "My Foreground ID";

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);

        Notification.Builder notification = new Notification.Builder (this, CHANNEL_ID)
                .setContentText("Foreground service is running!")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);

        return notification;
    }

    /**
     * listens to location changes in GPSListener class
     * @param location, Location from location provider
     * @param msg, String, message in context of the location data
     */
    @Override
    public void onLocationSubmit(Location location, String msg) {

        Log.i("GPSStickyService", "LOCATION CHANGED EVENT");
        Log.i(
                "GPSStickyService", location.getLatitude()+", "
                        +location.getLongitude()+","
                        +location.getAccuracy()
        );

        Post post = new Post(this, "https://pijo.linkpc.net/api/location");
        if (serviceSignalMsg.equals("")){
            post.sendPost(location, msg);
        } else {
            post.sendPost(location, serviceSignalMsg);
            serviceSignalMsg = "";
        }
    }
}
