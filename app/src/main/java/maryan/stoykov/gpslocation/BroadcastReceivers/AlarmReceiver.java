package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Context appContext = context.getApplicationContext();
        Log.d("ALARM RECEIVER","ALARM RECEIVED");
        // This method will be called when the alarm goes off

        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, "Your message", Toast.LENGTH_LONG).show();
            }
        });
        // You can perform any additional actions here
    }
}

