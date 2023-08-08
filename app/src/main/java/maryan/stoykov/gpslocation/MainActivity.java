package maryan.stoykov.gpslocation;

import static android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_PERMISSIONS_CODE = 1234;
    final int REQUEST_ACCESS_BACKGROUND_LOCATION_CODE = 1235;
    Intent serviceIntent;
    Button btnStart;
    Button btnStop;
    Button btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Whitelist the restricted permissions.
//        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(MODE_FULL_INSTALL);
//        params.setWhitelistedRestrictedPermissions(new HashSet<>(Arrays.asList("ACCESS_BACKGROUND_LOCATION")));
//
//        PackageInstaller installer = getPackageManager().getPackageInstaller();
//        try {
//            int sessionId = installer.createSession(params);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnAbout = findViewById(R.id.btnAbout);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Toast.makeText( MainActivity.this, deviceId, Toast.LENGTH_LONG).show();
            }
        });

        serviceIntent = new Intent(this, GPSStickyService.class);

        //startForegroundService(serviceIntent);

        if (checkPermissions()){
            startForegroundService(serviceIntent);
        } else {
            askForPermissions();
        }

    }

    public boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET
        };

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
        String[] permissions = {
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET
        };

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e ("GRANT: ", "onReqPer");
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if ( grantResults.length > 0 ) {
                // Permission granted, proceed with using location services
                for (int grantResult: grantResults) {
                    Log.e ("GRANT: ", String.valueOf(grantResult));
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,
                                "Please restart the application and grant all permissions!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                //ActivityCompat.requestPermissions(this, new String[]{"Manifest.permission.ACCESS_BACKGROUND_LOCATION"}
                        //, REQUEST_ACCESS_BACKGROUND_LOCATION_CODE);
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Explain why you need background location to the user
                    // ...
                    // Then, request the background location permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            REQUEST_ACCESS_BACKGROUND_LOCATION_CODE);
                } else {
                    // Request the background location permission without explanation
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            REQUEST_ACCESS_BACKGROUND_LOCATION_CODE);
                }
                //startForegroundService(serviceIntent);
            } else {
                Toast.makeText(this,
                        "Please restart the application and grant all permissions!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        if (requestCode == REQUEST_ACCESS_BACKGROUND_LOCATION_CODE) {
            Log.e("ACCESS_BACKGROUND_LOCATION", "HIT");
            Log.e("ACCESS_BACKGROUND_LOCATION",String.valueOf(grantResults[0]));
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startForegroundService(serviceIntent);
            }
        }
    }
}