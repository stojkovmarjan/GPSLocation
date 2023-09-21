package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maryan.stoykov.gpslocation.DeviceStatus;
import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.NotificationBuilder;
import maryan.stoykov.gpslocation.ServiceSignal;

public class BatteryChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, GPSStickyService.class);

        Log.d("BatteryChangedReceiver", "BATTERY CHANGED");

        if (DeviceStatus.isPowerSaveOn(context)){
            NotificationBuilder.notifyForPowerSaver(context);
            serviceIntent.putExtra("SIGNAL", ServiceSignal.POWER_SAVER_IS_ON);
            context.startForegroundService(serviceIntent);

        }

    }
}
