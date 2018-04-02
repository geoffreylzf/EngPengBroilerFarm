package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.URLEncoder;

import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.LogAsyncTaskLoader;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.NetworkUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.sPassword;
import static my.com.engpeng.engpeng.Global.sUsername;

public class LogActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, LogAsyncTaskLoader.LogAsyncTaskLoaderListener {

    private Button btnSend;
    private Dialog progressDialog;
    private SQLiteDatabase db;
    private AppLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        btnSend = findViewById(R.id.log_btn_send);
        progressDialog = UIUtils.getProgressDialog(this);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("Send Log");
        setupListener();

        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.LOG_LOADER_ID, null, loader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void setupListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(LogActivity.this).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Confirm to send log?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendLog();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public void beforeLogAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new LogAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (json != null && !json.equals("")) {
            boolean status = JsonUtils.getStatus(this, json);
            if (status) {
                UIUtils.getMessageDialog(this, "Info", "Send log success").show();
            } else {
                UIUtils.getMessageDialog(this, "Error", "Failed to send log!").show();
            }
        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to send log! (Internet problem)").show();
        }
    }

    private void sendLog() {
        String data = "&log={";
        try {
            String mortality_json = MortalityController.getUploadJson(db, 1);
            data += "\"" + URLEncoder.encode(EngPengContract.MortalityEntry.TABLE_NAME, NetworkUtils.ENCODE) + "\": "
                    + URLEncoder.encode(mortality_json, NetworkUtils.ENCODE) + ",";
            
            String catch_bta_json = CatchBTAController.getUploadJson(db, 1);
            data += "\"" + URLEncoder.encode(EngPengContract.CatchBTAEntry.TABLE_NAME, NetworkUtils.ENCODE) + "\": "
                    + URLEncoder.encode(catch_bta_json, NetworkUtils.ENCODE) + ",";

            String weight_json = WeightController.getUploadJson(db, 1);
            data += "\"" + URLEncoder.encode(EngPengContract.WeightEntry.TABLE_NAME, NetworkUtils.ENCODE) + "\": "
                    + URLEncoder.encode(weight_json, NetworkUtils.ENCODE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        data += "}";

        String username = sUsername;
        String password = sPassword;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);
        queryBundle.putString(AppLoader.LOADER_EXTRA_DATA, data);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> logLoader = loaderManager.getLoader(Global.LOG_LOADER_ID);

        if (logLoader == null) {
            loaderManager.initLoader(Global.LOG_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.LOG_LOADER_ID, queryBundle, loader);
        }
    }
}
