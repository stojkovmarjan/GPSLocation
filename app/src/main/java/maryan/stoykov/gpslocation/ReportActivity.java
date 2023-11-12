package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    Button btnClose;
    TextView tvFrom;
    TextView tvTo;
    TextView tvInside;
    TextView tvOutside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        int month = getIntent().getIntExtra("Month",1);
        Log.i("MONTH: ", month+"");

        String deviceId = getDeviceId();

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        tvDeviceId.setText("Device ID: "+deviceId.toUpperCase());

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
//    void setDateRange(int month){
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//
//        YearMonth yearMonthObject = YearMonth.of(1999, month);
//        int daysInMonth = yearMonthObject.lengthOfMonth(); //28
//    }

    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}