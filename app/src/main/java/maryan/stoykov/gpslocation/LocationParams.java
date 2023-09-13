package maryan.stoykov.gpslocation;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationParams {
    private static Long UPDATE_INTERVAL = 5L;
    private static Long MIN_UPDATE_INTERVAL = UPDATE_INTERVAL / 3;
    private static float MIN_UPDATE_DISTANCE = 5f;
    private static boolean START_SERVICE_ON_BOOT = true;

    public static void savePreferences(Context context,
                                       boolean startAtBoot,
                                       Long updateInterval,
                                       Long minUpdateInterval,
                                       float minUpdateDistance){
        // Initialize Shared Preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "location_preferences", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("updateInterval", updateInterval);
        editor.putLong("minUpdateInterval",minUpdateInterval);
        editor.putFloat("minUpdateDistance",minUpdateDistance);
        editor.putBoolean("startAtBoot", startAtBoot);
        editor.apply();

    }

    public static Long getUpdateInterval(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "location_preferences", Context.MODE_PRIVATE);
        UPDATE_INTERVAL = sharedPreferences.getLong("updateInterval", 300L);
        return UPDATE_INTERVAL;
    }
    public static Long getMinUpdateInterval(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "location_preferences", Context.MODE_PRIVATE);

        MIN_UPDATE_INTERVAL = sharedPreferences.getLong("minUpdateInterval", 100L);

        return MIN_UPDATE_INTERVAL;
    }
    public static float getMinUpdateDistance(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "location_preferences", Context.MODE_PRIVATE);

        MIN_UPDATE_DISTANCE = sharedPreferences.getFloat("minUpdateDistance", 5f);

        return MIN_UPDATE_DISTANCE;
    }
    public static boolean startServiceOnBoot(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "location_preferences", Context.MODE_PRIVATE);
        START_SERVICE_ON_BOOT = sharedPreferences.getBoolean("startAtBoot", true);
        return START_SERVICE_ON_BOOT;
    }


}
