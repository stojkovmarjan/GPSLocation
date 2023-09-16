package maryan.stoykov.gpslocation;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostLocation {
    private final String className = this.getClass().getSimpleName();
    private final String endpointURL;
    private HttpURLConnection conn;
    private final PostLocationResponseListener postLocationResponseListener;

    public PostLocation(String endpointURL, PostLocationResponseListener postLocationResponseListener) {

        this.postLocationResponseListener = postLocationResponseListener;
        //this.context = context;
        this.endpointURL = endpointURL;
    }

    public void sendPost(LocationDbRecord locationDbRecord) {
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

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    os.writeBytes(locationDbRecord.getLocationJson().toString());

                    os.flush();
                    os.close();

                    postLocationResponseListener.onHttpResponse(
                            conn.getResponseCode(), locationDbRecord
                    );

                } catch (Exception e) {
                    postLocationResponseListener.onHttpResponse(
                            404, locationDbRecord
                    );
                } finally {
                    locationDbRecord.setMessage("");
                    conn.disconnect();
                }
//                return null;
            }
        });
        thread.start();
    }
}
