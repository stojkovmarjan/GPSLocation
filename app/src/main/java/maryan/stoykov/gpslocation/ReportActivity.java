package maryan.stoykov.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.time.YearMonth;
import java.util.Calendar;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        int month = getIntent().getIntExtra("Month",0);
        Log.i("MONTH: ", month+"");

        String deviceId = getDeviceId();

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        tvDeviceId.setText("Device ID: "+deviceId.toUpperCase());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        YearMonth yearMonthObject = YearMonth.of(1999, 2);
        int daysInMonth = yearMonthObject.lengthOfMonth(); //28



    }

    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}