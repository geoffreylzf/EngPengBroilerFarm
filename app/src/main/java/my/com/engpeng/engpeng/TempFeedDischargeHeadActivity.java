package my.com.engpeng.engpeng;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import my.com.engpeng.engpeng.adapter.TempFeedDischargeHeadSelectionAdapter;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.LocationController;
import my.com.engpeng.engpeng.controller.TempFeedDischargeDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.LocationAsyncTaskLoader;
import my.com.engpeng.engpeng.model.FeedDischargeLocation;
import my.com.engpeng.engpeng.model.PersonStaffSelection;
import my.com.engpeng.engpeng.model.TempBtaWorker;
import my.com.engpeng.engpeng.utilities.DatabaseUtils;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_LOCATION_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_RUNNING_NO;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.RUNNING_CODE_DISCHARGE;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.Global.sPassword;
import static my.com.engpeng.engpeng.Global.sUsername;

public class TempFeedDischargeHeadActivity
        extends AppCompatActivity
        implements
        AppLoader.AppLoaderListener,
        LocationAsyncTaskLoader.LocationAsyncTaskLoaderListener {

    private String dateStr;

    private Button btnDate, btnRefresh, btnStart;
    private TextView tvYear, tvMonthDay;
    private EditText etTruckCode, etFilter;
    private RecyclerView rvSelection;
    private CheckBox cbLocal;

    private int company_id, location_id;

    private Calendar calender;
    private int year, month, day;
    private SimpleDateFormat sdf, sdfYear, sdfMonthDay;

    private SQLiteDatabase mDb;

    private AppLoader loader;
    private Dialog progressDialog;
    private boolean isFetching = false;

    private TempFeedDischargeHeadSelectionAdapter selectionAdapter;
    private final List<FeedDischargeLocation> feedDischargeLocationList = new ArrayList<FeedDischargeLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_discharge_head);

        btnDate = findViewById(R.id.temp_feed_discharge_head_btn_change_date);
        btnStart = findViewById(R.id.temp_feed_discharge_head_btn_start);
        tvYear = findViewById(R.id.temp_feed_discharge_head_tv_year);
        tvMonthDay = findViewById(R.id.temp_feed_discharge_head_tv_month_day);
        etTruckCode = findViewById(R.id.temp_feed_discharge_head_et_truck_code);

        etFilter = findViewById(R.id.temp_feed_discharge_head_et_filter);
        rvSelection = findViewById(R.id.temp_feed_discharge_head_rv_selection);
        btnRefresh = findViewById(R.id.temp_feed_discharge_head_btn_refresh);
        cbLocal = findViewById(R.id.temp_feed_discharge_head_cb_local);

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

        progressDialog = UIUtils.getProgressDialog(this);
        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.LOCATION_LOADER_ID, null, loader);

        setupRv();
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
                dpd.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 1));
                dpd.show();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFetching) {
                    fetchLocationData();
                }
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshSelectionList();
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

                int dischargeLocationId = 0;
                for (FeedDischargeLocation fdl : feedDischargeLocationList) {
                    if (fdl.isSelect()) {
                        dischargeLocationId = fdl.getId();
                    }
                }

                if (feedDischargeLocationList.size() != 0 && dischargeLocationId == 0) {
                    UIUtils.getMessageDialog(TempFeedDischargeHeadActivity.this,
                            "Error",
                            "Please select location\n" +
                                    "Sila pilih lokasi untuk feed hantar").show();
                    return;
                }


                String truck_code = etTruckCode.getText().toString();

                SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
                Calendar c = Calendar.getInstance();
                String discharge_code = sdfDateTime.format(c.getTime()) + String.format(Locale.US, "%04d%04d", company_id, location_id);

                String running_no = RUNNING_CODE_DISCHARGE + "-" + sUsername + "-1";
                String last_running_no = FeedDischargeController.getLastRunningNo(mDb);
                if (!last_running_no.equals("")) {
                    String[] arr = last_running_no.split("-");
                    int new_no = Integer.parseInt(arr[2]) + 1;
                    running_no = RUNNING_CODE_DISCHARGE + "-" + sUsername + "-" + new_no;
                }

                Intent sumIntent = new Intent(TempFeedDischargeHeadActivity.this, TempFeedDischargeSummaryActivity.class);
                sumIntent.putExtra(I_KEY_COMPANY, company_id);
                sumIntent.putExtra(I_KEY_LOCATION, location_id);
                sumIntent.putExtra(I_KEY_RECORD_DATE, dateStr);
                sumIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                sumIntent.putExtra(I_KEY_DISCHARGE_CODE, discharge_code);
                sumIntent.putExtra(I_KEY_DISCHARGE_LOCATION_ID, dischargeLocationId);
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

    @Override
    public void onBackPressed() {
        if (!isFetching) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void setupRv() {
        rvSelection.setLayoutManager(new LinearLayoutManager(this));
        selectionAdapter = new TempFeedDischargeHeadSelectionAdapter(
                this,
                feedDischargeLocationList
        );
        rvSelection.setAdapter(selectionAdapter);

        refreshSelectionList();
    }

    private void fetchLocationData() {
        String username = sUsername;
        String password = sPassword;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);

        if (cbLocal.isChecked()) {
            queryBundle.putBoolean(AppLoader.LOADER_IS_LOCAL, true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> psLoader = loaderManager.getLoader(Global.LOCATION_LOADER_ID);

        if (psLoader == null) {
            loaderManager.initLoader(Global.LOCATION_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.LOCATION_LOADER_ID, queryBundle, loader);
        }
    }

    @Override
    public void beforeLocationAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new LocationAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (json != null && !json.equals("")) {
            if (JsonUtils.getAuthentication(this, json)) {
                new TempFeedDischargeHeadActivity.InsertLocationDataTask().execute(json);
            }
        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to get location").show();
        }
    }

    private class InsertLocationDataTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            isFetching = true;
            String json = strings[0];
            System.out.println(json);
            ContentValues[] cvs = JsonUtils.getLocationContentValues(json);
            if (cvs != null && cvs.length != 0) {
                LocationController.deleteAll(mDb);
                DatabaseUtils.insertLocation(mDb, cvs);

                TempFeedDischargeHeadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshSelectionList();
                    }
                });
            } else {
                TempFeedDischargeHeadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.getMessageDialog(TempFeedDischargeHeadActivity.this,
                                "Error",
                                "No location retrieved!").show();
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            isFetching = false;
        }
    }

    private void refreshSelectionList() {
        Cursor cursorPs = LocationController.getAllByFilter(mDb, etFilter.getText().toString());
        feedDischargeLocationList.clear();
        while (cursorPs.moveToNext()) {
            int locId = cursorPs.getInt(cursorPs.getColumnIndex(EngPengContract.PersonStaffEntry._ID));
            String locCode = cursorPs.getString(cursorPs.getColumnIndex(EngPengContract.LocationEntry.COLUMN_LOCATION_CODE));
            String locName = cursorPs.getString(cursorPs.getColumnIndex(EngPengContract.LocationEntry.COLUMN_LOCATION_NAME));
            String coyCode = cursorPs.getString(cursorPs.getColumnIndex(EngPengContract.LocationEntry.COLUMN_COMPANY_CODE));
            int coyId = cursorPs.getInt(cursorPs.getColumnIndex(EngPengContract.LocationEntry.COLUMN_COMPANY_ID));

            FeedDischargeLocation ps = new FeedDischargeLocation(locId, coyId, locCode, locName, coyCode);
            feedDischargeLocationList.add(ps);
        }

        selectionAdapter.setList(feedDischargeLocationList);
    }
}
