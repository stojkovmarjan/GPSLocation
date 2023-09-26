package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.ServiceSignal;

public class DeepSleepReceiver extends BroadcastReceiver {
    PowerManager.WakeLock wakeLock;
    @SuppressLint({"UnsafeProtectedBroadcastReceiver", "WakelockTimeout"})
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("DeepSleepReceiver", "DEVICE IDLE MODE CHANGED");
        Intent serviceIntent = new Intent(context, GPSStickyService.class);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isDeviceIdle = powerManager.isDeviceIdleMode();


        wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "deepSleep:WakeLock");

//        powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |
//                PowerManager.ACQUIRE_CAUSES_WAKEUP,
        if (isDeviceIdle){

            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                                    | PowerManager.ACQUIRE_CAUSES_WAKEUP,

                            "deepSleep:WakeLock");
            Log.d("DeepSleepReceiver","DEVICE IDLE");
           wakeLock.acquire(10000);

//            NotificationBuilder.notifyForPowerSaver(context.getApplicationContext());
            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEEP_SLEEP);
            context.startForegroundService(serviceIntent);
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(home);


        } else {
            Log.d("DeepSleepReceiver","DEVICE NOT IDLE");
            if (wakeLock.isHeld()) wakeLock.release();
            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEVICE_ACTIVE);
            context.startForegroundService(serviceIntent);
//            NotificationBuilder.cancelNotification(context.getApplicationContext(),NotificationBuilder.POWER_SAVE_NOTIFICATION_ID);
        }
    }
}
