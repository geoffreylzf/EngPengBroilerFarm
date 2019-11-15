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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import my.com.engpeng.engpeng.adapter.WeightAdapter;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_SECTION;
import static my.com.engpeng.engpeng.Global.I_KEY_WEIGHT;
import static my.com.engpeng.engpeng.Global.MODULE_CATCH_BTA;
import static my.com.engpeng.engpeng.Global.MODULE_WEIGHT;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class WeightActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private TextView tvMaleWgt, tvMaleQty, tvMaleAvg;
    private TextView tvFemaleWgt, tvFemaleQty, tvFemaleAvg;
    private TextView tvOverallWgt, tvOverallQty, tvOverallAvg;
    private SQLiteDatabase db;
    private WeightAdapter adapter;
    private RecyclerView rv;
    private long weight_id;
    private String date, time, feed, day;
    private boolean is_upload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        fabAdd = findViewById(R.id.weight_fab_add);

        tvMaleWgt = findViewById(R.id.weight_tv_male_weight);
        tvMaleQty = findViewById(R.id.weight_tv_male_quantity);
        tvMaleAvg = findViewById(R.id.weight_tv_male_average);

        tvFemaleWgt = findViewById(R.id.weight_tv_female_weight);
        tvFemaleQty = findViewById(R.id.weight_tv_female_quantity);
        tvFemaleAvg = findViewById(R.id.weight_tv_female_average);

        tvOverallWgt = findViewById(R.id.weight_tv_overall_weight);
        tvOverallQty = findViewById(R.id.weight_tv_overall_quantity);
        tvOverallAvg = findViewById(R.id.weight_tv_overall_average);

        setupStartIntent();
        setupTitle();
        setupListener();
        setupRecycleView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
        setupSummary();
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_WEIGHT)) {
            weight_id = intentStart.getLongExtra(I_KEY_WEIGHT, 0);
        }
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent weightDetailIntent = new Intent(WeightActivity.this, WeightDetailActivity.class);

                int section = WeightDetailController.getMaxSectionByWeightId(db, weight_id);
                if (section == 0) {
                    section = 1;
                }

                weightDetailIntent.putExtra(I_KEY_SECTION, section);
                weightDetailIntent.putExtra(I_KEY_WEIGHT, (Long) weight_id);

                startActivity(weightDetailIntent);
            }
        });
    }

    private void setupTitle() {
        Cursor cursor = WeightController.getById(db, weight_id);
        cursor.moveToFirst();
        date = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_DATE));
        time = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_TIME));
        feed = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_FEED));
        day = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_DAY));

        int upload = cursor.getInt(cursor.getColumnIndex(WeightEntry.COLUMN_UPLOAD));
        if (upload == 1) {
            is_upload = true;
        }
        String title = "BW - " + date + " - " + time + " - " + feed +" - Day " + day;

        if (is_upload) {
            title += " (Uploaded)";
        }
        setTitle(title);
    }

    private void setupSummary() {

        int maleTtlWgt = WeightDetailController.getTotalWgtByGenderWeightId(db, weight_id, "M");
        int maleTtlQty = WeightDetailController.getTotalQtyByGenderWeightId(db, weight_id, "M");
        tvMaleWgt.setText(String.valueOf(maleTtlWgt));
        tvMaleQty.setText(String.valueOf(maleTtlQty));
        double maleAvg = 0;
        if (maleTtlQty != 0) {
            maleAvg = (double) maleTtlWgt / maleTtlQty;
        }
        tvMaleAvg.setText(String.format("%.02f", maleAvg));

        int femaleTtlWgt = WeightDetailController.getTotalWgtByGenderWeightId(db, weight_id, "F");
        int femaleTtlQty = WeightDetailController.getTotalQtyByGenderWeightId(db, weight_id, "F");
        tvFemaleWgt.setText(String.valueOf(femaleTtlWgt));
        tvFemaleQty.setText(String.valueOf(femaleTtlQty));
        double femaleAvg = 0;
        if (femaleTtlQty != 0) {
            femaleAvg = (double) femaleTtlWgt / femaleTtlQty;
        }
        tvFemaleAvg.setText(String.format("%.02f", femaleAvg));

        int oTtlWgt = WeightDetailController.getTotalWgtByWeightId(db, weight_id);
        int oTtlQty = WeightDetailController.getTotalQtyByWeightId(db, weight_id);
        tvOverallWgt.setText(String.valueOf(oTtlWgt));
        tvOverallQty.setText(String.valueOf(oTtlQty));
        double oAvg = 0;
        if (oTtlQty != 0) {
            oAvg = (double) oTtlWgt / oTtlQty;
        }
        tvOverallAvg.setText(String.format("%.02f", oAvg));
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.weight_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = WeightDetailController.getAllByWeightId(db, weight_id);

        adapter = new WeightAdapter(this, cursor);
        rv.setAdapter(adapter);

        /*(new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AlertDialog alertDialog = new AlertDialog.Builder(WeightActivity.this).create();
                alertDialog.setTitle("Delete Swiped Body Weight Detail?");
                alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped body weight detail?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                long id = (long) viewHolder.itemView.getTag();
                                WeightDetailController.remove(db, id);
                                adapter.swapCursor(WeightDetailController.getAllByWeightId(db, weight_id));
                                setupSummary();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.swapCursor(WeightDetailController.getAllByWeightId(db, weight_id));
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }
        }).attachToRecyclerView(rv);*/

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
        adapter.swapCursor(WeightDetailController.getAllByWeightId(db, weight_id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weight_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_weight_delete) {
            if (is_upload) {
                UIUtils.getMessageDialog(WeightActivity.this, "Delete Failed", "Uploaded data is unable to delete").show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(WeightActivity.this).create();
                alertDialog.setTitle("Delete This Body Weight?");
                alertDialog.setMessage("This action can't be undo. Do you still want to delete this body weight?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WeightController.remove(db, weight_id);
                                WeightDetailController.removeByWeightId(db, weight_id);
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            }
        }
        if (id == R.id.action_weight_print) {
            String printText = PrintUtils.printWeight(db, weight_id);

            Intent ppIntent = new Intent(WeightActivity.this, PrintPreview2Activity.class);
            ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
            ppIntent.putExtra(I_KEY_MODULE, MODULE_WEIGHT);
            ppIntent.putExtra(I_KEY_ID, weight_id);
            startActivity(ppIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
