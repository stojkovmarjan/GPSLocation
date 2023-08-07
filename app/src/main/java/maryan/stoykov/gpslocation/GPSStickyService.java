package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class GPSStickyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // GpsProvider gpsProvider = new GpsProvider(getApplicationContext());
        GPSListener gpsListener = new GPSListener((getApplicationContext()));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("SERVICE", "Service is running");
                gpsListener.requestLocation();
                handler.postDelayed(this,2000);
            }
        },2000);
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            Log.e ("Service", "Service is running");
//                            try {
//                                Thread.sleep(10000);
//                                Location location = gpsProvider.getLocation();
//                                Log.e("lat: ", String.valueOf(location.getLatitude()));
//                                Log.e("long: ",String.valueOf(location.getLongitude()));
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//        ).start();

        //startForeground(1001, SetNotification().build() );
        startForeground(1001, SetNotification().build(), FOREGROUND_SERVICE_TYPE_LOCATION );
        return super.onStartCommand(intent, flags, startId);
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
}
