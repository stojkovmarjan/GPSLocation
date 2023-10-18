package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ActionsActivity extends AppCompatActivity {

    private final String className = this.getClass().getSimpleName();

    private Button btnStart ;
    private Button btnStop ;
    private Button btnReport ;
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
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button clickedButton = (Button) view;
            Log.d(className,"CLICKED BUTTON "+clickedButton.getText());
            String btnText = ""+clickedButton.getText();

            Intent serviceIntent = new Intent(getApplicationContext(), GPSStickyService.class);
            String msg = "";

            switch (btnText){
                case "START":
                    msg = ServiceSignal.START_BUTTON_CLICKED;
                    break;
                case "STOP":
                    msg = ServiceSignal.STOP_BUTTON_CLICKED;
                    break;
                case "REPORT":
                    msg = ServiceSignal.REPORT_BUTTON_CLICKED;
                    break;
                default: break;
            }
            serviceIntent.putExtra("SIGNAL",msg);
            startForegroundService(serviceIntent);
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

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(onClickListener);

        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(onClickListener);

        btnReport = findViewById(R.id.btnReport);
        btnReport.setOnClickListener(onClickListener);

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