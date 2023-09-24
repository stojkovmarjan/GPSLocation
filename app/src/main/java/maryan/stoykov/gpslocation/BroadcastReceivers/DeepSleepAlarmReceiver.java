package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import maryan.stoykov.gpslocation.GPSStickyService;
import maryan.stoykov.gpslocation.ServiceSignal;

public class DeepSleepAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GPSStickyService.class);
        Log.d("ALARM RECEIVER","RECEIVED ALARM");
        serviceIntent.putExtra("SIGNAL", ServiceSignal.DEEP_SLEEP);
        context.startForegroundService(serviceIntent);
        Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show();
    }
}
