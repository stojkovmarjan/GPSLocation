package maryan.stoykov.gpslocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_CODE = 1234;
    private final int REQUEST_ACCESS_BACKGROUND_LOCATION_CODE = 1235;
    private Intent serviceIntent;
    private ToggleButton toggleServiceButton;
    private EditText etUpdateInterval;
    private EditText etMinUpdateInterval;
    private EditText etMinUpdateDistance;
    private CheckBox checkStartAtBoot;

    final String[] permissions = {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUpdateInterval = findViewById(R.id.etUpdateInterval);
        etMinUpdateInterval = findViewById(R.id.etMinUpdateInterval);
        etMinUpdateDistance = findViewById(R.id.etMinUpdateDistance);
        checkStartAtBoot = findViewById(R.id.checkStartAtBoot);
        toggleServiceButton = findViewById(R.id.serviceButton);
        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        tvDeviceId.setText(getDeviceId());
        etUpdateInterval.setText(LocationParams.getUpdateInterval(this).toString());
        etMinUpdateInterval.setText(LocationParams.getMinUpdateInterval(this).toString());
        etMinUpdateDistance.setText(
                Float.toString(LocationParams.getMinUpdateDistance(this))
        );

        checkStartAtBoot.setActivated(
                LocationParams.startServiceOnBoot(this)
        );

        toggleServiceButton.setChecked(isServiceRunning());


        askForPermissions();
        toggleServiceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setServiceState(b);
            }
        });

    }

    private void setServiceState(boolean b) {
        if (b) {
            toggleServiceButton.setText("Service is ON");
            toggleServiceButton.setTextColor(Color.GREEN);
            startGPSService();
        } else {
            toggleServiceButton.setText("Service is OFF");
            toggleServiceButton.setTextColor(Color.GRAY);
            stopGPSService();
        }
    }

    private void stopGPSService() {

        if (serviceIntent == null) {
            serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
        }

        stopService(serviceIntent);

        serviceIntent = null;
    }

    private void startGPSService() {

        LocationParams.savePreferences(this,
                checkStartAtBoot.isChecked(),
                Long.parseLong(String.valueOf(etUpdateInterval.getText())),
                Long.parseLong(String.valueOf(etMinUpdateInterval.getText())),
                Float.parseFloat(String.valueOf(etMinUpdateDistance.getText()))
        );

        if (checkPermissions() ){

            serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
            serviceIntent.putExtra("SIGNAL","SERVICE IS STARTED BY USER MSG");
            startForegroundService(serviceIntent);
        } else {
            askForPermissions();
        }
    }
    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean checkPermissions() {

        // Check if all permissions are granted
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        // All permissions are granted
        return true;
    }

    // Ask for permissions if they are not granted
    public void askForPermissions() {

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e ("GRANT: ", "onReqPer");

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if ( grantResults.length > 0 ) {
                // Permission granted, proceed with using location services
                for (int grantResult: grantResults) {

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,
                                "Please restart the application and grant all permissions!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                }

                // TODO: ACCESS_BACKGROUND_LOCATION - should go in separated method?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Explain why you need background location to the user
                    // ...
                    // Then, request the background location permission
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            REQUEST_ACCESS_BACKGROUND_LOCATION_CODE);
                } else {
                    // Request the background location permission without explanation
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            REQUEST_ACCESS_BACKGROUND_LOCATION_CODE);
                }
                //--------------------------------------------------------------------------

            } else {
                Toast.makeText(this,
                        "Please restart the application and grant all permissions!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        if (requestCode == REQUEST_ACCESS_BACKGROUND_LOCATION_CODE) {
            Log.i("ACCESS_BACKGROUND_LOCATION", "ACCESS_BACKGROUND_LOCATION "+String.valueOf(grantResults[0]));
        }
    }


    @SuppressWarnings("deprecation")
    private boolean isServiceRunning(){

        //ActivityManager manager = ContextCompat.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GPSStickyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}