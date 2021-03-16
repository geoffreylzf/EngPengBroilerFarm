package my.com.engpeng.engpeng;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import my.com.engpeng.engpeng.utilities.UIUtils;

public class UpdateAppVerActivity extends AppCompatActivity {

    private TextView tvVersionName, tvVersionCode;
    private Button btnUpdate;
    private CheckBox cbLocal;
    private Dialog progressDialog;

    final static String GLOBAL_HOST = "http://epgroup.dyndns.org:5031/";
    final static String LOCAL_HOST = "http://192.168.8.6/";

    private String appCode = "";
    private String versionName;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app_ver);

        tvVersionName = findViewById(R.id.tv_current_ver_name);
        tvVersionCode = findViewById(R.id.tv_current_ver_code);
        btnUpdate = findViewById(R.id.btn_update_app_ver);
        cbLocal = findViewById(R.id.cb_local);

        progressDialog = UIUtils.getProgressDialog(this);


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
            appCode = pInfo.packageName;

            tvVersionName.setText("Current Version Name : " + versionName);
            tvVersionCode.setText("Current Version Code : " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApp();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void updateApp() {
        String host = GLOBAL_HOST;
        if (cbLocal.isChecked()) {
            host = LOCAL_HOST;
        }

        String url = String.format("%s%s%s%s",
                host,
                "api/info/mobile/apps/",
                appCode,
                "/latest");

        RequestQueue queue = Volley.newRequestQueue(UpdateAppVerActivity.this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        try {
                            int latestVerCode = response.getInt("version_code");

                            if (latestVerCode > versionCode) {
                                Uri uri = Uri.parse(response.getString("download_link"));
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            } else if (latestVerCode == versionCode) {
                                UIUtils.getMessageDialog(UpdateAppVerActivity.this,
                                        "Info", "Current app is the latest version").show();
                            } else {
                                UIUtils.getMessageDialog(UpdateAppVerActivity.this,
                                        "Error",
                                        String.format(Locale.ENGLISH, "Current Ver : %d \nLatest Ver : %d", versionCode, latestVerCode)).show();
                            }
                        } catch (Exception e) {
                            UIUtils.getMessageDialog(UpdateAppVerActivity.this, "Error", e.toString()).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        String err = "Unknown error";
                        if (error instanceof TimeoutError) {
                            err = "Timeout error";
                        } else {
                            if (error.networkResponse.data != null) {
                                try {
                                    err = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    UIUtils.getMessageDialog(UpdateAppVerActivity.this, "Error", e.toString()).show();
                                }
                            }
                        }
                        UIUtils.getMessageDialog(UpdateAppVerActivity.this, "Error", err).show();
                    }
                });

        progressDialog.show();
        queue.add(jsonRequest);

    }
}