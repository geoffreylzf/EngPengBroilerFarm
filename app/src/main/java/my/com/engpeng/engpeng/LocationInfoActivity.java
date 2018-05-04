package my.com.engpeng.engpeng;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;
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

public class LocationInfoActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, LocationInfoAsyncTaskLoader.LocationInfoAsyncTaskLoaderListener {

    private Button btnSend;
    private Dialog progressDialog;
    private SQLiteDatabase db;
    private AppLoader loader;
    private CheckBox cbLocal;
    private boolean direct_run_location_info = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        btnSend = findViewById(R.id.location_info_btn_send);
        cbLocal = findViewById(R.id.location_info_cb_local);
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

    private void setupListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationInfo();
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
        //Log.i("afterLoaderDone", ""+json);
        progressDialog.hide();
        if (json != null && !json.equals("")) {

            if (JsonUtils.getAuthentication(this, json)) {

                ContentValues[] cvs_b = JsonUtils.getBranchContentValues(json);
                ContentValues[] cvs_h = JsonUtils.getHouseContentValues(json);
                ContentValues[] cvs_m = JsonUtils.getMortalityContentValues(json);
                ContentValues[] cvs_sw = JsonUtils.getStandardWeightContentValues(json);
                ContentValues[] cvs_fi = JsonUtils.getFeedItemContentValues(json);

                if (cvs_b != null && cvs_b.length != 0 && cvs_h != null && cvs_h.length != 0) {

                    DatabaseUtils.clearSystemData(db);
                    SharedPreferencesUtils.clearCompanyIdLocationId(LocationInfoActivity.this);

                    DatabaseUtils.insertBranch(db, cvs_b);
                    DatabaseUtils.insertHouse(db, cvs_h);
                    DatabaseUtils.insertStandardWeight(db, cvs_sw);
                    DatabaseUtils.insertFeedItem(db, cvs_fi);

                    MortalityController.removeUploaded(db);
                    DatabaseUtils.insertMortality(db, cvs_m);

                    CatchBTAController.removeUploaded(db);
                    JsonUtils.saveCatchBTAHistory(json, db);

                    WeightController.removeUploaded(db);
                    JsonUtils.saveWeightHistory(json, db);

                    finish();
                    this.startActivity(new Intent(this, CompanyListActivity.class));

                } else {
                    UIUtils.getMessageDialog(this, "Error", "Failed to get location info!").show();
                }
            }

        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to get location info! (Internet problem)").show();
        }
    }
}
