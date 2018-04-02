package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import my.com.engpeng.engpeng.adapter.TempWeightSummaryAdapter;
import my.com.engpeng.engpeng.controller.TempWeightController;
import my.com.engpeng.engpeng.controller.TempWeightDetailController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.*;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempWeightSummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private int company_id, location_id, house_code;
    private TextView tvMaleWgt, tvMaleQty, tvMaleAvg;
    private TextView tvFemaleWgt, tvFemaleQty, tvFemaleAvg;
    private TextView tvOverallWgt, tvOverallQty, tvOverallAvg;
    private SQLiteDatabase db;
    private TempWeightSummaryAdapter adapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_weight_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        fabAdd = findViewById(R.id.temp_weight_summary_fab_add);
        btnEnd = findViewById(R.id.temp_weight_summary_btn_end);

        tvMaleWgt = findViewById(R.id.temp_weight_summary_tv_male_weight);
        tvMaleQty = findViewById(R.id.temp_weight_summary_tv_male_quantity);
        tvMaleAvg = findViewById(R.id.temp_weight_summary_tv_male_average);

        tvFemaleWgt = findViewById(R.id.temp_weight_summary_tv_female_weight);
        tvFemaleQty = findViewById(R.id.temp_weight_summary_tv_female_quantity);
        tvFemaleAvg = findViewById(R.id.temp_weight_summary_tv_female_average);

        tvOverallWgt = findViewById(R.id.temp_weight_summary_tv_overall_weight);
        tvOverallQty = findViewById(R.id.temp_weight_summary_tv_overall_quantity);
        tvOverallAvg = findViewById(R.id.temp_weight_summary_tv_overall_average);

        setupTitle();

        setupStartIntent();
        setupListener();
        setupRecycleView();

        Cursor cursor = TempWeightDetailController.getAll(db);
        if(cursor.getCount() == 0){
            callTempWeightDetail(); //Start weight detail immediately after this open if no temp
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
        setupSummary();
    }

    private void setupTitle(){
        Cursor tempHead = TempWeightController.getAll(db);

        //add weight get id
        tempHead.moveToFirst();
        String date = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_RECORD_DATE));
        String time = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_RECORD_TIME));
        String feed = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_FEED));
        String day = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_DAY));

        setTitle("New Body Weight - " + date + " - " + time + " - " + feed +" - Day " + day);
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTempWeightDetail();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check detail
                if (TempWeightDetailController.getTotalQty(db) > 0) {
                    saveWeight();

                    Intent houseListIntent = new Intent(TempWeightSummaryActivity.this, HouseListActivity.class);
                    houseListIntent.putExtra(I_KEY_MODULE, MODULE_WEIGHT);
                    houseListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(houseListIntent);

                } else {
                    Toast toast = new Toast(TempWeightSummaryActivity.this);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(TempWeightSummaryActivity.this, "No detail enter, will not save body weight.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

            }
        });
    }

    private void callTempWeightDetail() {
        Intent weightDetailIntent = new Intent(TempWeightSummaryActivity.this, TempWeightDetailActivity.class);

        int section = TempWeightDetailController.getMaxSection(db);
        if (section == 0) {
            section = 1;
        }

        weightDetailIntent.putExtra(I_KEY_SECTION, section);

        startActivity(weightDetailIntent);
    }

    private void setupSummary() {

        int maleTtlWgt = TempWeightDetailController.getTotalWgtByGender(db, "M");
        int maleTtlQty = TempWeightDetailController.getTotalQtyByGender(db, "M");
        tvMaleWgt.setText(String.valueOf(maleTtlWgt));
        tvMaleQty.setText(String.valueOf(maleTtlQty));
        double maleAvg = 0;
        if (maleTtlQty != 0) {
            maleAvg = (double) maleTtlWgt / maleTtlQty;
        }
        tvMaleAvg.setText(String.format("%.02f", maleAvg));

        int femaleTtlWgt = TempWeightDetailController.getTotalWgtByGender(db, "F");
        int femaleTtlQty = TempWeightDetailController.getTotalQtyByGender(db, "F");
        tvFemaleWgt.setText(String.valueOf(femaleTtlWgt));
        tvFemaleQty.setText(String.valueOf(femaleTtlQty));
        double femaleAvg = 0;
        if (femaleTtlQty != 0) {
            femaleAvg = (double) femaleTtlWgt / femaleTtlQty;
        }
        tvFemaleAvg.setText(String.format("%.02f", femaleAvg));

        int oTtlWgt = TempWeightDetailController.getTotalWgt(db);
        int oTtlQty = TempWeightDetailController.getTotalQty(db);
        tvOverallWgt.setText(String.valueOf(oTtlWgt));
        tvOverallQty.setText(String.valueOf(oTtlQty));
        double oAvg = 0;
        if (oTtlQty != 0) {
            oAvg = (double) oTtlWgt / oTtlQty;
        }
        tvOverallAvg.setText(String.format("%.02f", oAvg));
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_weight_summary_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempWeightDetailController.getAll(db);

        adapter = new TempWeightSummaryAdapter(this, cursor);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                TempWeightDetailController.remove(db, id);
                adapter.swapCursor(TempWeightDetailController.getAll(db));
                setupSummary();
            }
        }).attachToRecyclerView(rv);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    fabAdd.hide();
                } else{
                    fabAdd.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void refreshRecycleView() {
        adapter.swapCursor(TempWeightDetailController.getAll(db));
    }

    private void saveWeight() {
        Cursor tempHead = TempWeightController.getAll(db);
        Cursor tempDetail = TempWeightDetailController.getAll(db);

        //add weight get id
        tempHead.moveToFirst();
        int company_id = tempHead.getInt(tempHead.getColumnIndex(TempWeightEntry.COLUMN_COMPANY_ID));
        int location_id = tempHead.getInt(tempHead.getColumnIndex(TempWeightEntry.COLUMN_LOCATION_ID));
        int house_code = tempHead.getInt(tempHead.getColumnIndex(TempWeightEntry.COLUMN_HOUSE_CODE));
        int day = tempHead.getInt(tempHead.getColumnIndex(TempWeightEntry.COLUMN_DAY));

        String date = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_RECORD_DATE));
        String time = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_RECORD_TIME));
        String feed = tempHead.getString(tempHead.getColumnIndex(TempWeightEntry.COLUMN_FEED));

        long weight_id = WeightController.add(db, company_id, location_id, house_code, day, date, time, feed);

        //loop tempdetail insert weight detail with weight_id
        while (tempDetail.moveToNext()){
            int section = tempDetail.getInt(tempDetail.getColumnIndex(TempWeightDetailEntry.COLUMN_SECTION));
            int wgt = tempDetail.getInt(tempDetail.getColumnIndex(TempWeightDetailEntry.COLUMN_WEIGHT));
            int qty = tempDetail.getInt(tempDetail.getColumnIndex(TempWeightDetailEntry.COLUMN_QTY));
            String gender = tempDetail.getString(tempDetail.getColumnIndex(TempWeightDetailEntry.COLUMN_GENDER));

            WeightDetailController.add(db, weight_id, section, wgt, qty, gender);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(TempWeightSummaryActivity.this).create();
        alertDialog.setTitle("Discard entered data?");
        alertDialog.setMessage("Back will discard entered data. Do you still want to back to previous screen?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TempWeightSummaryActivity.this.finish();
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
