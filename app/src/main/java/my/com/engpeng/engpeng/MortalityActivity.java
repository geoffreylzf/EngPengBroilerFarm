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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.*;

public class MortalityActivity extends AppCompatActivity {

    private String dateStr;
    private int house_code;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortality);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }

        setTitle("New Mortality for " + sLocationName + " H#" + String.valueOf(house_code));

        Button btnChangeDate = findViewById(R.id.mortality_btn_change_date);
        Button btnSave = findViewById(R.id.mortality_btn_save);

        final TextView tvYear = findViewById(R.id.year_text_view);
        final TextView tvMonthDay = findViewById(R.id.month_day_text_view);

        final EditText etMQ = findViewById(R.id.mortality_et_m_q);
        final EditText etRQ = findViewById(R.id.mortality_et_r_q);

        final Calendar calender = Calendar.getInstance();
        final int mYear = calender.get(Calendar.YEAR);
        final int mMonth = calender.get(Calendar.MONTH);
        final int mDay = calender.get(Calendar.DAY_OF_MONTH);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.US);
        final SimpleDateFormat sdfMonthDay = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        tvYear.setText(sdfYear.format(calender.getTime()));
        tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
        dateStr = sdf.format(calender.getTime());

        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(MortalityActivity.this,
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
                        }, mYear, mMonth, mDay);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
                dpd.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = new Toast(MortalityActivity.this);
                int m_q, r_q;
                if (toast != null) {
                    toast.cancel();
                }

                if (etMQ.getText().length() == 0) {
                    etMQ.setError(getString(R.string.error_field_required));
                    etMQ.requestFocus();
                    return;
                } else {
                    try {
                        m_q = Integer.parseInt(etMQ.getText().toString());
                    } catch (NumberFormatException ex) {
                        etMQ.setError(getString(R.string.error_field_number_only));
                        etMQ.requestFocus();
                        return;
                    }
                }

                if (etRQ.getText().length() == 0) {
                    etRQ.setError(getString(R.string.error_field_required));
                    etRQ.requestFocus();
                    return;
                } else {
                    try {
                        r_q = Integer.parseInt(etRQ.getText().toString());
                    } catch (NumberFormatException ex) {
                        etRQ.setError(getString(R.string.error_field_number_only));
                        etRQ.requestFocus();
                        return;
                    }
                }

                //check exist data
                int company_id = sCompanyId;
                int location_id = sLocationId;

                if (MortalityController.check(db, company_id, location_id, house_code, dateStr) > 0) {
                    toast = Toast.makeText(MortalityActivity.this, "Data already entered for the date.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    MortalityController.add(db, company_id, location_id, house_code, dateStr, m_q, r_q);
                    finish();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mortality_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mortality_history) {
            Intent mortalityHistoryIntent = new Intent(this, MortalityHistoryActivity.class);
            mortalityHistoryIntent.putExtra(I_KEY_HOUSE_CODE, house_code);
            startActivity(mortalityHistoryIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
