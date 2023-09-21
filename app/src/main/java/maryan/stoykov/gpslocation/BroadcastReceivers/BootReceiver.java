package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.LocationParams;
import maryan.stoykov.gpslocation.ServiceSignal;

public class BootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean startAtBoot = LocationParams.startServiceOnBoot(context);

        Intent serviceIntent = new Intent(context, GPSStickyService.class);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_REBOOT)) {

                serviceIntent.putExtra("SIGNAL", ServiceSignal.REBOOT);
                context.startForegroundService(serviceIntent);

            } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

                serviceIntent.putExtra("SIGNAL", ServiceSignal.POWER_OFF);
                context.startForegroundService(serviceIntent);

            } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                    && startAtBoot){
                serviceIntent.putExtra("SIGNAL", ServiceSignal.SERVICE_STARTED_ON_BOOT);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}
