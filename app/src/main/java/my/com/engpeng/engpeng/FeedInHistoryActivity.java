package my.com.engpeng.engpeng;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;

import my.com.engpeng.engpeng.adapter.FeedInHistoryAdapter;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

public class FeedInHistoryActivity extends AppCompatActivity
        implements FeedInHistoryAdapter.FeedInHistoryAdapterListener{

    private FeedInHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_in_history);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Feed In History");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.feed_in_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = FeedInController.getAllByCL(db, company_id, location_id);

        adapter = new FeedInHistoryAdapter(this, cursor, db, rv, this);
        rv.setAdapter(adapter);
    }

    private void refreshRecycleView() {
        adapter.swapCursor(FeedInController.getAllByCL(db, company_id, location_id));
    }

    @Override
    public void afterFeedInHistoryAdapterDelete() {
        refreshRecycleView();
    }

}
