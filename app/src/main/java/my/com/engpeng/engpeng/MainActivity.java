package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.adapter.MainTabFragmentAdapter;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.*;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private TextView tvLocationName, tvVersion;
    private TextView navTvCompanyName, navTvLocationName, navTvLoginAs;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Button btnLocation, btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorTransparent));

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        btnUpload = findViewById(R.id.main_btn_upload);
        btnLocation = findViewById(R.id.main_btn_location);
        tvLocationName = findViewById(R.id.main_tv_location_name);

        setupVersion();
        setupListener();
        setupDrawerLayout();
        setupTabLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupTextView();
    }

    private void setupVersion(){
        tvVersion = findViewById(R.id.main_tv_version_name);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Ver"+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupListener() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationListIntent = new Intent(MainActivity.this, LocationListActivity.class);
                locationListIntent.putExtra(I_KEY_COMPANY, String.valueOf(sCompanyId));
                startActivity(locationListIntent);
            }
        });
    }

    private void setupTextView() {
        Global.setupGlobalVariables(this, db);
        tvLocationName.setText(sLocationName);

        navTvCompanyName.setText(sCompanyName);
        navTvLocationName.setText(sLocationName);
        navTvLoginAs.setText(sUsername);
    }

    private void setupDrawerLayout() {
        toolbar = findViewById(R.id.main_tb);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_dl);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.main_nv);

        View headerView = navigationView.getHeaderView(0);
        navTvCompanyName = headerView.findViewById(R.id.nav_main_tv_company_name);
        navTvLocationName = headerView.findViewById(R.id.nav_main_tv_location_name);
        navTvLoginAs = headerView.findViewById(R.id.nav_main_tv_login_as);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_company) {

                    startActivity(new Intent(MainActivity.this, CompanyListActivity.class));
                    return true;

                } else if (id == R.id.nav_location) {

                    if (sCompanyId == 0) {
                        startActivity(new Intent(MainActivity.this, CompanyListActivity.class));
                    } else {

                        Intent locationListIntent = new Intent(MainActivity.this, LocationListActivity.class);
                        locationListIntent.putExtra(I_KEY_COMPANY, String.valueOf(sCompanyId));
                        startActivity(locationListIntent);
                    }

                } else if (id == R.id.nav_upload) {

                    startActivity(new Intent(MainActivity.this, UploadActivity.class));

                } else if (id == R.id.nav_sync) {

                    startActivity(new Intent(MainActivity.this, LocationInfoActivity.class));

                } else if (id == R.id.nav_weight_report) {

                    startActivity(new Intent(MainActivity.this, WeightReportActivity.class));

                }else if (id == R.id.nav_test) {

                    startActivity(new Intent(MainActivity.this, FunctionTestActivity.class));

                } else if (id == R.id.nav_logout) {

                    startActivity(new Intent(MainActivity.this, LogoutActivity.class));

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.main_tl);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.farm_data)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.harvest)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.feed)));
        //tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.farm_management)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.main_vp);
        final MainTabFragmentAdapter adapter = new MainTabFragmentAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Exit Application?");
            alertDialog.setMessage("Work done?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "EXIT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
}
