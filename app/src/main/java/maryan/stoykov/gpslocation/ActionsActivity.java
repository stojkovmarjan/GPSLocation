package maryan.stoykov.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import java.util.Objects;

public class ActionsActivity extends AppCompatActivity {

    private final String className = this.getClass().getSimpleName();
    private final BroadcastReceiver serviceDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.equals(intent.getAction(), "maryan.stoykov.gpslocation.SEND_DATA")) {
                boolean isRegistered = intent.getBooleanExtra("isRegistered", false);
                Log.d(className, "SERVICE DATA RECEIVER: "+isRegistered);
                if (!isRegistered) {
                    closeActionsActivity();
                }
            }
        }
    };
    @SuppressLint({"HardwareIds", "SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        IntentFilter filter = new IntentFilter("maryan.stoykov.gpslocation.SEND_DATA");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(serviceDataReceiver,filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(serviceDataReceiver,filter);
        }

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        tvDeviceId.setText("Device ID: "+getDeviceId().toUpperCase());
    }

    private void closeActionsActivity(){
        this.finish();
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceDataReceiver);
    }
}