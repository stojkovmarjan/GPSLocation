package maryan.stoykov.gpslocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

//@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private final String className = this.getClass().getSimpleName();
//    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 12345;
    private final int REQUEST_PERMISSIONS_CODE = 1234;
    private final int REQUEST_ACCESS_BACKGROUND_LOCATION_CODE = 1235;
    //private static final int REQUEST_OVERLAY_PERMISSION = 1236;
    private Intent serviceIntent;
    private ToggleButton toggleServiceButton;
    private EditText etUpdateInterval;
    private EditText etMinUpdateInterval;
    private EditText etMinUpdateDistance;
    private CheckBox checkStartAtBoot;
    private String[] permissions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(className,"On create");
        try (DBHelper db = new DBHelper(this)) {
            Log.d(className,"CALLED DB");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = getPermissionsForSDK33plus();
        } else {
            permissions = getPermissions();
        }

        etUpdateInterval = findViewById(R.id.etUpdateInterval);
        etMinUpdateInterval = findViewById(R.id.etMinUpdateInterval);
        etMinUpdateDistance = findViewById(R.id.etMinUpdateDistance);
        checkStartAtBoot = findViewById(R.id.checkStartAtBoot);
        toggleServiceButton = findViewById(R.id.serviceButton);
        TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        Button btnApplySettings = findViewById(R.id.btnApplySettings);

        btnApplySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savePreferences();

                if (isServiceRunning()){
                    startGPSService(ServiceSignal.PARAMS_CHANGED);
                }

            }
        });

        tvDeviceId.setText("Device Id: "+getDeviceId());

        etUpdateInterval.setText(LocationParams.getUpdateInterval(this).toString());

        etMinUpdateInterval.setText(LocationParams.getMinUpdateInterval(this).toString());

        etMinUpdateDistance.setText(
                Float.toString(LocationParams.getMinUpdateDistance(this))
        );

        checkStartAtBoot.setChecked(
                LocationParams.startServiceOnBoot(this)
        );

        setServiceButtonState( isServiceRunning() );
//        askOverlayPermission();
        askIgnoreBatteryOptimization();
        //askForPermissions();
        toggleServiceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if ((b)) {
                    startGPSService(ServiceSignal.SERVICE_STARTED_BY_USER);
                } else {
                    stopGPSService();
                }
                setServiceButtonState(b);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        boolean notificationsEnabled = notificationManager.areNotificationsEnabled();

        setServiceButtonState( isServiceRunning() );

        Log.d(className,"ON RESUME");
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
    private void askTurnOffPowerSaver() {

        String packageName = getPackageName();

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

    private void setServiceButtonState(boolean b) {

        if (b) {
            toggleServiceButton.setChecked(true);
            toggleServiceButton.setText("Service is ON");
            toggleServiceButton.setTextColor(Color.GREEN);
        } else {
            toggleServiceButton.setText("Service is OFF");
            toggleServiceButton.setTextColor(Color.GRAY);
            toggleServiceButton.setChecked(false);
        }

    }

    private void stopGPSService() {

        if (serviceIntent == null) {
            serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
        }

        stopService(serviceIntent);
        setServiceButtonState(false);
        serviceIntent = null;
    }

    private  void savePreferences(){
        LocationParams.savePreferences(this,
                checkStartAtBoot.isChecked(),
                Long.parseLong(String.valueOf(etUpdateInterval.getText())),
                Long.parseLong(String.valueOf(etMinUpdateInterval.getText())),
                Float.parseFloat(String.valueOf(etMinUpdateDistance.getText()))
        );
    }

    private void startGPSService(String msg) {

        savePreferences();

        if (checkPermissions() ){
            serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
            serviceIntent.putExtra("SIGNAL",msg);
            startForegroundService(serviceIntent);
            setServiceButtonState(true);
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
            }
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