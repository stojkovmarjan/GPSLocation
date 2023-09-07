package maryan.stoykov.gpslocation;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostLocation {
    //private final Context context;
    private final String endpointURL;
    private HttpURLConnection conn;
    private int responseCode;
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

                    postLocationResponseListener.onHttpResponse(conn.getResponseCode(), locationDbRecord);

                } catch (Exception e) {
                    postLocationResponseListener.onHttpResponse(400, locationDbRecord);
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
        });
        thread.start();
    }

}
