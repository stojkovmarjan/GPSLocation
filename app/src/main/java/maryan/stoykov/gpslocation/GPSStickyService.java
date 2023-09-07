package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class GPSStickyService extends Service
        implements  GPSListenerOnChange, PostLocationResponseListener {
    GPSListener gpsListener;
    PostLocationResponseListener postLocationResponseListener;
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

        serviceSignalMsg = "SERVICE IS STARTED ON DEVICE BOOT";

        if (intent.hasExtra("SIGNAL")){
            serviceSignalMsg = intent.getExtras().getString("SIGNAL");
        }

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

        return new Notification.Builder (this, CHANNEL_ID)
                .setContentText("Foreground service is running!")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);
    }

    @Override
    public void onLocationSubmit(Location location, String msg) {

        if (!serviceSignalMsg.equals("")){
            msg = serviceSignalMsg;
            serviceSignalMsg = "";
        }

        Log.i("GPSStickyService", "LOCATION CHANGED EVENT");

        Log.i("GPSStickyService", location.toString());

        PostLocation postLocation = new PostLocation(
                "https://pijo.linkpc.net/api/location", this);

        LocationDbRecord locationDbRecord = new LocationDbRecord(this, location, msg);

        postLocation.sendPost(locationDbRecord);

    }

    private void postDbRecords(){

        List<LocationDbRecord> locationDbRecords;

        try (DBHelper dbHelper = new DBHelper(this)) {

            if (dbHelper.getRecordsCount() <= 0) return;

            Log.i("POST CLASS", "DB has records");

            locationDbRecords = dbHelper.getLocationsList();
        }

        for (LocationDbRecord locationDbRecord: locationDbRecords ) {
            PostLocation postLocation = new PostLocation(
                    "https://pijo.linkpc.net/api/location", this);
            postLocation.sendPost(locationDbRecord);
        }

    }

    private void writeToDb(LocationDbRecord locationDbRecord){

        Long rowId;

        try (DBHelper db = new DBHelper(this)) {

            rowId = db.addLocation(locationDbRecord);
        }

        if (rowId > -1) {
            Log.i("GPSStickyService","Location is added to local db!");
        } else {
            Log.e("GPSStickyService","Write to db failed!");
        }
    }

    private void deleteDbRecord(Long id){
        try (DBHelper dbHelper = new DBHelper(this)) {
            dbHelper.deleteLocationRecord(id);
        }
    }

    @Override
    public void onHttpResponse(int responseCode, LocationDbRecord locationDbRecord) {
        switch (responseCode){
            case 200:
                Log.i("GPSStickyService","Response reseived "+responseCode
                        +"\nData sent to server!");
                if (locationDbRecord.getId() > -1){
                    deleteDbRecord(locationDbRecord.getId());
                } else {
                    postDbRecords();
                }
                break;
            case 400:
                Log.e("GPSStickyService","ENDPOINT NOT AVAILABLE");
                Log.i("GPSStickyService","Response reseived "+responseCode);
                if (locationDbRecord.getId() == -1) writeToDb(locationDbRecord);
                break;
        }
    }
}
