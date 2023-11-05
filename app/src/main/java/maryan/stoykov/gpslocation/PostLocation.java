package maryan.stoykov.gpslocation;

import android.content.Context;
import android.os.Looper;
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
import maryan.stoykov.gpslocation.Models.TrackingProfile;
import maryan.stoykov.gpslocation.Models.WorkDays;
import maryan.stoykov.gpslocation.Models.WorkTime;

public class PostLocation  {
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
                Looper.prepare();
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

                    // TODO: here new DTO instead of only parameters response
                    ResponseRoot responseRoot = null;

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK
                            || conn.getResponseCode() == 208){
                        responseRoot = parseResponseRootJson(
                                handlePostResponseData(conn.getInputStream())
                        );
                    }

                    postLocationResponseListener.onHttpResponse(
                            conn.getResponseCode(), locationDbRecord, responseRoot
                    );


                } catch (Exception e) {
                    e.printStackTrace();
                    postLocationResponseListener.onHttpResponse(
                            500, locationDbRecord, null
                    );
                } finally {
                    conn.disconnect();
                }
//                return null;
                Looper.loop();
            }
        });
        
        thread.start();
    }

    private String handlePostResponseData(InputStream inputStream){
        String responseData = "";
        try {
            // Read and process the data from the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            responseData = response.toString();

            inputStream.close();
            Log.d(className,"Response data: "+responseData);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
        return responseData;
    }

    private ResponseRoot parseResponseRootJson(String jsonString){

        Gson gson = new Gson();

        // TODO: get all objects from the root and add them to the DTO object
        //LocationResponse locationResponse = root.getLocationResponse();
//        String message = root.getMessage();
//
//        ParametersResponse parametersResponse = root.getParametersResponse();
//
//        TrackingProfile trackingProfile = root.getTrackingProfile();
//
//        WorkDays workDays = root.getWorkDays();
//
//        WorkTime workTime = root.getWorkTime();;

        //Log.d(className, "PARSED PARAMETERS DATA: "+ parametersResponse);

        return gson.fromJson(jsonString, ResponseRoot.class);
    }
}
