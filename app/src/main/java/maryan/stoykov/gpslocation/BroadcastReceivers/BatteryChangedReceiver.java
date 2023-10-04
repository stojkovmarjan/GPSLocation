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

public class BatteryChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent serviceIntent = new Intent(context, GPSStickyService.class);

//        Log.d("BatteryChangedReceiver", "BATTERY CHANGED");
//
//        Intent serviceIntent = new Intent(context, GPSStickyService.class);
//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        boolean isDeviceIdle = powerManager.isDeviceIdleMode();
//        if (isDeviceIdle){
//            serviceIntent.putExtra("SIGNAL", ServiceSignal.DEEP_SLEEP+" batt changed");
//            context.startForegroundService(serviceIntent);
//        }

    }
}
