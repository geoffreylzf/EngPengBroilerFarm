package my.com.engpeng.engpeng;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.FeedDischargeDetailController;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.FeedReceiveController;
import my.com.engpeng.engpeng.controller.FeedReceiveDetailController;
import my.com.engpeng.engpeng.controller.FeedTransferController;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.LocationInfoAsyncTaskLoader;
import my.com.engpeng.engpeng.utilities.DatabaseUtils;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_DIRECT_RUN;
import static my.com.engpeng.engpeng.Global.sPassword;
import static my.com.engpeng.engpeng.Global.sUsername;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class LocationInfoActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, LocationInfoAsyncTaskLoader.LocationInfoAsyncTaskLoaderListener {

    private Button btnSend;
    private Dialog progressDialog;
    private ProgressBar pbProgress;
    private SQLiteDatabase db;
    private AppLoader loader;
    private CheckBox cbLocal;
    private boolean direct_run_location_info = false, is_inserting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        btnSend = findViewById(R.id.location_info_btn_send);
        cbLocal = findViewById(R.id.location_info_cb_local);
        pbProgress = findViewById(R.id.location_info_pb_progress);
        progressDialog = UIUtils.getProgressDialog(this);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("Get Location Info");

        setupIntent();
        setupListener();

        loader = new AppLoader(this);
        if (direct_run_location_info) {
            getLocationInfo();
        } else {
            getSupportLoaderManager().initLoader(Global.LOCATION_INFO_LOADER_ID, null, loader);
        }
    }

    @Override
    public void onBackPressed() {
        if (!is_inserting) {
            super.onBackPressed();
        }
    }

    private void setupListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_inserting) {
                    getLocationInfo();
                }
            }
        });
    }

    private void setupIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_DIRECT_RUN)) {
            direct_run_location_info = intentStart.getBooleanExtra(I_KEY_DIRECT_RUN, false);
        }
    }

    private void getLocationInfo() {
        String username = sUsername;
        String password = sPassword;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);

        if (cbLocal.isChecked()) {
            queryBundle.putBoolean(AppLoader.LOADER_IS_LOCAL, true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> uploadLoader = loaderManager.getLoader(Global.LOCATION_INFO_LOADER_ID);

        if (uploadLoader == null) {
            loaderManager.initLoader(Global.LOCATION_INFO_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.LOCATION_INFO_LOADER_ID, queryBundle, loader);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public void beforeLocationInfoAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new LocationInfoAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (json != null && !json.equals("")) {
            if (JsonUtils.getAuthentication(this, json)) {

                btnSend.setText("Inserting data...");
                new InsertDataTask().execute(json);
            }
        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to get location info! (Internet problem)").show();
        }
    }

    private class InsertDataTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            is_inserting = true;
            String json = strings[0];
            ContentValues[] cvs_b = JsonUtils.getBranchContentValues(json);
            ContentValues[] cvs_h = JsonUtils.getHouseContentValues(json);
            ContentValues[] cvs_m = JsonUtils.getMortalityContentValues(json);
            ContentValues[] cvs_sw = JsonUtils.getStandardWeightContentValues(json);
            ContentValues[] cvs_fi = JsonUtils.getFeedItemContentValues(json);

            if (cvs_b != null && cvs_b.length != 0 && cvs_h != null && cvs_h.length != 0) {

                DatabaseUtils.clearSystemData(db);
                SharedPreferencesUtils.clearCompanyIdLocationId(LocationInfoActivity.this);
                pbProgress.setProgress(0);

                DatabaseUtils.insertBranch(db, cvs_b);
                publishProgress(5);
                DatabaseUtils.insertHouse(db, cvs_h);
                publishProgress(10);
                DatabaseUtils.insertStandardWeight(db, cvs_sw);
                publishProgress(15);
                DatabaseUtils.insertFeedItem(db, cvs_fi);
                publishProgress(20);

                MortalityController.removeUploaded(db);
                insertMortality(cvs_m, 20, 20);
                publishProgress(40);

                CatchBTAController.removeUploaded(db);
                insertCatchBTA(json, 40, 20);
                publishProgress(60);

                WeightController.removeUploaded(db);
                insertWeight(json, 60, 20);
                publishProgress(80);

                FeedInController.removeUploaded(db);
                insertFeedIn(json, 80, 5);
                publishProgress(85);

                FeedTransferController.removeUploaded(db);
                insertFeedTransfer(json, 85, 5);
                publishProgress(90);

                FeedDischargeController.removeUploaded(db);
                insertFeedDischarge(json, 90, 5);
                publishProgress(95);

                FeedReceiveController.removeUploaded(db);
                insertFeedReceive(json, 95, 5);
                publishProgress(100);

            } else {
                UIUtils.getMessageDialog(LocationInfoActivity.this, "Error", "Failed to get location info!").show();
                return false;
            }
            return true;
        }

        private void insertMortality(ContentValues[] cvs, int start, int allocate) {
            if (cvs != null) {
                for (int i = 0; i < cvs.length; i++) {
                    db.insert(MortalityEntry.TABLE_NAME, null, cvs[i]);
                    publishProgress(start + (int) (((double) (i + 1) / (double) cvs.length) * (double) allocate));
                }
            }
        }

        private void insertCatchBTA(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_catch_bta");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject catch_bta = jsonArray.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    cv.put(CatchBTAEntry.COLUMN_COMPANY_ID, catch_bta.getInt("company_id"));
                    cv.put(CatchBTAEntry.COLUMN_LOCATION_ID, catch_bta.getInt("location_id"));
                    cv.put(CatchBTAEntry.COLUMN_RECORD_DATE, catch_bta.getString("record_date"));
                    cv.put(CatchBTAEntry.COLUMN_TYPE, catch_bta.getString("type"));
                    cv.put(CatchBTAEntry.COLUMN_DOC_NUMBER, catch_bta.getInt("doc_number"));
                    cv.put(CatchBTAEntry.COLUMN_DOC_TYPE, catch_bta.getString("doc_type"));
                    cv.put(CatchBTAEntry.COLUMN_TRUCK_CODE, catch_bta.getString("truck_code"));
                    cv.put(CatchBTAEntry.COLUMN_PRINT_COUNT, catch_bta.getInt("print_count"));
                    cv.put(CatchBTAEntry.COLUMN_TIMESTAMP, catch_bta.getString("timestamp"));
                    cv.put(CatchBTAEntry.COLUMN_CODE, catch_bta.getString("code"));
                    cv.put(CatchBTAEntry.COLUMN_UPLOAD, 1);

                    long catch_bta_id = db.insert(CatchBTAEntry.TABLE_NAME, null, cv);

                    JSONArray jsonArrayDetail = catch_bta.getJSONArray("mobile_catch_bta_detail");
                    for (int x = 0; x < jsonArrayDetail.length(); x++) {
                        JSONObject catch_bta_detail = jsonArrayDetail.getJSONObject(x);
                        CatchBTADetailController.add(db,
                                catch_bta_id,
                                catch_bta_detail.getDouble("weight"),
                                catch_bta_detail.getInt("qty"),
                                catch_bta_detail.getInt("house_code"),
                                catch_bta_detail.getInt("cage_qty"),
                                catch_bta_detail.getInt("with_cover_qty"),
                                catch_bta_detail.getInt("is_bt"));
                    }

                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertWeight(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_weight");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject weight = jsonArray.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    cv.put(WeightEntry.COLUMN_COMPANY_ID, weight.getInt("company_id"));
                    cv.put(WeightEntry.COLUMN_LOCATION_ID, weight.getInt("location_id"));
                    cv.put(WeightEntry.COLUMN_HOUSE_CODE, weight.getInt("house_code"));
                    cv.put(WeightEntry.COLUMN_DAY, weight.getInt("day"));
                    cv.put(WeightEntry.COLUMN_RECORD_DATE, weight.getString("record_date"));
                    cv.put(WeightEntry.COLUMN_RECORD_TIME, weight.getString("record_time"));
                    cv.put(WeightEntry.COLUMN_FEED, weight.getString("feed"));
                    cv.put(WeightEntry.COLUMN_TIMESTAMP, weight.getString("timestamp"));
                    cv.put(WeightEntry.COLUMN_UPLOAD, 1);

                    long weight_id = db.insert(WeightEntry.TABLE_NAME, null, cv);
                    JSONArray jsonArrayDetail = weight.getJSONArray("mobile_weight_detail");
                    for (int x = 0; x < jsonArrayDetail.length(); x++) {
                        JSONObject weight_detail = jsonArrayDetail.getJSONObject(x);
                        WeightDetailController.add(db,
                                weight_id,
                                weight_detail.getInt("section"),
                                weight_detail.getInt("weight"),
                                weight_detail.getInt("qty"),
                                weight_detail.getString("gender"));
                    }

                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertFeedIn(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_feed_in");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject feed_in = jsonArray.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    cv.put(FeedInEntry.COLUMN_COMPANY_ID, feed_in.getInt("company_id"));
                    cv.put(FeedInEntry.COLUMN_LOCATION_ID, feed_in.getInt("location_id"));
                    cv.put(FeedInEntry.COLUMN_RECORD_DATE, feed_in.getString("record_date"));
                    cv.put(FeedInEntry.COLUMN_DOC_ID, feed_in.getLong("doc_id"));
                    cv.put(FeedInEntry.COLUMN_DOC_NUMBER, feed_in.getString("doc_number"));
                    cv.put(FeedInEntry.COLUMN_TRUCK_CODE, feed_in.getString("truck_code"));
                    cv.put(FeedInEntry.COLUMN_VARIANCE, feed_in.getDouble("variance"));
                    cv.put(FeedInEntry.COLUMN_TIMESTAMP, feed_in.getString("timestamp"));
                    cv.put(FeedInEntry.COLUMN_UPLOAD, 1);

                    long feed_in_id = db.insert(FeedInEntry.TABLE_NAME, null, cv);

                    JSONArray jsonArrayDetail = feed_in.getJSONArray("mobile_feed_in_detail");
                    for (int x = 0; x < jsonArrayDetail.length(); x++) {
                        JSONObject feed_in_detail = jsonArrayDetail.getJSONObject(x);
                        FeedInDetailController.add(db,
                                feed_in_id,
                                feed_in_detail.getLong("doc_detail_id"),
                                feed_in_detail.getInt("house_code"),
                                feed_in_detail.getInt("item_packing_id"),
                                feed_in_detail.getString("compartment_no"),
                                feed_in_detail.getDouble("qty"),
                                feed_in_detail.getDouble("weight"));
                    }
                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertFeedTransfer(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_feed_transfer");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject feed_transfer = jsonArray.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(FeedTransferEntry.COLUMN_COMPANY_ID, feed_transfer.getInt("company_id"));
                    cv.put(FeedTransferEntry.COLUMN_LOCATION_ID, feed_transfer.getInt("location_id"));
                    cv.put(FeedTransferEntry.COLUMN_RECORD_DATE, feed_transfer.getString("record_date"));
                    cv.put(FeedTransferEntry.COLUMN_RUNNING_NO, feed_transfer.getString("running_no"));
                    cv.put(FeedTransferEntry.COLUMN_DISCHARGE_HOUSE, feed_transfer.getInt("discharge_house"));
                    cv.put(FeedTransferEntry.COLUMN_RECEIVE_HOUSE, feed_transfer.getInt("receive_house"));
                    cv.put(FeedTransferEntry.COLUMN_ITEM_PACKING_ID, feed_transfer.getInt("item_packing_id"));
                    cv.put(FeedTransferEntry.COLUMN_WEIGHT, feed_transfer.getDouble("weight"));
                    cv.put(FeedTransferEntry.COLUMN_TIMESTAMP, feed_transfer.getString("timestamp"));
                    cv.put(FeedTransferEntry.COLUMN_UPLOAD, 1);

                    db.insert(FeedTransferEntry.TABLE_NAME, null, cv);

                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertFeedDischarge(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_feed_discharge");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject feed_discharge = jsonArray.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    cv.put(FeedDischargeEntry.COLUMN_COMPANY_ID, feed_discharge.getInt("company_id"));
                    cv.put(FeedDischargeEntry.COLUMN_LOCATION_ID, feed_discharge.getInt("location_id"));
                    cv.put(FeedDischargeEntry.COLUMN_RECORD_DATE, feed_discharge.getString("record_date"));
                    cv.put(FeedDischargeEntry.COLUMN_DISCHARGE_CODE, feed_discharge.getString("discharge_code"));
                    cv.put(FeedDischargeEntry.COLUMN_RUNNING_NO, feed_discharge.getString("running_no"));
                    cv.put(FeedDischargeEntry.COLUMN_TRUCK_CODE, feed_discharge.getString("truck_code"));
                    cv.put(FeedDischargeEntry.COLUMN_TIMESTAMP, feed_discharge.getString("timestamp"));
                    cv.put(FeedDischargeEntry.COLUMN_UPLOAD, 1);

                    long feed_discharge_id = db.insert(FeedDischargeEntry.TABLE_NAME, null, cv);

                    JSONArray jsonArrayDetail = feed_discharge.getJSONArray("mobile_feed_discharge_detail");
                    for (int x = 0; x < jsonArrayDetail.length(); x++) {
                        JSONObject feed_discharge_detail = jsonArrayDetail.getJSONObject(x);
                        FeedDischargeDetailController.add(db,
                                feed_discharge_id,
                                feed_discharge_detail.getInt("house_code"),
                                feed_discharge_detail.getInt("item_packing_id"),
                                feed_discharge_detail.getDouble("weight"));
                    }
                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertFeedReceive(String jsonStr, int start, int allocate) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray jsonArray = json.getJSONArray("mobile_feed_receive");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject feed_receive = jsonArray.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    cv.put(FeedReceiveEntry.COLUMN_COMPANY_ID, feed_receive.getInt("company_id"));
                    cv.put(FeedReceiveEntry.COLUMN_LOCATION_ID, feed_receive.getInt("location_id"));
                    cv.put(FeedReceiveEntry.COLUMN_RECORD_DATE, feed_receive.getString("record_date"));
                    cv.put(FeedReceiveEntry.COLUMN_DISCHARGE_CODE, feed_receive.getString("discharge_code"));
                    cv.put(FeedReceiveEntry.COLUMN_RUNNING_NO, feed_receive.getString("running_no"));
                    cv.put(FeedReceiveEntry.COLUMN_TRUCK_CODE, feed_receive.getString("truck_code"));
                    cv.put(FeedReceiveEntry.COLUMN_VARIANCE, feed_receive.getDouble("variance"));
                    cv.put(FeedReceiveEntry.COLUMN_TIMESTAMP, feed_receive.getString("timestamp"));
                    cv.put(FeedReceiveEntry.COLUMN_UPLOAD, 1);

                    long feed_receive_id = db.insert(FeedReceiveEntry.TABLE_NAME, null, cv);

                    JSONArray jsonArrayDetail = feed_receive.getJSONArray("mobile_feed_receive_detail");
                    for (int x = 0; x < jsonArrayDetail.length(); x++) {
                        JSONObject feed_receive_detail = jsonArrayDetail.getJSONObject(x);
                        FeedReceiveDetailController.add(db,
                                feed_receive_id,
                                feed_receive_detail.getInt("house_code"),
                                feed_receive_detail.getInt("item_packing_id"),
                                feed_receive_detail.getDouble("weight"));
                    }
                    publishProgress(start + (int) (((double) (i + 1) / (double) jsonArray.length()) * (double) allocate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pbProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                LocationInfoActivity.this.finish();
                LocationInfoActivity.this.startActivity(new Intent(LocationInfoActivity.this, CompanyListActivity.class));
            }
            is_inserting = false;
        }
    }
}
