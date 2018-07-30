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
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.barcode.BarcodeCaptureActivity;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.QR_LINE_TYPE_HEAD;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_FIELD;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_LINE;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempFeedInHeadActivity extends AppCompatActivity {

    private String dateStr;
    private Button btnDate, btnStart;
    private TextView tvYear, tvMonthDay;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private SQLiteDatabase mDb;

    private static final int RC_BARCODE_CAPTURE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_in_head);

        btnDate = findViewById(R.id.temp_feed_in_head_btn_change_date);
        btnStart = findViewById(R.id.temp_feed_in_head_btn_start);
        tvYear = findViewById(R.id.temp_feed_in_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_feed_in_head_tv_month_day);

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

        setTitle("New Feed IN from factory for " + sLocationName);

        setupListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        TempFeedInDetailController.delete(mDb);
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
                dpd.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 1));
                dpd.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempFeedInHeadActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String qr_data = barcode.displayValue;

                    try {
                        String[] lines = qr_data.split(QR_SPLIT_LINE);

                        String type, doc_no = "", truck_code = "";
                        Long doc_id = null;

                        for (String line : lines) {
                            String[] fields = line.split(QR_SPLIT_FIELD);

                            type = fields[0];
                            doc_id = Long.parseLong(fields[1]);
                            doc_no = fields[2];
                            truck_code = fields[3];

                            if (type.equals(QR_LINE_TYPE_HEAD)) {
                                break;
                            }
                        }

                        Intent selectionIntent = new Intent(TempFeedInHeadActivity.this, TempFeedInSummaryActivity.class);
                        selectionIntent.putExtra(I_KEY_COMPANY, company_id);
                        selectionIntent.putExtra(I_KEY_LOCATION, location_id);
                        selectionIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                        selectionIntent.putExtra(I_KEY_DOC_ID, doc_id);
                        selectionIntent.putExtra(I_KEY_DOC_NUMBER, doc_no);
                        selectionIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                        selectionIntent.putExtra(I_KEY_QR_DATA, qr_data);
                        startActivity(selectionIntent);

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
