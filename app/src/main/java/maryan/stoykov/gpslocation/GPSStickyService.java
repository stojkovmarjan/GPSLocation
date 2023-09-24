package maryan.stoykov.gpslocation;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import maryan.stoykov.gpslocation.BroadcastReceivers.BatteryChangedReceiver;
import maryan.stoykov.gpslocation.BroadcastReceivers.BootReceiver;
import maryan.stoykov.gpslocation.BroadcastReceivers.DeepSleepReceiver;
import maryan.stoykov.gpslocation.BroadcastReceivers.PowerSaverReceiver;
import maryan.stoykov.gpslocation.EventListeners.GPSListenerOnChange;
import maryan.stoykov.gpslocation.EventListeners.PostLocationResponseListener;

public class GPSStickyService extends Service
        implements GPSListenerOnChange, PostLocationResponseListener {
    private final String className = this.getClass().getSimpleName();
    private GPSListener gpsListener;
    private String serviceSignalMsg = "";
    private BootReceiver bootReceiver;
    private DeepSleepReceiver deepSleepReceiver;
    private PowerManager.WakeLock wakeLock;
    protected static final int SERVICE_NOTIFICATION_ID = 11001;
    protected static final int POWER_SAVE_NOTIFICATION_ID = 11002;
    private BatteryChangedReceiver batteryChangedReceiver;
    private PowerSaverReceiver powerSaverReceiver;

    @SuppressLint("WakelockTimeout")
    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, className+":WakeLock"
        );
        wakeLock.acquire();

        gpsListener = new GPSListener(this, this);
        gpsListener.requestLocation();

        Log.d(className,"SERVICE ON CREATE");
        bootReceiver = new BootReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_REBOOT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(bootReceiver, filter);

        powerSaverReceiver = new PowerSaverReceiver();
        registerReceiver(powerSaverReceiver, new IntentFilter(
                PowerManager.ACTION_POWER_SAVE_MODE_CHANGED));

        deepSleepReceiver = new DeepSleepReceiver();
        registerReceiver(deepSleepReceiver, new
                IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED));

        batteryChangedReceiver = new BatteryChangedReceiver();
        registerReceiver(batteryChangedReceiver, new
                IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(className,"SERVICE ON DESTROY");
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        unregisterReceiver(bootReceiver);

        unregisterReceiver(deepSleepReceiver);

        unregisterReceiver(powerSaverReceiver);

        unregisterReceiver(batteryChangedReceiver);

        Log.d(className,"SERVICE STOPPED BY USER");

        serviceSignalMsg = ServiceSignal.SERVICE_STOPPED_BY_USER;

        onLocationSubmit(gpsListener.getLocation(),serviceSignalMsg);

        gpsListener.stopLocationUpdate();

        gpsListener = null;

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(SERVICE_NOTIFICATION_ID);
        notificationManager.cancel(POWER_SAVE_NOTIFICATION_ID);

        stopForeground(true);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceSignalMsg = ServiceSignal.SERVICE_STARTED_ON_BOOT;

        // signal received from intent (context.startForegroundService(serviceIntent);)
        if (intent.hasExtra("SIGNAL")){

            serviceSignalMsg = Objects.requireNonNull(intent.getExtras()).getString("SIGNAL");

            Log.i(className,"SIGNAL: "+serviceSignalMsg);

            if (gpsListener != null) {
                Log.d(className,"GPS NOT NULL");
                onLocationSubmit(gpsListener.getLocation(), serviceSignalMsg);
//                gpsListener.stopLocationUpdate();
//                gpsListener = null;
            }
        }

//        gpsListener = new GPSListener(this, this);
//
//        gpsListener.requestLocation();

        Log.d(className,"START");

        startForeground(SERVICE_NOTIFICATION_ID,
                NotificationBuilder.SetNotification(this).build(),
                FOREGROUND_SERVICE_TYPE_LOCATION );
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onLocationSubmit(Location location, String msg) {
        if (location == null) return;
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isDeviceIdle = powerManager.isDeviceIdleMode();

        if (isDeviceIdle){
            msg=msg+" "+isDeviceIdle;
        }

        Log.i(className, "LOCATION CHANGED EVENT");

        Log.i(className, location.toString());

        if (!serviceSignalMsg.equals("")) {
            if (!msg.equals("")) {
                msg = serviceSignalMsg;
            } else {
                msg = serviceSignalMsg + ", " + msg;
            }
            serviceSignalMsg = "";
        }

        DeviceStatusDbRecord deviceStatusDbRecord = new DeviceStatusDbRecord(
                DeviceStatus.getBatteryLevel(GPSStickyService.this),
                DeviceStatus.getTimeZoneOffsetInHours(),
                DeviceStatus.getTimeZone()
        );

        LocationDbRecord locationDbRecord = new LocationDbRecord(
                this, location, deviceStatusDbRecord, msg);

        PostLocation postLocation = new PostLocation(
                "https://pijo.linkpc.net/api/location",
                GPSStickyService.this);

        postLocation.sendPost(locationDbRecord);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
