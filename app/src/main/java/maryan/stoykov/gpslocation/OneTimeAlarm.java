package maryan.stoykov.gpslocation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import maryan.stoykov.gpslocation.BroadcastReceivers.DeepSleepAlarmReceiver;

public class OneTimeAlarm {
    public static void setOneTimeAlarm(Context context, Calendar triggerTime) {
        Log.d("ONE TIME ALARM","SENDING ALARM");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Create an intent to specify the broadcast receiver that will be triggered when the alarm goes off
        Intent intent = new Intent(context, DeepSleepAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the one-time alarm using RTC_WAKEUP
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), pendingIntent);
    }
}
