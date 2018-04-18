package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.net.URLEncoder;

import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.UploadAsyncTaskLoader;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.utilities.DatabaseUtils;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.NetworkUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.sPassword;
import static my.com.engpeng.engpeng.Global.sUsername;

public class UploadActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, UploadAsyncTaskLoader.UploadAsyncTaskLoaderListener {

    private TextView tvMortality, tvCatchBTA, tvWeight;
    private Button btnUpload;
    private CheckBox cbLocal;
    private Dialog progressDialog;
    private SQLiteDatabase db;
    private int ttl_row;
    private AppLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tvMortality = findViewById(R.id.upload_tv_mortality);
        tvCatchBTA = findViewById(R.id.upload_tv_catch_bta);
        tvWeight = findViewById(R.id.upload_tv_weight);
        cbLocal = findViewById(R.id.upload_cb_local);
        btnUpload = findViewById(R.id.upload_btn_upload);
        progressDialog = UIUtils.getProgressDialog(this);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("Upload");
        setupListener();
        setupSummary();

        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.UPLOAD_LOADER_ID, null, loader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void setupSummary() {
        int mortality_count = MortalityController.getCount(db, 0);
        int catch_bta_count = CatchBTAController.getCount(db, 0);
        int weight_count = WeightController.getCount(db, 0);

        ttl_row = mortality_count + catch_bta_count + weight_count;

        tvMortality.setText(String.valueOf(mortality_count));
        tvCatchBTA.setText(String.valueOf(catch_bta_count));
        tvWeight.setText(String.valueOf(weight_count));
    }

    private void setupListener() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ttl_row > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(UploadActivity.this).create();
                    alertDialog.setTitle("Confirmation");
                    alertDialog.setMessage("Confirm to upload? Data will be clear after upload");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    upload();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = UIUtils.getMessageDialog(UploadActivity.this, "No Data", "Unable to perform upload without data");
                    alertDialog.show();
                }
            }
        });
    }


    private void upload() {
        String data = "";
        try {
            String mortality_json = MortalityController.getUploadJson(db, 0);
            data += "&" + URLEncoder.encode(EngPengContract.MortalityEntry.TABLE_NAME, NetworkUtils.ENCODE) + "="
                    + URLEncoder.encode(mortality_json, NetworkUtils.ENCODE);

            String catch_bta_json = CatchBTAController.getUploadJson(db, 0);
            data += "&" + URLEncoder.encode(EngPengContract.CatchBTAEntry.TABLE_NAME, NetworkUtils.ENCODE) + "="
                    + URLEncoder.encode(catch_bta_json, NetworkUtils.ENCODE);

            String weight_json = WeightController.getUploadJson(db, 0);
            data += "&" + URLEncoder.encode(EngPengContract.WeightEntry.TABLE_NAME, NetworkUtils.ENCODE) + "="
                    + URLEncoder.encode(weight_json, NetworkUtils.ENCODE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String username = sUsername;
        String password = sPassword;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);
        queryBundle.putString(AppLoader.LOADER_EXTRA_DATA, data);

        if (cbLocal.isChecked()) {
            queryBundle.putBoolean(AppLoader.LOADER_IS_LOCAL, true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> uploadLoader = loaderManager.getLoader(Global.UPLOAD_LOADER_ID);

        if (uploadLoader == null) {
            loaderManager.initLoader(Global.UPLOAD_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.UPLOAD_LOADER_ID, queryBundle, loader);
        }

    }

    @Override
    public void beforeUploadAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new UploadAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (json != null && !json.equals("")) {
            if (JsonUtils.getAuthentication(this, json)) {
                boolean status = JsonUtils.getStatus(json);
                int row = JsonUtils.getUploadRow(json);
                if (status) {
                    DatabaseUtils.updateUploadedStatus(db);
                    UIUtils.getMessageDialog(this, "Info", "Upload success, insert " + row + " row").show();
                    setupSummary();
                } else {
                    UIUtils.getMessageDialog(this, "Error", "Failed to upload!").show();
                }
            }
        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to upload! (Internet problem)").show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_log) {
            startActivity(new Intent(this, LogActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
