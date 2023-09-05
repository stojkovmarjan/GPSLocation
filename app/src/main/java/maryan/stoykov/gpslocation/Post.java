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
import java.text.DecimalFormat;

public class Post {
    private final Context context;
    private final String endpointURL;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private HttpURLConnection conn;


    public Post(Context context, String endpointURL) {
        this.context = context;
        this.endpointURL = endpointURL;
    }

    public void sendPost(Location location, String msg) {
        Log.i("POST CLASS", msg);
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    java.net.URL url = new URL(endpointURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("deviceId", deviceId);
                    jsonParam.put("latitude", location.getLatitude());
                    jsonParam.put("longitude", location.getLongitude());
                    jsonParam.put("accuracy", df.format(location.getAccuracy()) );
                    jsonParam.put("provider",location.getProvider());
                    jsonParam.put("message",msg);

                    Log.i("POST CLASS", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("POST CLASS","Response code: "+ String.valueOf(conn.getResponseCode()));
                    Log.i("POST CLASS" , conn.getResponseMessage());

                } catch (Exception e) {
                    /** TODO: Here if endpoint not available, no internet or whatever
                     * save data to local storage and set data stored flag to true
                     * IF available check if data stored flag is true
                     * IF flag is true read data from storage and send it to the endpoint
                     * set flag false on the end of the process
                     */
                    Log.e("POST CLASS","ENDPOINT NOT AVAILABLE");
                    Log.i("POST CLAAS",
                            "NOT SENT: "+location.getLatitude()+", "+location.getLongitude());

                } finally {
                    conn.disconnect();
                }
            }
        });

        thread.start();
    }
}
