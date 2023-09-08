package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GPSStickyService.class);
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
                // Handle power-off event
                // This code will execute when the device is shutting down.
            } else if (intent.getAction().equals(Intent.ACTION_REBOOT)) {
                // Handle reboot event
                // This code will execute when the device is restarting.
            }
        }
        context.startForegroundService(serviceIntent);
    }
}
