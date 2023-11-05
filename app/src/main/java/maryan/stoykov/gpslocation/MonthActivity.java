package maryan.stoykov.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class MonthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final String[] months = new String[]{"Select Month","January","February","March","April","May",
            "June","July","August","September","October","November","December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        tvDeviceId.setText("Device ID: "+getDeviceId().toUpperCase());

        TextView reportYear = findViewById(R.id.tvReportYear);
        reportYear.setText("Report: "+year);

        Spinner spMonth = findViewById(R.id.spMonth);
        spMonth.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, months);

        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spMonth.setAdapter(arrayAdapter);
    }
    @SuppressLint("HardwareIds")
    private String getDeviceId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position>0 && position<= getCurrentMonthIndex()){
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("Month",position);
            startActivity(intent);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int getCurrentMonthIndex(){
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }
}
