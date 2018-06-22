package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import my.com.engpeng.engpeng.adapter.FeedTransferHistoryAdapter;
import my.com.engpeng.engpeng.adapter.MortalityHistoryAdapter;
import my.com.engpeng.engpeng.controller.FeedTransferController;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class FeedTransferHistoryActivity extends AppCompatActivity {

    private FeedTransferHistoryAdapter adapter;
    private SQLiteDatabase db;
    private int company_id, location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_transfer_history);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        company_id = sCompanyId;
        location_id = sLocationId;

        setupRecycleView();

        setTitle("Feed Transfer History for " + sLocationName);
    }

    private void setupRecycleView(){
        RecyclerView rv = this.findViewById(R.id.feed_transfer_history_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = FeedTransferController.getAllByCL(db, company_id, location_id);

        adapter = new FeedTransferHistoryAdapter(this, cursor, db);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final long id = (long) viewHolder.itemView.getTag();

                Cursor feedTransferCursor = FeedTransferController.getById(db, id);
                feedTransferCursor.moveToFirst();

                if(feedTransferCursor.getInt(feedTransferCursor.getColumnIndex(EngPengContract.FeedTransferEntry.COLUMN_UPLOAD)) == 1){
                    UIUtils.getMessageDialog(FeedTransferHistoryActivity.this, "Delete Failed", "Uploaded data is unable to delete").show();
                    adapter.swapCursor(FeedTransferController.getAllByCL(db, company_id, location_id));

                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(FeedTransferHistoryActivity.this).create();
                    alertDialog.setTitle("Delete Swiped Feed Transfer Data ?");
                    alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped feed transfer data ?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FeedTransferController.remove(db, id);
                                    adapter.swapCursor(FeedTransferController.getAllByCL(db, company_id, location_id));
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.swapCursor(FeedTransferController.getAllByCL(db, company_id, location_id));
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }).attachToRecyclerView(rv);
    }
}
