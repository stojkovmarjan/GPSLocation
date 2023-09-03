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
    @Override
    public void onDestroy() {
        Log.d("GPSStickyService","SERVICE DESTROY");
        gpsListener = null;
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("GPSStickyService","SERVICE STOP");
        return super.stopService(name);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = this;

        gpsListener = new GPSListener(getApplicationContext(), this);

        gpsListener.requestLocation();

        String signal = "";

        Bundle extras = intent.getExtras();

        if (intent.hasExtra("SIGNAL")) signal = extras.getString("SIGNAL");

        Log.d("GPSStickyService", "SIGNAL RECEIVED "+signal);

        if (signal.equals("STOP")) {

                Log.d("GPSStickyService","STOP SIGNAL");
                gpsListener.stopLocationUpdate();
                stopForeground(true);
                stopSelfResult(1001);
                stopSelf();

                return START_NOT_STICKY;// not working this way!

        } else {
            Log.d("GPSStickyService","START");
            startForeground(1001, SetNotification().build(), FOREGROUND_SERVICE_TYPE_LOCATION );
        }

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
     * @param location
     */
    @Override
    public void onLocationSubmit(Location location) {
        Log.i("GPSStickyService", "LOCATION CHANGED EVENT");
        Log.i(
                "GPSStickyService", location.getLatitude()+", "
                        +location.getLongitude()+","
                        +location.getAccuracy()
        );

        Post post = new Post(this, "https://msvs.ddnsfree.com/api/location");
        post.sendPost(location);
    }

}
