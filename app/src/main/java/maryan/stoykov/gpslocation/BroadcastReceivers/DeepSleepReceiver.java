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
    @SuppressLint({"WakelockTimeout", "UnsafeProtectedBroadcastReceiver"})
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GPSStickyService.class);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isDeviceIdle = powerManager.isDeviceIdleMode();

        PowerManager.WakeLock wakeLock =
                powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                        "deepSleep:WakeLock");
        if (isDeviceIdle){
            wakeLock.acquire();

            context.stopService(serviceIntent);
            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEEP_SLEEP);
            context.startForegroundService(serviceIntent);

        } else {
            Log.d("DeepSleepReceiver","DEVICE NOT IDLE");
            if (wakeLock.isHeld()) wakeLock.release();
        }
    }
}
