package my.com.engpeng.engpeng;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.LoginAsyncTaskLoader;
import my.com.engpeng.engpeng.utilities.DatabaseUtils;
import my.com.engpeng.engpeng.utilities.FakeDataUtils;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_DIRECT_RUN;

public class LoginActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, LoginAsyncTaskLoader.LoginAsyncTaskLoaderListener {

    private EditText etUsername, etPassword;
    private CheckBox cbPrototype;
    private CheckBox cbLocal;
    private Dialog progressDialog;
    private Button btnLogin;
    private SQLiteDatabase db;
    private AppLoader loader;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        etUsername = findViewById(R.id.login_btn_username);
        etPassword = findViewById(R.id.login_btn_password);
        cbPrototype = findViewById(R.id.login_cb_prototype);
        btnLogin = findViewById(R.id.login_btn_login);
        cbLocal = findViewById(R.id.login_cb_local);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        SharedPreferencesUtils.clearUsernamePassword(this);
        DatabaseUtils.clearSystemData(db);
        Global.sIsPrototype = false;

        progressDialog = UIUtils.getProgressDialog(this);
        setupListener();

        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.LOGIN_LOADER_ID, null, loader);
    }

    private void setupListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        cbPrototype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Global.sIsPrototype = true;
                } else {
                    Global.sIsPrototype = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
    }

    private void attemptLogin() {
        if (etUsername.getText().length() == 0) {
            etUsername.setError(getString(R.string.error_field_required));
            etUsername.requestFocus();
            return;
        }
        if (etPassword.getText().length() == 0) {
            etPassword.setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return;
        }
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);

        if(cbLocal.isChecked()){
            queryBundle.putBoolean(AppLoader.LOADER_IS_LOCAL, true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> uploadLoader = loaderManager.getLoader(Global.LOGIN_LOADER_ID);

        if (uploadLoader == null) {
            loaderManager.initLoader(Global.LOGIN_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.LOGIN_LOADER_ID, queryBundle, loader);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public void beforeLoginAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new LoginAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (!Global.sIsPrototype) { //TODO remove this when online
            if (json != null && !json.equals("")) {

                if (JsonUtils.getAuthentication(this, json)) {

                    SharedPreferencesUtils.saveUsernamePassword(this, username, password);
                    Global.setupGlobalVariables(this, db);
                    finish();

                    if(BranchController.getAllCompany(db).getCount() == 0){
                        Intent syncIntent = new Intent(this, LocationInfoActivity.class);
                        syncIntent.putExtra(I_KEY_DIRECT_RUN, true);
                        startActivity(syncIntent);
                    }

                } else {
                    UIUtils.showToastMessage(this, "Login Failed! (Check username or password)");
                }
            } else {
                UIUtils.showToastMessage(this, "Connection Failed!");
            }
        } else {
            SharedPreferencesUtils.saveUsernamePassword(this, username, password);
            FakeDataUtils.insertFakeData(db);
            finish();
        }
    }
}
