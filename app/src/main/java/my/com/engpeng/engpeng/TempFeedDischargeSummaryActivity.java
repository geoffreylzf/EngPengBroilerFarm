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

import my.com.engpeng.engpeng.adapter.TempFeedDischargeSummaryAdapter;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.FeedDischargeDetailController;
import my.com.engpeng.engpeng.controller.TempFeedDischargeDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_LOCATION_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_RUNNING_NO;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedDischargeSummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDischargeCode, tvTruckCode;
    private RecyclerView rv;

    private int company_id, location_id, dischargeLocationId;
    private String record_date, truck_code, discharge_code, running_no;
    private SQLiteDatabase db;
    private Toast mToast;
    private TempFeedDischargeSummaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_discharge_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();
        mToast = new Toast(this);

        fabAdd = findViewById(R.id.temp_feed_discharge_summary_fab_add);
        btnEnd = findViewById(R.id.temp_feed_discharge_summary_btn_end);

        tvLocation = findViewById(R.id.temp_feed_discharge_summary_tv_location_name);
        tvDischargeCode = findViewById(R.id.temp_feed_discharge_summary_tv_discharge_code);
        tvTruckCode = findViewById(R.id.temp_feed_discharge_summary_tv_truck_code);

        setupStartIntent();
        setupRecycleView();
        setupListener();
        setupSummary();

        setTitle("New Feed Discharge Summary");

        Cursor cursor = TempFeedDischargeDetailController.getAll(db);
        if (cursor.getCount() == 0) {
            callTempFeedDischargeDetail();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
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
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
        if (intentStart.hasExtra(I_KEY_DISCHARGE_LOCATION_ID)) {
            dischargeLocationId = intentStart.getIntExtra(I_KEY_DISCHARGE_LOCATION_ID, 0);
        }
        if (intentStart.hasExtra(I_KEY_DISCHARGE_CODE)) {
            discharge_code = intentStart.getStringExtra(I_KEY_DISCHARGE_CODE);
        }
        if (intentStart.hasExtra(I_KEY_RUNNING_NO)) {
            running_no = intentStart.getStringExtra(I_KEY_RUNNING_NO);
        }
    }

    public void setupSummary() {
        tvLocation.setText("Location : " + sLocationName);
        tvDischargeCode.setText("Discharge Code : " + discharge_code);
        tvTruckCode.setText("Truck Code : " + truck_code);
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTempFeedDischargeDetail();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TempFeedDischargeDetailController.getAll(db).getCount() > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TempFeedDischargeSummaryActivity.this).create();
                    alertDialog.setTitle("Confirm to save? (Simpan?)");
                    alertDialog.setMessage("Edit is unable after save, please check carefully before save.\n(Pengubahan dihalang selepas simpan, pastikan semua betul sebelum simpan.)");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE (SIMPAN)",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Long feed_discharge_id = saveFeedDischarge();

                                    Intent mainIntent = new Intent(TempFeedDischargeSummaryActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);

                                    String printText = PrintUtils.printFeedDischarge(db, feed_discharge_id);
                                    String qr = FeedDischargeController.toQrData(db, feed_discharge_id);

                                    Intent ppIntent = new Intent(TempFeedDischargeSummaryActivity.this, PrintPreview2Activity.class);
                                    ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                                    ppIntent.putExtra(I_KEY_PRINT_QR_TEXT, qr);
                                    TempFeedDischargeSummaryActivity.this.startActivity(ppIntent);
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
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(TempFeedDischargeSummaryActivity.this, "No detail enter, will not save feed discharge.", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
    }

    private void callTempFeedDischargeDetail() {
        Intent intent = new Intent(this, TempFeedDischargeDetailActivity.class);
        intent.putExtra(I_KEY_COMPANY, company_id);
        intent.putExtra(I_KEY_LOCATION, location_id);
        startActivity(intent);
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_feed_discharge_summary_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempFeedDischargeDetailController.getAll(db);

        adapter = new TempFeedDischargeSummaryAdapter(this, cursor, db);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final long id = (long) viewHolder.itemView.getTag();

                AlertDialog alertDialog = new AlertDialog.Builder(TempFeedDischargeSummaryActivity.this).create();
                alertDialog.setTitle("Delete Swiped Feed Discharge Data ?");

                alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped feed discharge data ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE(BUANG)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TempFeedDischargeDetailController.remove(db, id);
                                refreshRecycleView();
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
        adapter.swapCursor(TempFeedDischargeDetailController.getAll(db));
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Discard entered data? (Buang rekod feed pindah keluar ini?)");
        alertDialog.setMessage("Discard entered data will not recover. Do you still want to discard?\n" +
                "(Rekod feed pindah keluar yang dibuang tidak akan kembali, anda pasti mahu buang?)");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD(BUANG)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TempFeedDischargeSummaryActivity.this.finish();
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

    private Long saveFeedDischarge() {
        long feed_discharge_id = FeedDischargeController.add(db,
                company_id,
                location_id,
                record_date,
                discharge_code,
                truck_code,
                dischargeLocationId,
                running_no);

        Cursor tempDetail = TempFeedDischargeDetailController.getAll(db);
        while (tempDetail.moveToNext()) {
            int house_code = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedDischargeDetailEntry.COLUMN_HOUSE_CODE));
            int item_packing_id = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID));
            double weight = tempDetail.getDouble(tempDetail.getColumnIndex(TempFeedDischargeDetailEntry.COLUMN_WEIGHT));

            FeedDischargeDetailController.add(db,
                    feed_discharge_id,
                    house_code,
                    item_packing_id,
                    weight);
        }

        return feed_discharge_id;
    }
}
