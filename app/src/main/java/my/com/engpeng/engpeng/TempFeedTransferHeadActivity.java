package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpeng.adapter.TempFeedTransferHeadAdapter;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.HouseCode;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_HOUSE;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.I_KEY_RECEIVE_HOUSE;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempFeedTransferHeadActivity extends AppCompatActivity {

    private String dateStr;
    private Button btnDate, btnStart;
    private TextView tvYear, tvMonthDay;
    private RecyclerView rvDischarge, rvReceive;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private TempFeedTransferHeadAdapter mDischargeAdapter, mReceiveAdapter;
    private List<HouseCode> mDischargeList, mReceiveList;

    private SQLiteDatabase mDb;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_transfer_head);

        btnDate = findViewById(R.id.temp_feed_transfer_head_btn_change_date);
        btnStart = findViewById(R.id.temp_feed_transfer_head_btn_start);
        tvYear = findViewById(R.id.temp_feed_transfer_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_feed_transfer_head_tv_month_day);
        rvDischarge = findViewById(R.id.temp_feed_transfer_head_rv_discharge);
        rvReceive = findViewById(R.id.temp_feed_transfer_head_rv_receive);

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
        mToast = new Toast(this);

        setTitle("New Feed Transfer for " + sLocationName);

        setupListener();
        setupDischargeRecycleView();
        setupReceiveRecycleView();
    }

    private void setupListener() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TempFeedTransferHeadActivity.this,
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
            public void onClick(View v) {
                if (mToast != null) {
                    mToast.cancel();

                    int house_discharge = 0, house_receive = 0;

                    for (HouseCode hc : mDischargeList) {
                        if (hc.getIsSelect()) {
                            house_discharge = hc.getHouseCode();
                            break;
                        }
                    }
                    if (house_discharge == 0) {
                        mToast = Toast.makeText(TempFeedTransferHeadActivity.this, "Please select discharge house", Toast.LENGTH_SHORT);
                        mToast.show();
                        return;
                    }

                    for (HouseCode hc : mReceiveList) {
                        if (hc.getIsSelect()) {
                            house_receive = hc.getHouseCode();
                            break;
                        }
                    }
                    if (house_receive == 0) {
                        mToast = Toast.makeText(TempFeedTransferHeadActivity.this, "Please select receive house", Toast.LENGTH_SHORT);
                        mToast.show();
                        return;
                    }

                    if (house_discharge == house_receive) {
                        mToast = Toast.makeText(TempFeedTransferHeadActivity.this, "Discharge and receive cannot be same house", Toast.LENGTH_SHORT);
                        mToast.show();
                        return;
                    }

                    Intent selectionIntent = new Intent(TempFeedTransferHeadActivity.this, TempFeedTransferDetailActivity.class);
                    selectionIntent.putExtra(I_KEY_COMPANY, company_id);
                    selectionIntent.putExtra(I_KEY_LOCATION, location_id);
                    selectionIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                    selectionIntent.putExtra(I_KEY_DISCHARGE_HOUSE, house_discharge);
                    selectionIntent.putExtra(I_KEY_RECEIVE_HOUSE, house_receive);
                    startActivity(selectionIntent);
                }
            }
        });
    }

    public void setupDischargeRecycleView() {
        rvDischarge.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = HouseController.getHouseCodeByLocationId(mDb, location_id);

        mDischargeList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int house_code = cursor.getInt(cursor.getColumnIndex(EngPengContract.HouseEntry.COLUMN_HOUSE_CODE));

            HouseCode hc = new HouseCode();
            hc.setHouseCode(house_code);

            mDischargeList.add(hc);
        }
        mDischargeAdapter = new TempFeedTransferHeadAdapter(this, mDischargeList);
        rvDischarge.setAdapter(mDischargeAdapter);
    }

    public void setupReceiveRecycleView() {
        rvReceive.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = HouseController.getHouseCodeByLocationId(mDb, location_id);

        mReceiveList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int house_code = cursor.getInt(cursor.getColumnIndex(EngPengContract.HouseEntry.COLUMN_HOUSE_CODE));

            HouseCode hc = new HouseCode();
            hc.setHouseCode(house_code);

            mReceiveList.add(hc);
        }
        mReceiveAdapter = new TempFeedTransferHeadAdapter(this, mReceiveList);
        rvReceive.setAdapter(mReceiveAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_transfer_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_feed_transfer_history) {

            Intent intent = new Intent(this, FeedTransferHistoryActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
