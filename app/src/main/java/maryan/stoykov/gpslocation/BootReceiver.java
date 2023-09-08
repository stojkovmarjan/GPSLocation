package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, GPSStickyService.class);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_REBOOT)) {
                serviceIntent.putExtra("SIGNAL","POWER OFF or REBOOT");
            } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
                serviceIntent.putExtra("SIGNAL","REBOOT");
            } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                serviceIntent.putExtra("SIGNAL","SERVICE STARTED ON BOOT");
            }
        }
        context.startForegroundService(serviceIntent);
    }
}
