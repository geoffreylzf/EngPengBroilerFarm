package my.com.engpeng.engpeng;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import my.com.engpeng.engpeng.adapter.FeedDischargeHistoryAdapter;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

public class FeedDischargeHistoryActivity extends AppCompatActivity
        implements FeedDischargeHistoryAdapter.FeedDischargeHistoryAdapterListener{

    private FeedDischargeHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_discharge_history);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Feed Discharge History");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.feed_discharge_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = FeedDischargeController.getAllByCL(db, company_id, location_id);

        adapter = new FeedDischargeHistoryAdapter(this, cursor, db, rv, this);
        rv.setAdapter(adapter);
    }

    private void refreshRecycleView() {
        adapter.swapCursor(FeedDischargeController.getAllByCL(db, company_id, location_id));
    }

    @Override
    public void afterFeedDischargeHistoryAdapterDelete() {
        refreshRecycleView();
    }
}
