package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import my.com.engpeng.engpeng.adapter.TempFeedDischargeSummaryAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedInSummaryAdapter;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempFeedDischargeSummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDischargeCode, tvTruckCode;
    private RecyclerView rv;

    private int company_id, location_id;
    private String truck_code, discharge_code;
    private SQLiteDatabase db;
    private Toast mToast;
    private TempFeedDischargeSummaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_discharge_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();
        mToast = new Toast(this);

        fabAdd = findViewById(R.id.temp_feed_discharge_summary_fab_add);
        btnEnd = findViewById(R.id.temp_feed_discharge_summary_btn_end);

        tvLocation = findViewById(R.id.temp_feed_discharge_summary_tv_location_name);
        tvDischargeCode = findViewById(R.id.temp_feed_discharge_summary_tv_discharge_code);
        tvTruckCode = findViewById(R.id.temp_feed_discharge_summary_tv_truck_code);

        setupStartIntent();
        setupSummary();

        setTitle("New Feed Discharge Summary");
    }

    private void setupStartIntent(){
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
        if (intentStart.hasExtra(I_KEY_DISCHARGE_CODE)) {
            discharge_code = intentStart.getStringExtra(I_KEY_DISCHARGE_CODE);
        }
    }

    public void setupSummary() {
        tvLocation.setText("Location : " + sLocationName);
        tvDischargeCode.setText("Doc Number : " + discharge_code);
        tvTruckCode.setText("Truck Code : " + truck_code);
    }
}
