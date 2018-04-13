package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.controller.TempWeightController;
import my.com.engpeng.engpeng.controller.TempWeightDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempWeightHeadActivity extends AppCompatActivity {

    private String dateStr, timeStr;
    private TextView tvMonthDay, tvTime, tvYear;
    private EditText etFeed;
    private Button btnDate, btnTime, btnStart;
    private RadioGroup rgDay;

    private Calendar calender;
    private int year, month, day, hour, minute;
    private int company_id, location_id, house_code;
    private SimpleDateFormat sdf, sdfMonthDay, sdfTime, sdfYear;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_weight_head);

        tvMonthDay = findViewById(R.id.temp_weight_head_tv_month_day);
        tvTime = findViewById(R.id.temp_weight_head_tv_time);
        tvYear = findViewById(R.id.temp_weight_head_tv_year);
        etFeed = findViewById(R.id.temp_weight_head_et_feed);

        btnDate = findViewById(R.id.temp_weight_head_btn_date);
        btnTime = findViewById(R.id.temp_weight_head_btn_time);
        btnStart = findViewById(R.id.temp_weight_head_btn_start);

        rgDay = findViewById(R.id.temp_weight_head_rg_day);

        calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);
        hour = calender.get(Calendar.HOUR_OF_DAY);
        minute = calender.get(Calendar.MINUTE);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdfMonthDay = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        sdfYear = new SimpleDateFormat("yyyy", Locale.US);
        sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

        tvYear.setText(sdfYear.format(calender.getTime()));
        tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
        tvTime.setText(sdfTime.format(calender.getTime()));
        dateStr = sdf.format(calender.getTime());
        timeStr = sdfTime.format(calender.getTime());

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }
        company_id = Global.sCompanyId;
        location_id = Global.sLocationId;

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("New Body Weight for "+sLocationName+" H#" + String.valueOf(house_code));

        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TempWeightController.delete(db);
        TempWeightDetailController.delete(db);
    }


    private void setupListener() {

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempWeightHeadActivity.this,
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

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(TempWeightHeadActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int h, int m) {
                                calender.set(Calendar.HOUR_OF_DAY, h);
                                calender.set(Calendar.MINUTE, m);
                                tvTime.setText(sdfTime.format(calender.getTime()));
                                timeStr = sdfTime.format(calender.getTime());
                            }
                        }, hour, minute, false);
                tpd.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = new Toast(TempWeightHeadActivity.this);
                if (toast != null) {
                    toast.cancel();
                }

                if (etFeed.getText().length() == 0) {
                    etFeed.setError(getString(R.string.error_field_required));
                    etFeed.requestFocus();
                    return;
                }

                String feed = etFeed.getText().toString();

                int selectedRB = rgDay.getCheckedRadioButtonId();
                if (selectedRB == -1) {
                    toast = Toast.makeText(TempWeightHeadActivity.this, "Please select day", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                int day = 1;
                if (selectedRB == R.id.temp_weight_head_rd_day1) {
                    day = 1;
                } else if (selectedRB == R.id.temp_weight_head_rd_day7) {
                    day = 7;
                } else if (selectedRB == R.id.temp_weight_head_rd_day14) {
                    day = 14;
                } else if (selectedRB == R.id.temp_weight_head_rd_day21) {
                    day = 21;
                } else if (selectedRB == R.id.temp_weight_head_rd_day28) {
                    day = 28;
                }

                TempWeightController.add(db, company_id, location_id, house_code, day, dateStr, timeStr, feed);

                Intent sumIntent = new Intent(TempWeightHeadActivity.this, TempWeightSummaryActivity.class);
                sumIntent.putExtra(I_KEY_COMPANY, company_id);
                sumIntent.putExtra(I_KEY_LOCATION, location_id);
                sumIntent.putExtra(I_KEY_HOUSE_CODE, house_code);
                startActivity(sumIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weight_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_weight_history) {

            Intent weightHistoryIntent = new Intent(this, WeightHistoryActivity.class);
            weightHistoryIntent.putExtra(I_KEY_HOUSE_CODE, house_code);
            startActivity(weightHistoryIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
