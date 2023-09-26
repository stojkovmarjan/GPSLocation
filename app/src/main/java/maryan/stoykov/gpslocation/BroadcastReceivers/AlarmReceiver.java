package maryan.stoykov.gpslocation.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import maryan.stoykov.gpslocation.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Context appContext = context.getApplicationContext();
        Log.d("ALARM RECEIVER","ALARM RECEIVED");
        // This method will be called when the alarm goes off

        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Intent myAct = new Intent(context, MainActivity.class);
//        myAct.putExtra("WAKE", true);
//        myAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        context.startActivity(myAct);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, "", Toast.LENGTH_SHORT).show();
            }
        });
        // You can perform any additional actions here
    }
}

