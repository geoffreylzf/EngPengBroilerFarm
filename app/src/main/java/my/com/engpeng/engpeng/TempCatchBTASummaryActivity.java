package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.com.engpeng.engpeng.adapter.TempCatchBTASummaryAdapter;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.CatchBTAWorkerController;
import my.com.engpeng.engpeng.controller.TempCatchBTADetailController;
import my.com.engpeng.engpeng.controller.TempCatchBTAWorkerController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_CAGE_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_CATCH_TEAM;
import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_CONTINUE_NEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_TYPE;
import static my.com.engpeng.engpeng.Global.I_KEY_FASTING_TIME;
import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;
import static my.com.engpeng.engpeng.Global.I_KEY_WITH_COVER_QTY;
import static my.com.engpeng.engpeng.Global.MODULE_CATCH_BTA;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.data.EngPengContract.TempCatchBTADetailEntry;

public class TempCatchBTASummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDocNumber, tvDestination, tvType, tvTruckCode, tvFastingTime, tvTtlWeight, tvTtlQty, tvTtlRecord;

    private SQLiteDatabase db;
    private TempCatchBTASummaryAdapter adapter;
    private RecyclerView rv;

    private static int REQUEST_CODE_CONTINUE_NEXT = 1;

    private int company_id, location_id, doc_number;
    private String record_date, type, doc_type, truck_code, fasting_time, catch_team;

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
        tvFastingTime = findViewById(R.id.temp_catch_bta_summary_tv_fasting_time);

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
        if (intentStart.hasExtra(I_KEY_RECORD_DATE)) {
            record_date = intentStart.getStringExtra(I_KEY_RECORD_DATE);
        }
        if (intentStart.hasExtra(I_KEY_TYPE)) {
            type = intentStart.getStringExtra(I_KEY_TYPE);
        }
        if (intentStart.hasExtra(I_KEY_DOC_NUMBER)) {
            doc_number = intentStart.getIntExtra(I_KEY_DOC_NUMBER, 0);
        }
        if (intentStart.hasExtra(I_KEY_DOC_TYPE)) {
            doc_type = intentStart.getStringExtra(I_KEY_DOC_TYPE);
        }
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
        if (intentStart.hasExtra(I_KEY_FASTING_TIME)) {
            fasting_time = intentStart.getStringExtra(I_KEY_FASTING_TIME);
        }
        if (intentStart.hasExtra(I_KEY_CATCH_TEAM)) {
            catch_team = intentStart.getStringExtra(I_KEY_CATCH_TEAM);
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
                    alertDialog.setTitle("Confirm to save? (Simpan?)");
                    alertDialog.setMessage("Edit is unable after save, please check carefully before save.\n(Pengubahan dihalang selepas simpan, pastikan semua betul sebelum simpan.)");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE (SIMPAN)",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    long catch_bta_id = saveCatchBTA();

                                    Intent mainIntent = new Intent(TempCatchBTASummaryActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);

                                    String printText = PrintUtils.printCatchBTA(TempCatchBTASummaryActivity.this, db, catch_bta_id);
                                    String qr = CatchBTAController.toQrData(db, catch_bta_id);

                                    Intent ppIntent = new Intent(TempCatchBTASummaryActivity.this, PrintPreview2Activity.class);
                                    ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                                    ppIntent.putExtra(I_KEY_PRINT_QR_TEXT, qr);
                                    ppIntent.putExtra(I_KEY_MODULE, MODULE_CATCH_BTA);
                                    ppIntent.putExtra(I_KEY_ID, catch_bta_id);
                                    startActivity(ppIntent);

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL(BALIK)",
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
                final long id = (long) viewHolder.itemView.getTag();

                Cursor cursorDetail = TempCatchBTADetailController.getById(db, id);
                cursorDetail.moveToFirst();
                double weight = cursorDetail.getDouble(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WEIGHT));
                int qty = cursorDetail.getInt(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_QTY));
                int house_code = cursorDetail.getInt(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_HOUSE_CODE));
                int cage_qty = cursorDetail.getInt(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_CAGE_QTY));
                int with_cover_qty = cursorDetail.getInt(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
                int is_bt = cursorDetail.getInt(cursorDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_IS_BT));

                AlertDialog alertDialog = new AlertDialog.Builder(TempCatchBTASummaryActivity.this).create();
                alertDialog.setTitle("Delete Swiped Catch BTA Data ?");
                String message = "Weight(kg) : " + weight + "\n";
                message += "Quantity : " + qty + "\n";
                message += "House : " + house_code + "\n";
                message += "Cage Qty : " + cage_qty + "\n";
                message += "With Cover Qty : " + with_cover_qty + "\n";
                message += "Is Bluetooth : " + is_bt + "\n\n";

                alertDialog.setMessage(message + "This action can't be undo. Do you still want to delete swiped catch BTA data ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE(BUANG)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TempCatchBTADetailController.remove(db, id);
                                refreshRecycleView();
                                setupTtlSummary();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL(BALIK)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshRecycleView();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
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
        //Do nothing
    }

    public void setupSummary() {
        String type = this.type;

        if (type.equals("B")) {
            type = "C";
        }

        String destination = getString(R.string.bta_customer);
        if (doc_type.equals("IFT")) {
            destination = getString(R.string.bta_slaughterhouse);
        } else if (doc_type.equals("OP")) {
            destination = getString(R.string.bta_op);
        }

        tvLocation.setText("Location : " + sLocationName);
        tvDocNumber.setText("Doc Number : " + doc_number);
        tvDestination.setText("Destination : " + destination);
        tvType.setText("Type : " + type);
        tvTruckCode.setText("Truck Code : " + truck_code);
        tvFastingTime.setText("Fasting Time : " + fasting_time);
    }

    public void setupTtlSummary() {
        int ttlQty = TempCatchBTADetailController.getTotalQty(db);
        double ttlWeight = TempCatchBTADetailController.getTotalWeight(db);
        int ttlCage = TempCatchBTADetailController.getTotalCage(db);

        tvTtlQty.setText("Total Quantity : " + ttlQty);
        tvTtlWeight.setText("Total Weight : " + String.format("%.2f", ttlWeight) + " Kg");
        tvTtlRecord.setText("Total Cage : " + ttlCage + "");

    }

    public Long saveCatchBTA() {
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
        Calendar c = Calendar.getInstance();
        String code = sdfDateTime.format(c.getTime()) + String.format(Locale.US, "%04d%04d", company_id, location_id);

        long catch_bta_id = CatchBTAController.add(db, company_id, location_id, record_date, type, doc_number, doc_type, truck_code, code, fasting_time, catch_team);

        Cursor tempDetail = TempCatchBTADetailController.getAll(db);
        while (tempDetail.moveToNext()) {
            double wgt = tempDetail.getDouble(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WEIGHT));
            int qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_QTY));
            int house_code = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_HOUSE_CODE));
            int cage_qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_CAGE_QTY));
            int with_cover_qty = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
            int is_bt = tempDetail.getInt(tempDetail.getColumnIndex(TempCatchBTADetailEntry.COLUMN_IS_BT));

            CatchBTADetailController.add(db, catch_bta_id, wgt, qty, house_code, cage_qty, with_cover_qty, is_bt);
        }
        TempCatchBTADetailController.delete(db);

        Cursor tempWorker = TempCatchBTAWorkerController.getAll(db);
        while (tempWorker.moveToNext()) {
            String psIdStr = tempWorker.getString(tempWorker.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID));
            Integer psId = null;
            try {
                psId = Integer.valueOf(psIdStr);
            } catch (NumberFormatException ex) {
                //DO Nothing
            }
            String workerName = tempWorker.getString(tempWorker.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_WORKER_NAME));
            CatchBTAWorkerController.add(db, catch_bta_id, psId, workerName);
        }
        TempCatchBTAWorkerController.deleteAll(db);

        return catch_bta_id;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTINUE_NEXT) {
            if (resultCode == RESULT_OK) {
                boolean is_continue = data.getBooleanExtra(I_KEY_CONTINUE_NEXT, false);
                if (is_continue) {
                    callTempCatchBTADetail();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.temp_catch_bta_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_temp_catch_bta_discard) {
            AlertDialog alertDialog = new AlertDialog.Builder(TempCatchBTASummaryActivity.this).create();
            alertDialog.setTitle("Discard entered data? (Buang rekod tangkap ini?)");
            alertDialog.setMessage("Discard entered data will not recover. Do you still want to discard?\n" +
                    "(Rekod tangkap yang dibuang tidak akan kembali, anda pasti mahu buang?)");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD(BUANG)",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            TempCatchBTASummaryActivity.this.finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL(BALIK)",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
