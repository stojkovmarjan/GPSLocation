package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.NotificationBuilder;
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (powerManager.isDeviceLightIdleMode()) {
                Log.d("DeepSleepReceiver","DEVICE LIGHT IDLE");
            }
        }

        if (powerManager.isInteractive()){
            Log.d("DeepSleepReceiver","DEVICE INTERACTIVE");
        }


        if (isDeviceIdle){
            Log.d("DeepSleepReceiver","DEVICE IDLE");
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                            "deepSleep:WakeLock");
//
//           wakeLock.acquire(10000);

            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEEP_SLEEP);

            context.startForegroundService(serviceIntent);

        } else {
            Log.d("DeepSleepReceiver","DEVICE NOT IDLE");

//            if (wakeLock.isHeld()) wakeLock.release();

            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEVICE_ACTIVE);
            context.startForegroundService(serviceIntent);

        }
    }

}
