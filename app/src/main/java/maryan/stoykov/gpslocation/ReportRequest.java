package maryan.stoykov.gpslocation;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import maryan.stoykov.gpslocation.EventListeners.ReportRequestResponseListener;
import maryan.stoykov.gpslocation.Models.ReportResponse;
import maryan.stoykov.gpslocation.Models.ResponseRoot;

public class ReportRequest {
    private HttpURLConnection conn;
    private final ReportRequestResponseListener reportRequestResponseListener;
    ReportResponse reportResponse;
    int responseCode;
    public ReportRequest(ReportRequestResponseListener reportRequestResponseListener){
        this.reportRequestResponseListener = reportRequestResponseListener;
    }
    public void sendRequest(String deviceId, int month){
        String baseUrl = "https://izzihr.schweizerweb.com/api/reports/device/";
        //String baseUrl = "https://pijo.linkpc.net/api/location/";
        String urlString = baseUrl +deviceId+"/"+month;
        //Log.d("REQUEST REPORT","SEND REQUEST");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                try {
                    URL url = new URL(urlString);
                    conn = (HttpURLConnection) url.openConnection();

                    //Log.d("REQUEST REPORT","conn open conn");

                    //Log.d("REQUEST REPORT", conn.getResponseCode()+"");
                    String responseData = "";

                    responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        responseData = handlePostResponseData(conn.getInputStream());
                    }

                    reportResponse = new ReportResponse();

                    if (!responseData.equals("")){
                        reportResponse = parseResponseRootJson(responseData);
                        //Log.d("REQUEST REPORT", "DEVICE ID: "+reportResponse.getDevice_id());
                    }


                } catch (MalformedURLException | ProtocolException e) {
                    Log.e("URL EXCEPTION", e.toString());
                } catch (IOException e) {
                    Log.e("IO EXCEPTION", e.toString());
                }
                finally {

                    ReportResponse finalReportResponse = reportResponse;
                    final int finalResponseCode = responseCode;
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            reportRequestResponseListener.onReportResponse(
                                    responseCode, finalReportResponse
                            );
                        }
                    });

                    conn.disconnect();
                    conn = null;
                }
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
            //Log.d("REQUEST REPORT","Response data: "+responseData);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
        return responseData;
    }

    private ReportResponse parseResponseRootJson(String jsonString){

        Gson gson = new Gson();

        return gson.fromJson(jsonString, ReportResponse.class);
    }

}
