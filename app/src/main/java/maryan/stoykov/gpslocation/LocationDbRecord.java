package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class LocationDbRecord {
    private final String className = this.getClass().getSimpleName();
    private final String dateTime;
    private final Double latitude;
    private final Double longitude;
    private final Float accuracy;
    private final String message;
    private final String deviceId;
    private final String provider;
    private final Long id;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat locationDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    );
    public LocationDbRecord(Long id, String dateTime, String deviceId, Double latitude, Double longitude,
                            Float accuracy,  String provider, String message ){

        this.id = id;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.message = message;
        this.deviceId = deviceId;
        this.provider = provider;

    }
    @SuppressLint("HardwareIds")
    public LocationDbRecord(Context context, Location location, String message){

        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        this.id = -1L;
        this.dateTime = locationDateFormat.format(location.getTime());
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
        this.provider = location.getProvider();
        this.message = message;
    }
    @NonNull
    public String toString(){
        return this.id+", "+
                this.dateTime+", "+
                this.longitude+", "+
                this.longitude+", "+
                this.longitude+", "+
                this.accuracy+", "
                +this.message;
    }
    public String getDateTime(){
        return this.dateTime;
    }
    public String getDeviceId(){
        return this.deviceId;
    }
    public Double getLatitude(){
        return this.latitude;
    }
    public Double getLongitude(){
        return this.longitude;
    }
    public Float getAccuracy(){
        return this.accuracy;
    }
    public String getProvider(){
        return this.provider;
    }
    public String getMessage(){
        return this.message;
    }
    public Long getId(){
        return this.id;
    }
    public JSONObject getLocationJson(){

        JSONObject jsonParam = new JSONObject();

        try {

            jsonParam.put("time", this.dateTime);
            jsonParam.put("deviceId", this.deviceId);
            jsonParam.put("latitude", this.latitude);
            jsonParam.put("longitude", this.longitude);
            jsonParam.put("accuracy", df.format(this.accuracy));
            jsonParam.put("provider",this.provider);
            jsonParam.put("message",this.message);

        } catch (JSONException e) {
            Log.e(className,"Conversion to JSON failed!");
            e.printStackTrace();
        }
        return  jsonParam;
    }
}
