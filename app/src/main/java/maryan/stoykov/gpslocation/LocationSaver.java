package maryan.stoykov.gpslocation;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LocationSaver {
    private final Context context;
    private final String endpointURL;
    private HttpURLConnection conn;

    public LocationSaver(Context context, String endpointURL) {
        this.context = context;
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

                    Log.i("POST CLASS" , conn.getResponseMessage());
                    Log.i("POST CLASS","Response code: "+ conn.getResponseCode());

                    if (conn.getResponseCode() == 200 && locationDbRecord.getId() == -1){
                        postDbRecords();
                    }

                    if (conn.getResponseCode() == 200 && locationDbRecord.getId() > -1){
                        deleteDbRecord(locationDbRecord.getId());
                    }

                } catch (Exception e) {
                    Log.e("POST CLASS","ENDPOINT NOT AVAILABLE");
                    if (locationDbRecord.getId() == -1) writeToDb(locationDbRecord);
                } finally {
                    conn.disconnect();
                }
            }
        });

        thread.start();
    }

    private void postDbRecords(){

        List<LocationDbRecord> locationDbRecords;

        try (DBHelper dbHelper = new DBHelper(context)) {

            if (dbHelper.getRecordsCount() <= 0) return;

            Log.i("POST CLASS", "DB has records");

            locationDbRecords = dbHelper.getLocationsList();
        }

        for (LocationDbRecord locationDbRecord: locationDbRecords ) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sendPost(locationDbRecord);
                }
            });
            thread.start();
        }

    }

    private void deleteDbRecord(Long id){
        try (DBHelper dbHelper = new DBHelper(context)) {
            dbHelper.deleteLocationRecord(id);
        }
    }

    private void writeToDb(LocationDbRecord locationDbRecord){

        DBHelper db = new DBHelper(context);

        Long rowId = db.addLocation(locationDbRecord);

        if (rowId > -1) {
            Log.i("POST CLASS","Location is added to local db!");
        } else {
            Log.e("POST CLASS","Write to db failed!");
        }
    }
}
