package maryan.stoykov.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class ActionsActivity extends AppCompatActivity {

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        tvDeviceId.setText("Device ID: "+getDeviceId().toUpperCase());
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
        //System.exit(0);
    }
}