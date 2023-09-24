package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import maryan.stoykov.gpslocation.DeviceStatus;
import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.NotificationBuilder;
import maryan.stoykov.gpslocation.ServiceSignal;

public class PowerSaverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("POWER SAVER RECEIVER", "POWER SAVER CHANGED");
        Intent serviceIntent = new Intent(context, GPSStickyService.class);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isInteractive = powerManager.isInteractive();
        Log.d("POWER SAVER RECEIVER", "isInteractive "+isInteractive);
        if (DeviceStatus.isPowerSaveOn(context)){
            Log.d("POWER SAVER RECEIVER", "POWER SAVER ON");
            NotificationBuilder.notifyForPowerSaver(context);
            serviceIntent.putExtra("SIGNAL", ServiceSignal.POWER_SAVER_IS_ON);
            context.startForegroundService(serviceIntent);
        }  else {
            Log.d("POWER SAVER RECEIVER", "POWER SAVER OFF");
            NotificationBuilder.cancelNotification(
                    context, NotificationBuilder.POWER_SAVE_NOTIFICATION_ID);
            serviceIntent.putExtra("SIGNAL", ServiceSignal.POWER_SAVER_IS_OFF);
            context.startForegroundService(serviceIntent);
        }
    }
}
