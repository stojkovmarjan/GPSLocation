package maryan.stoykov.gpslocation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

public class NotificationBuilder {
    public static final int SERVICE_NOTIFICATION_ID = 11001;
    public static final int POWER_SAVE_NOTIFICATION_ID = 11002;
    public static final int IDLE_DUMMY_NOTIFICATION = 11003;
    protected static Notification.Builder SetNotification (Context context) {

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent();
            intent.setClassName(context.getPackageName(),
                    "maryan.stoykov.gpslocation.MainActivity");
        }

        intent.putExtra("notificationId",SERVICE_NOTIFICATION_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        final String CHANNEL_ID = "My Foreground ID";

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );

        context.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);

        return new Notification.Builder (context, CHANNEL_ID)
                .setContentText("Foreground service is running!")
                .setContentTitle("Service enabled")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.marker_24);
    }

    public static void notifyForPowerSaver (Context context) {

        Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_IMMUTABLE);
        final String CHANNEL_ID = "Power saver ID";

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
        );

        context.getSystemService(NotificationManager.class)
                .createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, CHANNEL_ID)
                .setContentText(
                        "Seems like the battery power saver is on.\nPlease click on this notification \nto turn off the power saver")
                .setContentTitle("PLEASE TURN OFF POWER SAVER!")
                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
                .setSmallIcon(R.drawable.marker_24_1);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(POWER_SAVE_NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context, int notificationId){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
    public static void notifyForDeviceIdle (Context context) {

        Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_IMMUTABLE);
        final String CHANNEL_ID = "Power saver ID";

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
        );

        context.getSystemService(NotificationManager.class)
                .createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, CHANNEL_ID)
                .setContentText("Just a test notification")
                .setContentTitle("IDLE PROBLEM SOLVING")
                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
                .setSmallIcon(R.drawable.marker_24_1);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(IDLE_DUMMY_NOTIFICATION, builder.build());
    }


}
