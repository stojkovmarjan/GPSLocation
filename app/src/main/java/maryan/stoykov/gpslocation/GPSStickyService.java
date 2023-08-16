package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GPSStickyService extends Service implements  GPSListenerOnChange{

    Handler handler;
    Runnable runnable;
    @Override
    public void onDestroy() {

        Log.e("SERVICE STOP","DESTROY");

       handler.removeCallbacks(runnable);

        handler.removeCallbacksAndMessages(null);

        handler = null;

        super.onDestroy();

    }

    @Override
    public boolean stopService(Intent name) {
        Log.e("SERVICE STOP","STOP");
        return super.stopService(name);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GPSListener gpsListener = new GPSListener(getApplicationContext(), this);

        handler = new Handler();

        String signal = "";
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("RUNNABLE", "Runnable is running");

                gpsListener.requestLocation();

                if (handler != null) {
                    handler.postDelayed(this,1000*20*1);
                }

            }
        };

        Bundle extras = intent.getExtras();


        if (intent.hasExtra("SIGNAL")) signal = extras.getString("SIGNAL");

        Log.e("SIGNAL", signal);

        if (signal.equals("STOP")) {
            Log.e("SERVICE STOP","STOP SIGNAL");
            gpsListener.stopLocationUpdate();

            stopForeground(true);
            //stopService(intent);
            stopSelfResult(1001);
            stopSelf();
            handler.removeCallbacks(runnable);
            return START_NOT_STICKY;

        } else {
            handler.postDelayed(runnable,2000);
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
        Log.e(
                "Location changed", location.getLatitude()+", "
                        +location.getLongitude()+","
                        +location.getAccuracy()
        );

        // LocationSaver locationSaver = new LocationSaver();
        // locationSaver.SaveFile("assdsddsd");
        sendPost(location);
    }

    public void sendPost(Location location) {

        @SuppressLint("HardwareIds")
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://msvs.ddnsfree.com/api/location");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("deviceId", deviceId);
                    jsonParam.put("latitude", location.getLatitude());
                    jsonParam.put("longitude", location.getLongitude());
                    jsonParam.put("accuracy",location.getAccuracy());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
