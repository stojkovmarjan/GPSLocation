package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Post {
    private final Context context;
    private final String URL;

    public Post(Context context, String URL) {
        this.context = context;
        this.URL = URL;
    }

    public void sendPost(Location location) {

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    java.net.URL url = new URL("https://msvs.ddnsfree.com/api/location");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("deviceId", deviceId);
                    jsonParam.put("latitude", location.getLatitude());
                    jsonParam.put("longitude", location.getLongitude());
                    jsonParam.put("accuracy",location.getAccuracy());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
