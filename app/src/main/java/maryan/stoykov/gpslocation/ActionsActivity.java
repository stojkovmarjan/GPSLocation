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

import maryan.stoykov.gpslocation.Models.TrackingProfile;

public class ActionsActivity extends AppCompatActivity {

    private final String className = this.getClass().getSimpleName();
    private final Context context = this;

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
                    Intent intent = new Intent( context, MonthActivity.class);
                    startActivity(intent);
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
        IntentFilter trackingProfileFilter = new IntentFilter("maryan.stoykov.gpslocation.TRACKING_PROFILE");
        registerReceiver(trackingProfileReceiver, trackingProfileFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(serviceDataReceiver,filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(serviceDataReceiver,filter);
        }

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        tvDeviceId.setText("Device ID: "+getDeviceId().toUpperCase());

        Button btnStart = findViewById(R.id.btnStart);
        //here show hide the button according trackingProfile from GPSStickyService
        btnStart.setOnClickListener(onClickListener);

        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(onClickListener);

        Button btnReport = findViewById(R.id.btnReport);
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
    private final BroadcastReceiver trackingProfileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "maryan.stoykov.gpslocation.TRACKING_PROFILE")) {
                TrackingProfile trackingProfile = (TrackingProfile) intent.getSerializableExtra("trackingProfile");

                if (trackingProfile != null) {
                    int showStartButton = trackingProfile.getStartBtnEnabled();
                    int showStopButton = trackingProfile.getStopBtnEnabled();
                    Button btnStart = findViewById(R.id.btnStart);
                    btnStart.setVisibility(showStartButton == 1 ? View.VISIBLE : View.INVISIBLE);
                    Button btnStop = findViewById(R.id.btnStop);
                    btnStop.setVisibility(showStopButton == 1 ? View.VISIBLE : View.INVISIBLE);
                    TextView employeeName = findViewById(R.id.tvEmployeeName);
                    employeeName.setVisibility(showStartButton == 0 ? View.VISIBLE : View.INVISIBLE);
                    employeeName.setText(trackingProfile.getEmployeeName());
                    TextView companyName = findViewById(R.id.tvCompanyName);
                    companyName.setVisibility(showStartButton == 0 ? View.VISIBLE : View.INVISIBLE);
                    companyName.setText(trackingProfile.getCompanyName());
                }
            }
        }
    };

}