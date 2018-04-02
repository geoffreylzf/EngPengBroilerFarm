package my.com.engpeng.engpeng;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import my.com.engpeng.engpeng.adapter.CatchBTAHistoryAdapter;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

public class CatchBTAHistoryActivity extends AppCompatActivity {

    private CatchBTAHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_bta_history);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Catch BTA History");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.catch_bta_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = CatchBTAController.getAllByCLU(db, company_id, location_id);

        adapter = new CatchBTAHistoryAdapter(this, cursor);
        rv.setAdapter(adapter);
    }

    private void refreshRecycleView(){
        adapter.swapCursor(CatchBTAController.getAllByCLU(db, company_id, location_id));
    }
}
