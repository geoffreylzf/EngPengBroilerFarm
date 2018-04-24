package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.adapter.MortalityHistoryAdapter;
import my.com.engpeng.engpeng.adapter.WeightReportAdapter;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

public class WeightReportActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    private Button btnChangeDate;
    private TextView tvYear, tvMonthDay;

    private Calendar calender;
    private int mYear, mMonth, mDay;

    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;
    private String dateStr;

    private WeightReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_report);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        btnChangeDate = findViewById(R.id.weight_report_btn_change_date);
        tvYear = findViewById(R.id.year_text_view);
        tvMonthDay = findViewById(R.id.month_day_text_view);

        calender = Calendar.getInstance();
        mYear = calender.get(Calendar.YEAR);
        mMonth = calender.get(Calendar.MONTH);
        mDay = calender.get(Calendar.DAY_OF_MONTH);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdfYear = new SimpleDateFormat("yyyy", Locale.US);
        sdfMonthDay = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        tvYear.setText(sdfYear.format(calender.getTime()));
        tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
        dateStr = sdf.format(calender.getTime());

        setTitle("Body Weight Report");

        setupListener();

        setupRecycleView();

    }

    private void setupListener() {
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(WeightReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                calender.set(Calendar.YEAR, year);
                                calender.set(Calendar.MONTH, monthOfYear);
                                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                tvYear.setText(sdfYear.format(calender.getTime()));
                                tvMonthDay.setText(sdfMonthDay.format(calender.getTime()));
                                dateStr = sdf.format(calender.getTime());
                                refreshRecycleView();
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
    }

    private void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.weight_report_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = WeightController.getAllByDate(db, dateStr);

        adapter = new WeightReportAdapter(this, cursor, db);
        rv.setAdapter(adapter);
    }

    private void refreshRecycleView(){
        adapter.swapCursor(WeightController.getAllByDate(db, dateStr));
    }
}
