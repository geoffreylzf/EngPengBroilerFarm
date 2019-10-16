package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.barcode.BarcodeCaptureActivity;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_TYPE;
import static my.com.engpeng.engpeng.Global.I_KEY_FASTING_TIME;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_FIELD;
import static my.com.engpeng.engpeng.Global.REQUEST_CODE_BARCODE_CAPTURE;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempCatchBTAHeadActivity extends AppCompatActivity {

    private String dateStr;

    private Button btnDate, btnStart, btnScan;
    private TextView tvYear, tvMonthDay;
    private EditText etDocNumber, etTruckCode, etFastingTimeHour, etFastingTimeMinute;
    private RadioGroup rgDestination, rgType, rgFastingTime;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_catch_bta_head);

        btnDate = findViewById(R.id.temp_catch_bta_head_btn_change_date);
        btnStart = findViewById(R.id.temp_catch_bta_head_btn_start);
        btnScan = findViewById(R.id.temp_catch_bta_head_btn_scan);
        tvYear = findViewById(R.id.temp_catch_bta_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_catch_bta_head_tv_month_day);
        etDocNumber = findViewById(R.id.temp_catch_bta_head_et_doc_number);
        rgDestination = findViewById(R.id.temp_catch_bta_head_rg_destination);
        rgType = findViewById(R.id.temp_catch_bta_head_rg_type);
        etTruckCode = findViewById(R.id.temp_catch_bta_head_et_truck_code);

        etFastingTimeHour = findViewById(R.id.temp_catch_bta_head_et_fasting_time_hour);
        etFastingTimeMinute = findViewById(R.id.temp_catch_bta_head_et_fasting_time_minute);
        rgFastingTime = findViewById(R.id.temp_catch_bta_head_rg_fasting_time);

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

        setTitle("New Catch BTA for " + sLocationName);

        setupListener();
    }

    protected void onStart() {
        super.onStart();
    }

    private void setupListener() {

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempCatchBTAHeadActivity.this,
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

                Toast toast = new Toast(TempCatchBTAHeadActivity.this);
                if (toast != null) {
                    toast.cancel();
                }

                if (etDocNumber.getText().length() == 0) {
                    etDocNumber.setError(getString(R.string.error_field_required));
                    etDocNumber.requestFocus();
                    return;
                }
                int doc_number = Integer.parseInt(etDocNumber.getText().toString());

                String doc_type = "";
                int selectedRB = rgDestination.getCheckedRadioButtonId();
                if (selectedRB == -1) {
                    toast = Toast.makeText(TempCatchBTAHeadActivity.this, "Please select destination", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {
                    if (selectedRB == R.id.temp_catch_bta_head_rd_customer) {
                        doc_type = "PL";
                    } else if (selectedRB == R.id.temp_catch_bta_head_rd_slaughterhouse) {
                        doc_type = "IFT";
                    } else if (selectedRB == R.id.temp_catch_bta_head_rd_op) {
                        doc_type = "OP";
                    }
                }

                String type = "";
                selectedRB = rgType.getCheckedRadioButtonId();
                if (selectedRB == -1) {
                    toast = Toast.makeText(TempCatchBTAHeadActivity.this, "Please select type", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {
                    if (selectedRB == R.id.temp_catch_bta_head_rd_kfc) {
                        type = "KFC";
                    } else if (selectedRB == R.id.temp_catch_bta_head_rd_grade_a) {
                        type = "A";
                    } else if (selectedRB == R.id.temp_catch_bta_head_rd_grade_b) {
                        type = "B";
                    }
                }

                if (etTruckCode.getText().length() == 0) {
                    etTruckCode.setError(getString(R.string.error_field_required));
                    etTruckCode.requestFocus();
                    return;
                }
                String truck_code = etTruckCode.getText().toString();


                if (!CatchBTAController.checkDocNumber(db, doc_number, doc_type)) {
                    toast = Toast.makeText(TempCatchBTAHeadActivity.this, "Document number and destination duplicate", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                String fasting_time = "";

                if (etFastingTimeHour.getText().length() != 0 && etFastingTimeMinute.getText().length() != 0) {
                    int rgSelectedFastingTime = rgFastingTime.getCheckedRadioButtonId();
                    if (rgSelectedFastingTime != -1) {
                        String fasting_time_convention = "";
                        if (rgSelectedFastingTime == R.id.temp_catch_bta_head_rd_fasting_time_am) {
                            fasting_time_convention = "AM";
                        } else if (rgSelectedFastingTime == R.id.temp_catch_bta_head_rd_fasting_time_pm) {
                            fasting_time_convention = "PM";
                        }

                        fasting_time = etFastingTimeHour.getText().toString() + ":" + etFastingTimeMinute.getText().toString() + " " + fasting_time_convention;
                    }
                }

                Intent sumIntent = new Intent(TempCatchBTAHeadActivity.this, TempCatchBTASummaryActivity.class);
                sumIntent.putExtra(I_KEY_COMPANY, company_id);
                sumIntent.putExtra(I_KEY_LOCATION, location_id);
                sumIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                sumIntent.putExtra(I_KEY_TYPE, type);
                sumIntent.putExtra(I_KEY_DOC_NUMBER, doc_number);
                sumIntent.putExtra(I_KEY_DOC_TYPE, doc_type);
                sumIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                sumIntent.putExtra(I_KEY_FASTING_TIME, fasting_time);
                startActivity(sumIntent);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempCatchBTAHeadActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                startActivityForResult(intent, REQUEST_CODE_BARCODE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String qr_data = barcode.displayValue;

                    try {

                        String[] fields = qr_data.split(QR_SPLIT_FIELD);

                        String companyId = fields[0];
                        String locationId = fields[1];
                        String docDate = fields[2];
                        String docNo = fields[3];
                        String docType = fields[4];
                        String harvestType = fields[5];
                        String truckCode = fields[6];

//                        Intent sumIntent = new Intent(TempCatchBTAHeadActivity.this, TempCatchBTASummaryActivity.class);
//                        sumIntent.putExtra(I_KEY_COMPANY, company_id);
//                        sumIntent.putExtra(I_KEY_LOCATION, location_id);
//                        sumIntent.putExtra(I_KEY_RECORD_DATE, docDate);
//                        sumIntent.putExtra(I_KEY_TYPE, harvestType);
//                        sumIntent.putExtra(I_KEY_DOC_NUMBER, docNo);
//                        sumIntent.putExtra(I_KEY_DOC_TYPE, docType);
//                        sumIntent.putExtra(I_KEY_TRUCK_CODE, truckCode);
//                        startActivity(sumIntent);

                        etDocNumber.setText(docNo);

                        switch (docType) {
                            case "PL":
                                rgDestination.check(R.id.temp_catch_bta_head_rd_customer);
                                break;
                            case "IFT":
                            case "OP":
                                rgDestination.check(R.id.temp_catch_bta_head_rd_slaughterhouse);
                                break;
                        }

                        switch (harvestType) {
                            case "KFC":
                                rgType.check(R.id.temp_catch_bta_head_rd_kfc);
                                break;
                            case "A":
                                rgType.check(R.id.temp_catch_bta_head_rd_grade_a);
                                break;
                            case "B":
                                rgType.check(R.id.temp_catch_bta_head_rd_grade_b);
                                break;
                        }

                        etTruckCode.setText(truckCode);

                    } catch (Exception e) {
                        UIUtils.showToastMessage(this, "Invalid QR");
                    }
                } else {
                    UIUtils.showToastMessage(this, "No barcode captured");
                }
            } else {
                UIUtils.showToastMessage(this, "Fail to read barcode");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catch_bta_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_catch_bta_history) {

            Intent catchBTAHistoryIntent = new Intent(this, CatchBTAHistoryActivity.class);
            startActivity(catchBTAHistoryIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
