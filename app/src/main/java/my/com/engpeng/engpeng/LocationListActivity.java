package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import my.com.engpeng.engpeng.adapter.LocationListAdapter;
import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;

public class LocationListActivity extends AppCompatActivity {

    private LocationListAdapter mAdapter;
    private SQLiteDatabase db;
    private int company_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        Intent intentStarted = getIntent();
        company_id = Integer.parseInt(intentStarted.getStringExtra(I_KEY_COMPANY));

        RecyclerView locationListRecyclerView = this.findViewById(R.id.location_list_view);
        locationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = BranchController.getLocationByCompanyId(db, company_id);

        mAdapter = new LocationListAdapter(this, cursor, db, company_id);
        locationListRecyclerView.setAdapter(mAdapter);

        setTitle("Select Location");
    }

}
