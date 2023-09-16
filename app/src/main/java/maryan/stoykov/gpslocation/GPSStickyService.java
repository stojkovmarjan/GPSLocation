package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GPSStickyService extends Service
        implements  GPSListenerOnChange, PostLocationResponseListener {
    private final String className = this.getClass().getSimpleName();
    private GPSListener gpsListener;
    private PostLocationResponseListener postLocationResponseListener;
    private String serviceSignalMsg = "";
    private BootReceiver receiver;
    private ScheduledExecutorService scheduledLocationSender;
    private LocationDbRecord locationDbRecord;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new BootReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_REBOOT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        unregisterReceiver(receiver);

        Log.d(className,"SERVICE STOPPED BY USER");

        serviceSignalMsg = ServiceSignal.SERVICE_STOPPED_BY_USER;

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

        serviceSignalMsg = ServiceSignal.SERVICE_STARTED_ON_BOOT;

        // signal received from intent (context.startForegroundService(serviceIntent);)
        if (intent.hasExtra("SIGNAL")){

            serviceSignalMsg = Objects.requireNonNull(intent.getExtras()).getString("SIGNAL");

            //assert serviceSignalMsg != null;

            Log.i(className,"SIGNAL: "+serviceSignalMsg);

            if (gpsListener != null) {
                Log.d(className,"GPS NOT NULL");
                onLocationSubmit(gpsListener.getLocation(), serviceSignalMsg);
                gpsListener.stopLocationUpdate();
                gpsListener = null;
            }
        }

        gpsListener = new GPSListener(this, this);

        gpsListener.requestLocation();

        Log.d(className,"START");

        if (serviceSignalMsg.equals(ServiceSignal.SERVICE_STARTED_BY_USER)
                || serviceSignalMsg.equals(ServiceSignal.SERVICE_STARTED_ON_BOOT)){
            // TODO: here start the code that sends location data to Post class
            setScheduledLocationSender();
        }

        startForeground(1001, SetNotification().build(), FOREGROUND_SERVICE_TYPE_LOCATION );
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void setScheduledLocationSender(){

        scheduledLocationSender = Executors.newSingleThreadScheduledExecutor();

        scheduledLocationSender.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i(className, "SCHEDULED LOCATION SENDING");
                        try {
                            if (locationDbRecord != null){
                                if (!serviceSignalMsg.equals("")){
                                    locationDbRecord.setMessage(addServiceMsg());
                                }
                                PostLocation postLocation = new PostLocation(
                                        "https://pijo.linkpc.net/api/location",
                                        GPSStickyService.this);
                                postLocation.sendPost(locationDbRecord);
                            } else {
                                Log.i(className, "LOCATION IS NULL, EXITING SCHEDULED LOCATION SENDING");
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }, 0,1,TimeUnit.MINUTES
        );
    }

    private String addServiceMsg(){

        String returnMsg = serviceSignalMsg;

        if (!locationDbRecord.getMessage().equals("")){
            returnMsg = serviceSignalMsg+", "+locationDbRecord.getMessage();
        } else {
            returnMsg = serviceSignalMsg;
        }
        serviceSignalMsg = "";

        return returnMsg;
    }

//    private void processSignal(String signal){
//
//        if (signal.equals(ServiceSignal.USER_CHANGED_PARAMS)
//        && gpsListener != null) { // gps listener restart locations update request
//            Log.d(className,"STOP LOC UPD, gpsListener null");
//            gpsListener.stopLocationUpdate();
//            gpsListener = null;
//        }
//
//    }

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

//        if (!serviceSignalMsg.equals("")){
//            msg = serviceSignalMsg;
//            serviceSignalMsg = "";
//        }

        Log.i(className, "LOCATION CHANGED EVENT");

        Log.i(className, location.toString());

//        PostLocation postLocation = new PostLocation(
//                "https://pijo.linkpc.net/api/location", this);

         locationDbRecord = new LocationDbRecord(this, location, msg);

//        postLocation.sendPost(locationDbRecord);
    }

    @Override
    public void onHttpResponse(int responseCode, LocationDbRecord locationDbRecord) {

        Log.i(className,"Server responded with code "+responseCode);

        if (responseCode >= 200 && responseCode<300){
            if (locationDbRecord.getId() > -1){
                deleteDbRecord(locationDbRecord.getId());
            } else {
                postDbRecords();
            }
        } else {
            Log.e(className,"ENDPOINT NOT AVAILABLE");
            if (locationDbRecord.getId() == -1) writeToDb(locationDbRecord);
        }
    }
    private void postDbRecords(){

        List<LocationDbRecord> locationDbRecords;

        try (DBHelper dbHelper = new DBHelper(this)) {

            if (dbHelper.getRecordsCount() <= 0) return;

            Log.i(className, "DB has records");

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
            Log.i(className,"Location is added to local db!");
        } else {
            Log.e(className,"Write to db failed!");
        }
    }

    private void deleteDbRecord(Long id){
        Log.i(className,"Deleting a record!");
        int rowsDeleted = -1;
        try (DBHelper dbHelper = new DBHelper(this)) {
            rowsDeleted = dbHelper.deleteLocationRecord(id);
        }

        if (rowsDeleted == -1) {
            Log.e(className,"Deleting a record failed!");
        }
    }

}
