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
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
    private CheckBox cbLocal;
    private Dialog progressDialog;
    private Button btnLogin;
    private SQLiteDatabase db;
    private AppLoader loader;
    private String username, password;
    private GoogleSignInClient mGoogleSignInClient;
    private String mSignInEmail;
    private SignInButton mSignInButton;

    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        etUsername = findViewById(R.id.login_btn_username);
        etPassword = findViewById(R.id.login_btn_password);
        btnLogin = findViewById(R.id.login_btn_login);
        cbLocal = findViewById(R.id.login_cb_local);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        SharedPreferencesUtils.clearUsernamePassword(this);
        DatabaseUtils.clearSystemData(db);

        progressDialog = UIUtils.getProgressDialog(this);
        setupListener();

        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.LOGIN_LOADER_ID, null, loader);

        //Google Authentication
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInButton = findViewById(R.id.login_sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUI(account);
            } catch (ApiException e) {
                UIUtils.showToastMessage(this, "Error : "+e.toString());
                updateUI(null);
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            mSignInEmail = account.getEmail();
            setGoogleSignInButtonText(mSignInButton, account.getEmail() + " (Sign out)");
        } else {
            mSignInEmail = null;
            setGoogleSignInButtonText(mSignInButton,  "Sign in");
        }
    }

    private void setGoogleSignInButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void setupListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
    }

    private void attemptLogin() {
        if (mSignInEmail == null || mSignInEmail.isEmpty()) {
            UIUtils.showToastMessage(this, "Please sign in google account before login");
            return;
        }

        if (!(mSignInEmail.equals("ep.it.apps@gmail.com") || mSignInEmail.equals("geoffreylzf@gmail.com"))) {
            UIUtils.showToastMessage(this, "Invalid google account");
            return;
        }

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

        if (cbLocal.isChecked()) {
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
        if (json != null && !json.equals("")) {
            if (JsonUtils.getAuthentication( json)) {
                SharedPreferencesUtils.saveUsernamePassword(this, username, password);
                Global.setupGlobalVariables(this, db);
                finish();

                if (BranchController.getAllCompany(db).getCount() == 0) {
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
    }
}
