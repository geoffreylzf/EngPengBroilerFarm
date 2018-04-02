package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import my.com.engpeng.engpeng.adapter.WeightHistoryAdapter;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

public class WeightHistoryActivity extends AppCompatActivity {

    private WeightHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id, house_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Body Weight History");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.weight_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = WeightController.getAllByCLHU(db, company_id, location_id, house_code, 0);

        adapter = new WeightHistoryAdapter(this, cursor);
        rv.setAdapter(adapter);

        /*new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                WeightController.remove(db, id);
                WeightDetailController.removeByWeightId(db, id);
                adapter.swapCursor(WeightController.getAllByCLHU(db, company_id, location_id, house_code, 0));
            }
        }).attachToRecyclerView(rv);*/
    }

    private void refreshRecycleView(){
        adapter.swapCursor(WeightController.getAllByCLHU(db, company_id, location_id, house_code, 0));
    }
}
