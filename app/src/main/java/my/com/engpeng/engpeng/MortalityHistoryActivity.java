package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import my.com.engpeng.engpeng.adapter.MortalityHistoryAdapter;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.*;

public class MortalityHistoryActivity extends AppCompatActivity {

    private MortalityHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id, house_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortality_history);

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Mortality History for " + sLocationName + " H#" + String.valueOf(house_code));
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.mortality_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = MortalityController.getAllByCLHU(db, company_id, location_id, house_code);

        adapter = new MortalityHistoryAdapter(this, cursor);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final long id = (long) viewHolder.itemView.getTag();

                Cursor mortalityCursor = MortalityController.getById(db, id);
                mortalityCursor.moveToFirst();

                if(mortalityCursor.getInt(mortalityCursor.getColumnIndex(EngPengContract.MortalityEntry.COLUMN_UPLOAD)) == 1){
                    UIUtils.getMessageDialog(MortalityHistoryActivity.this, "Delete Failed", "Uploaded data is unable to delete").show();
                    adapter.swapCursor(MortalityController.getAllByCLHU(db, company_id, location_id, house_code));

                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MortalityHistoryActivity.this).create();
                    alertDialog.setTitle("Delete Swiped Mortality Data ?");
                    alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped mortality data ?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    MortalityController.remove(db, id);
                                    adapter.swapCursor(MortalityController.getAllByCLHU(db, company_id, location_id, house_code));
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.swapCursor(MortalityController.getAllByCLHU(db, company_id, location_id, house_code));
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }).attachToRecyclerView(rv);
    }
}
