package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.TempFeedDischargeDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_RUNNING_NO;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.Global.sUsername;

public class TempFeedDischargeHeadActivity extends AppCompatActivity {

    private String dateStr;

    private Button btnDate, btnStart;
    private TextView tvYear, tvMonthDay;
    private EditText etTruckCode;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_discharge_head);

        btnDate = findViewById(R.id.temp_feed_discharge_head_btn_change_date);
        btnStart = findViewById(R.id.temp_feed_discharge_head_btn_start);
        tvYear = findViewById(R.id.temp_feed_discharge_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_feed_discharge_head_tv_month_day);
        etTruckCode = findViewById(R.id.temp_feed_discharge_head_et_truck_code);

        calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdfYear = new SimpleDateFormat("yyyy", Locale.US);
        sdfMonthDay = new SimpleDateFormat("EEE, MMM dd", Locale.US);

        tvYear.setText(sdfYear.format(calender.getTime()));
        tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
        dateStr = sdf.format(calender.getTime());

        company_id = Global.sCompanyId;
        location_id = Global.sLocationId;

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        setTitle("New Feed Discharge for " + sLocationName);

        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TempFeedDischargeDetailController.delete(mDb);
    }

    private void setupListener() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempFeedDischargeHeadActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calender.set(Calendar.YEAR, year);
                                calender.set(Calendar.MONTH, monthOfYear);
                                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                tvYear.setText(sdfYear.format(calender.getTime()));
                                tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
                                dateStr = sdf.format(calender.getTime());
                            }
                        }, year, month, day);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etTruckCode.getText().length() == 0) {
                    etTruckCode.setError(getString(R.string.error_field_required));
                    etTruckCode.requestFocus();
                    return;
                }
                String truck_code = etTruckCode.getText().toString();

                SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
                Calendar c = Calendar.getInstance();
                String discharge_code = sdfDateTime.format(c.getTime()) + String.format(Locale.US, "%04d%04d", company_id, location_id);

                String running_no = "D-" + sUsername + "-1";
                String last_running_no = FeedDischargeController.getLastRunningNo(mDb);
                if (!last_running_no.equals("")) {
                    String[] arr = last_running_no.split("-");
                    int new_no = Integer.parseInt(arr[2]) + 1;
                    running_no = "D-" + sUsername + "-" + new_no;
                }

                Intent sumIntent = new Intent(TempFeedDischargeHeadActivity.this, TempFeedDischargeSummaryActivity.class);
                sumIntent.putExtra(I_KEY_COMPANY, company_id);
                sumIntent.putExtra(I_KEY_LOCATION, location_id);
                sumIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                sumIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                sumIntent.putExtra(I_KEY_DISCHARGE_CODE, discharge_code);
                sumIntent.putExtra(I_KEY_RUNNING_NO, running_no);
                startActivity(sumIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_discharge_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_feed_discharge_history) {

            Intent intent = new Intent(this, FeedDischargeHistoryActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
