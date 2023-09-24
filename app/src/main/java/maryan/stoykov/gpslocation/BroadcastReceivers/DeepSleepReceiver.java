package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.NotificationBuilder;
import maryan.stoykov.gpslocation.OneTimeAlarm;
import maryan.stoykov.gpslocation.ServiceSignal;

public class DeepSleepReceiver extends BroadcastReceiver {
    //private Context context;
    @SuppressLint({"WakelockTimeout", "UnsafeProtectedBroadcastReceiver"})
    @Override
    public void onReceive(Context context, Intent intent) {
        //this.context = context;
        //Intent serviceIntent = new Intent(context, GPSStickyService.class);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isDeviceIdle = powerManager.isDeviceIdleMode();

        PowerManager.WakeLock wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "deepSleep:WakeLock");
        if (isDeviceIdle){
            wakeLock.acquire();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 2);
            // Call the setOneTimeAlarm method to schedule the alarm
            OneTimeAlarm.setOneTimeAlarm(context, calendar);
            Log.d("DeepSleepReceiver","DEVICE IDLE");

        } else {
            Log.d("DeepSleepReceiver","DEVICE NOT IDLE");
            if (wakeLock.isHeld()) wakeLock.release();
        }
        Log.d("DeepSleepReceiver",""+wakeLock.isHeld());
    }

//    Runnable dummyRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                NotificationBuilder.notifyForDeviceIdle(context);
//                Thread.sleep(3000);
//                NotificationBuilder.cancelNotification(context,
//                        NotificationBuilder.IDLE_DUMMY_NOTIFICATION);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    };
}
