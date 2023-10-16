package maryan.stoykov.gpslocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import maryan.stoykov.gpslocation.EventListeners.PostLocationResponseListener;
import maryan.stoykov.gpslocation.Models.ParametersResponse;
import maryan.stoykov.gpslocation.Models.ResponseRoot;

public class MainActivity extends AppCompatActivity {
    private final String className = this.getClass().getSimpleName();
    private final int REQUEST_PERMISSIONS_CODE = 1234;
    private String[] permissions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(className,"ON CREATE");

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        tvDeviceId.setText("Device ID: "+getDeviceId().toUpperCase());
        
        try (DBHelper db = new DBHelper(this)) {
            Log.d(className,"CALLED DB");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = getPermissionsForSDK33plus();
        } else {
            permissions = getPermissions();
        }

        askIgnoreBatteryOptimization();

        boolean isDeviceRegistered = LocationParams.getDeviceIsRegistered(this);

        boolean isServiceRunning = isServiceRunning();

        if (isDeviceRegistered){
            //Toast.makeText(this,"Device is registered!",Toast.LENGTH_SHORT).show();
            if (isServiceRunning){
                // here we need SERVICE_RESUMED signal maybe?
                //startGPSService(ServiceSignal.SERVICE_STARTED_BY_USER);
                Intent intent = new Intent(this, ActionsActivity.class);
                startActivity(intent);
                this.finish();
            }
        }

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(className,"ON RESUME");
    }

    private void askTurnOffPowerSaver() {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        if (powerManager.isPowerSaveMode()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Power Save Mode")
                    .setMessage(
                            "Power save mode is on. This may reduce the performance of your app.")
                    .setPositiveButton("Turn off", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open the power saving mode settings.
                            startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this,
                                    "Please restart the application and turn off power saver!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .show();
        }
    }
    private void askIgnoreBatteryOptimization() {

        //Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            @SuppressLint("BatteryLife")
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            batteryOptimizationLauncher.launch(intent);
        } else {
            askForPermissions();
        }
    }
    private final ActivityResultLauncher<Intent> batteryOptimizationLauncher =
            registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(className, "activity result: "+result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK){
                        askForPermissions();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Please restart the application and grant all permissions!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
    );
    private  void savePreferences(){
        LocationParams.savePreferences(this,
                true,
                Long.parseLong("60"),
                Long.parseLong("57"),
                Float.parseFloat("0")
        );
    }
    private void startGPSService(String msg) {

        savePreferences();

        if (checkPermissions() ){
            Intent serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
            Log.d(className,"CALLING START FOREGROUND SERVICE MAIN ACTIVITY");
            serviceIntent.putExtra("SIGNAL",msg);
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
            if (ContextCompat
                    .checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED
            ) {
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

        int REQUEST_ACCESS_BACKGROUND_LOCATION_CODE = 1235;
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
            Log.i("ACCESS_BACKGROUND_LOCATION",
                    "ACCESS_BACKGROUND_LOCATION "+String.valueOf(grantResults[0]));
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Please restart the application and grant all permissions!",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
               askTurnOffPowerSaver();
               startGPSService(ServiceSignal.SERVICE_STARTED_BY_USER);
            }
        }
    }


    @SuppressWarnings("deprecation")
    private boolean isServiceRunning(){

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GPSStickyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("InlinedApi")
    private String[] getPermissionsForSDK33plus (){
        return new String[]{
               Manifest.permission.WAKE_LOCK,
               Manifest.permission.POST_NOTIFICATIONS,
               Manifest.permission.FOREGROUND_SERVICE,
               Manifest.permission.RECEIVE_BOOT_COMPLETED,
               Manifest.permission.ACCESS_FINE_LOCATION,
               Manifest.permission.ACCESS_COARSE_LOCATION,
               Manifest.permission.ACCESS_NETWORK_STATE,
               Manifest.permission.INTERNET
       };
    }

    private String[] getPermissions (){
        return new String[]{
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };
    }

}