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

import my.com.engpeng.engpeng.adapter.TempCatchBTASummaryAdapter;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.TempCatchBTAController;
import my.com.engpeng.engpeng.controller.TempCatchBTADetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_CAGE_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_CONTINUE_NEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_WITH_COVER_QTY;
import static my.com.engpeng.engpeng.Global.MODULE_CATCH_BTA;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempCatchBTASummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDocNumber, tvDestination, tvType, tvTruckCode, tvTtlWeight, tvTtlQty, tvTtlRecord;
    private int company_id, location_id;
    private SQLiteDatabase db;
    private TempCatchBTASummaryAdapter adapter;
    private RecyclerView rv;
    private String doc_number, destination, type;
    private static int REQUEST_CODE_CONTINUE_NEXT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_catch_bta_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        fabAdd = findViewById(R.id.temp_catch_bta_summary_fab_add);
        btnEnd = findViewById(R.id.temp_catch_bta_summary_btn_end);
        tvLocation = findViewById(R.id.temp_catch_bta_summary_tv_location_name);
        tvDocNumber = findViewById(R.id.temp_catch_bta_summary_tv_doc_number);
        tvDestination = findViewById(R.id.temp_catch_bta_summary_tv_destination);
        tvType = findViewById(R.id.temp_catch_bta_summary_tv_type);
        tvTruckCode = findViewById(R.id.temp_catch_bta_summary_tv_truck_code);

        tvTtlWeight = findViewById(R.id.temp_catch_bta_summary_tv_ttl_weight);
        tvTtlQty = findViewById(R.id.temp_catch_bta_summary_tv_ttl_qty);
        tvTtlRecord = findViewById(R.id.temp_catch_bta_summary_tv_ttl_record);

        setupStartIntent();
        setupListener();
        setupRecycleView();
        setupSummary();

        setTitle("New Catch BTA Summary");

        Cursor cursor = TempCatchBTADetailController.getAll(db);
        if (cursor.getCount() == 0) {
            callTempCatchBTADetail(); //Start weight detail immediately after this open if no temp
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
        setupTtlSummary();
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTempCatchBTADetail();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check detail
                if (TempCatchBTADetailController.getAll(db).getCount() > 0) {

                    AlertDialog alertDialog = new AlertDialog.Builder(TempCatchBTASummaryActivity.this).create();
                    alertDialog.setTitle("Confirm to save?");
                    alertDialog.setMessage("Edit is unable after save, please check carefully before save.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    long catch_bta_id = saveCatchBTA();

                                    Intent mainIntent = new Intent(TempCatchBTASummaryActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);

                                    String printText = PrintUtils.printCatchBTA(db, catch_bta_id);

                                    Intent ppIntent = new Intent(TempCatchBTASummaryActivity.this, PrintPreviewActivity.class);
                                    ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                                    ppIntent.putExtra(I_KEY_MODULE, MODULE_CATCH_BTA);
                                    ppIntent.putExtra(I_KEY_ID, catch_bta_id);
                                    startActivity(ppIntent);

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else {
                    Toast toast = new Toast(TempCatchBTASummaryActivity.this);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(TempCatchBTASummaryActivity.this, "No detail enter, will not save catch BTA.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        });
    }

    private void callTempCatchBTADetail() {
        Intent catchBTADetailIntent = new Intent(TempCatchBTASummaryActivity.this, TempCatchBTADetailActivity.class);
        catchBTADetailIntent.putExtra(I_KEY_COMPANY, company_id);
        catchBTADetailIntent.putExtra(I_KEY_LOCATION, location_id);

        Cursor cursorLastDetail = TempCatchBTADetailController.getLastRecord(db);
        if (cursorLastDetail != null) {
            catchBTADetailIntent.putExtra(I_KEY_HOUSE_CODE, cursorLastDetail.getInt(cursorLastDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_HOUSE_CODE)));
            catchBTADetailIntent.putExtra(I_KEY_QTY, cursorLastDetail.getInt(cursorLastDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_QTY)));
            catchBTADetailIntent.putExtra(I_KEY_CAGE_QTY, cursorLastDetail.getInt(cursorLastDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_CAGE_QTY)));
            catchBTADetailIntent.putExtra(I_KEY_WITH_COVER_QTY, cursorLastDetail.getInt(cursorLastDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY)));
        }

        startActivityForResult(catchBTADetailIntent, REQUEST_CODE_CONTINUE_NEXT);
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_catch_bta_summary_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempCatchBTADetailController.getAll(db);

        adapter = new TempCatchBTASummaryAdapter(this, cursor);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                TempCatchBTADetailController.remove(db, id);
                refreshRecycleView();
                setupTtlSummary();
            }
        }).attachToRecyclerView(rv);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void refreshRecycleView() {
        adapter.swapCursor(TempCatchBTADetailController.getAll(db));
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(TempCatchBTASummaryActivity.this).create();
        alertDialog.setTitle("Discard entered data?");
        alertDialog.setMessage("Back will discard entered data. Do you still want to back to previous screen?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TempCatchBTASummaryActivity.this.finish();
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

    public void setupSummary() {
        Cursor cursor = TempCatchBTAController.getAll(db);
        cursor.moveToFirst();
        String doc_number = cursor.getString(cursor.getColumnIndex(TempCatchBTAEntry.COLUMN_DOC_NUMBER));
        String doc_type = cursor.getString(cursor.getColumnIndex(TempCatchBTAEntry.COLUMN_DOC_TYPE));
        String type = cursor.getString(cursor.getColumnIndex(TempCatchBTAEntry.COLUMN_TYPE));

        if(type.equals("B")){
            type = "C";
        }

        String truck_code = cursor.getString(cursor.getColumnIndex(TempCatchBTAEntry.COLUMN_TRUCK_CODE));

        String destination = getString(R.string.bta_customer);
        if (doc_type.equals("IFT")) {
            destination = getString(R.string.bta_slaughterhouse);
        }else if (doc_type.equals("OP")) {
            destination = getString(R.string.bta_op);
        }

        tvLocation.setText("Location : " + sLocationName);
        tvDocNumber.setText("Doc Number : " + doc_number);
        tvDestination.setText("Destination : " + destination);
        tvType.setText("Type : " + type);
        tvTruckCode.setText("Truck Code : " + truck_code);
    }

    public void setupTtlSummary(){
        int ttlQty = TempCatchBTADetailController.getTotalQty(db);
        double ttlWeight = TempCatchBTADetailController.getTotalWeight(db);
        int ttlCage = TempCatchBTADetailController.getAll(db).getCount();

        tvTtlQty.setText("Total Quantity : " + ttlQty);
        tvTtlWeight.setText("Total Weight : " + String.format("%.2f", ttlWeight) + " Kg");
        tvTtlRecord.setText("Total Record : " + ttlCage + "");

    }

    public Long saveCatchBTA(){
        Cursor tempHead = TempCatchBTAController.getAll(db);
        Cursor tempDetail = TempCatchBTADetailController.getAll(db);

        tempHead.moveToFirst();

        int company_id = tempHead.getInt(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_COMPANY_ID));
        int location_id = tempHead.getInt(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_LOCATION_ID));
        int doc_number = tempHead.getInt(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_DOC_NUMBER));

        String dateStr = tempHead.getString(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_RECORD_DATE));
        String type = tempHead.getString(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_TYPE));
        String doc_type = tempHead.getString(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_DOC_TYPE));
        String truck_code = tempHead.getString(tempHead.getColumnIndex(TempCatchBTAEntry.COLUMN_TRUCK_CODE));

        long catch_bta_id = CatchBTAController.add(db, company_id, location_id, dateStr, type, doc_number, doc_type, truck_code);

        while (tempDetail.moveToNext()){
            double wgt = tempDetail.getDouble(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WEIGHT));
            int qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_QTY));
            int house_code = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_HOUSE_CODE));
            int cage_qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_CAGE_QTY));
            int with_cover_qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY));

            CatchBTADetailController.add(db, catch_bta_id, wgt, qty, house_code, cage_qty, with_cover_qty);
        }

        return catch_bta_id;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTINUE_NEXT) {
            if(resultCode == RESULT_OK) {
                boolean is_continue = data.getBooleanExtra(I_KEY_CONTINUE_NEXT, false);
                if(is_continue){
                    callTempCatchBTADetail();
                }
            }
        }
    }
}
