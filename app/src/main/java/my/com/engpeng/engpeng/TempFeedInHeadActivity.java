package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.adapter.TempFeedInItemSelectionAdapter;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempFeedInHeadActivity extends AppCompatActivity {

    private String dateStr;
    private Button btnDate, btnStart;
    private TextView tvYear, tvMonthDay;
    private EditText etDocNumber, etTruckCode;
    private RadioGroup rgType;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private SQLiteDatabase db;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_in_head);

        btnDate = findViewById(R.id.temp_feed_in_head_btn_change_date);
        btnStart = findViewById(R.id.temp_feed_in_head_btn_start);
        tvYear = findViewById(R.id.temp_feed_in_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_feed_in_head_tv_month_day);
        etDocNumber = findViewById(R.id.temp_feed_in_head_et_doc_number);
        rgType = findViewById(R.id.temp_feed_in_head_rg_type);
        etTruckCode = findViewById(R.id.temp_feed_in_head_et_truck_code);

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
        db = dbHelper.getWritableDatabase();
        toast = new Toast(this);

        setTitle("New Feed IN for " + sLocationName);

        setupListener();
    }

    private void setupListener() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempFeedInHeadActivity.this,
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

                if (toast != null) {
                    toast.cancel();
                }

                if (etDocNumber.getText().length() == 0) {
                    etDocNumber.setError(getString(R.string.error_field_required));
                    etDocNumber.requestFocus();
                    return;
                }
                int doc_number = Integer.parseInt(etDocNumber.getText().toString());

                String type = "";
                int selectedRB = rgType.getCheckedRadioButtonId();
                if (selectedRB == -1) {
                    toast = Toast.makeText(TempFeedInHeadActivity.this, "Please select type", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {
                    if (selectedRB == R.id.temp_feed_in_head_rd_bg) {
                        type = "BG";
                    } else if (selectedRB == R.id.temp_feed_in_head_rd_mt) {
                        type = "MT";
                    }
                }

                if (etTruckCode.getText().length() == 0) {
                    etTruckCode.setError(getString(R.string.error_field_required));
                    etTruckCode.requestFocus();
                    return;
                }
                String truck_code = etTruckCode.getText().toString();


                /*if(!CatchBTAController.checkDocNumber(db, doc_number, doc_type)){
                    toast = Toast.makeText(TempCatchBTAHeadActivity.this, "Document number and destination duplicate", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }*/


                //TempCatchBTAController.add(db, company_id, location_id, dateStr, type, doc_number, doc_type, truck_code);

                Intent selectionIntent = new Intent(TempFeedInHeadActivity.this, TempFeedInItemSelectionActivity.class);
                selectionIntent.putExtra(I_KEY_COMPANY, company_id);
                selectionIntent.putExtra(I_KEY_LOCATION, location_id);
                selectionIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                selectionIntent.putExtra(I_KEY_DOC_NUMBER, doc_number);
                selectionIntent.putExtra(I_KEY_TYPE, type);
                selectionIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                startActivity(selectionIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_in_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_feed_in_history) {

            Intent catchBTAHistoryIntent = new Intent(this, FeedInHistoryActivity.class);
            startActivity(catchBTAHistoryIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
