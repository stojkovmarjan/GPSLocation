package maryan.stoykov.gpslocation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

import maryan.stoykov.gpslocation.EventListeners.PostLocationResponseListener;
import maryan.stoykov.gpslocation.Models.LocationResponse;
import maryan.stoykov.gpslocation.Models.ParametersResponse;
import maryan.stoykov.gpslocation.Models.ResponseRoot;

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

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        handlePostResponseData(conn.getInputStream());
                    }

                    postLocationResponseListener.onHttpResponse(
                            conn.getResponseCode(), locationDbRecord
                    );


                } catch (Exception e) {
                    e.printStackTrace();
                    postLocationResponseListener.onHttpResponse(
                            404, locationDbRecord
                    );
                } finally {
                    conn.disconnect();
                }
//                return null;
            }
        });
        thread.start();
    }

    private void handlePostResponseData(InputStream inputStream){
        try {
            // Read and process the data from the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Now you can work with the response data in this method
            String responseData = response.toString();

            // Close the inputStream when you're done with it
            inputStream.close();
            Log.d(className,"Response data: "+responseData);
            parseJson(responseData);
            // Further processing or handling of the responseData
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }

    private void parseJson(String jsonString){

        Gson gson = new Gson();

        ResponseRoot root = gson.fromJson(jsonString, ResponseRoot.class);

        LocationResponse locationResponse = root.getLocationResponse();

        ParametersResponse parametersResponse = root.getParametersResponse();

        String time = locationResponse.getTime();
        double latitude = locationResponse.getLatitude();
        int updateInterval = parametersResponse.getUpdateInterval();
        Log.d(className, "PARSED DATA: "+time+", "+latitude+", "+updateInterval);
    }

}
