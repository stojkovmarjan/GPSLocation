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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Post extends AppCompatActivity  {
    private final Context context;
    private final String endpointURL;

    public Post(Context context, String endpointURL) {
        this.context = context;
        this.endpointURL = endpointURL;
    }

    public void sendPost(Location location, String msg) {

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              boolean requestSuccess = false;
                try {
                    java.net.URL url = new URL(endpointURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("imei", deviceId);
                    jsonParam.put("lat", location.getLatitude());
                    jsonParam.put("lon", location.getLongitude());
                    jsonParam.put("acc",location.getAccuracy());
                    jsonParam.put("msg",msg);


                  Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                      // Request was successful
                      requestSuccess = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

              final boolean finalRequestSuccess = requestSuccess;

              // Update UI on the main thread
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  if (finalRequestSuccess) {
                    // Show a success message
                    Toast.makeText(context, "POST request successful", Toast.LENGTH_SHORT).show();
                  } else {
                    // Show an error message
                    Toast.makeText(context, "POST request failed", Toast.LENGTH_SHORT).show();
                  }
                }
              });
            }
        });

        thread.start();
    }
}
