package maryan.stoykov.gpslocation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

import maryan.stoykov.gpslocation.EventListeners.ReportRequestResponseListener;
import maryan.stoykov.gpslocation.Models.ReportResponse;

public class ReportActivity extends AppCompatActivity implements ReportRequestResponseListener {

    Button btnClose;
    TextView tvFrom;
    TextView tvTo;
    TextView tvInside;
    TextView tvOutside;
    TextView tvGeozone;
    ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        initTextViews();

        int month = getIntent().getIntExtra("Month",1);

        setDateRange(month);

        String deviceId = getDeviceId();

                TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        tvDeviceId.setText("Device ID: "+deviceId.toUpperCase());

        btnClose = findViewById(R.id.btnClose);
        btnClose.setEnabled(false);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ReportRequest reportRequest = new ReportRequest(this);

        reportRequest.sendRequest(deviceId, month);
    }
    @SuppressLint("SetTextI18n")
    void setDateRange(int month){

        String strMonth = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);

        Calendar calendar = Calendar.getInstance();

        int currentMonth = calendar.get(Calendar.MONTH);

        int year = calendar.get(Calendar.YEAR);

        tvFrom.setText("01."+strMonth+"."+Integer.toString(year));

        if ( (currentMonth+1) == month){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            tvTo.setText(formatter.format(date));
        } else {
            YearMonth yearMonthObject = YearMonth.of(year, month);
            int daysInMonth = yearMonthObject.lengthOfMonth(); //28
            tvTo.setText(daysInMonth+"."+strMonth+"."+year);
        }

    }
    private void initTextViews(){
        tvFrom = findViewById(R.id.tvFrom);
        tvTo = findViewById(R.id.tvTo);
        tvInside = findViewById(R.id.tvInside);
        tvOutside = findViewById(R.id.tvOutside);
        tvGeozone = findViewById(R.id.tvGeozone);
    }
    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onReportResponse(int responseCode, ReportResponse reportResponse) {
        Log.i("REPORT RESPONSE RECEIVED","CODE: "+responseCode+" "+reportResponse.toString());
        tvGeozone.setText(reportResponse.getGeozone_name());
        tvInside.setText(reportResponse.getInzone_time());
        tvOutside.setText(reportResponse.getExcuse_time());
        btnClose.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}