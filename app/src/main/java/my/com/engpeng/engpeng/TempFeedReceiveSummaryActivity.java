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

import my.com.engpeng.engpeng.adapter.TempFeedReceiveSummaryAdapter;
import my.com.engpeng.engpeng.controller.FeedReceiveController;
import my.com.engpeng.engpeng.controller.FeedReceiveDetailController;
import my.com.engpeng.engpeng.controller.TempFeedReceiveDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_RUNNING_NO;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedReceiveSummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDischargeCode, tvTruckCode;
    private RecyclerView rv;

    private int company_id, location_id;
    private String record_date, discharge_code, truck_code, running_no, qr_data;
    private SQLiteDatabase db;
    private Toast mToast;
    private TempFeedReceiveSummaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_receive_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();
        mToast = new Toast(this);

        fabAdd = findViewById(R.id.temp_feed_receive_summary_fab_add);
        btnEnd = findViewById(R.id.temp_feed_receive_summary_btn_end);

        tvLocation = findViewById(R.id.temp_feed_receive_summary_tv_location_name);
        tvDischargeCode = findViewById(R.id.temp_feed_receive_summary_tv_discharge_code);
        tvTruckCode = findViewById(R.id.temp_feed_receive_summary_tv_truck_code);

        setupStartIntent();
        setupListener();
        setupRecycleView();
        setupSummary();

        setTitle("New Feed Receive Summary");

        Cursor cursor = TempFeedReceiveDetailController.getAll(db);
        if (cursor.getCount() == 0) {
            callTempFeedReceiveDetail();
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
        if (intentStart.hasExtra(I_KEY_DISCHARGE_CODE)) {
            discharge_code = intentStart.getStringExtra(I_KEY_DISCHARGE_CODE);
        }
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
        if (intentStart.hasExtra(I_KEY_RUNNING_NO)) {
            running_no = intentStart.getStringExtra(I_KEY_RUNNING_NO);
        }
        if (intentStart.hasExtra(I_KEY_QR_DATA)) {
            qr_data = intentStart.getStringExtra(I_KEY_QR_DATA);
        }
    }

    private void setupSummary() {
        tvLocation.setText("Location : " + sLocationName);
        tvDischargeCode.setText("Discharge Code : " + discharge_code);
        tvTruckCode.setText("Truck Code : " + truck_code);
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTempFeedReceiveDetail();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TempFeedReceiveDetailController.getAll(db).getCount() > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TempFeedReceiveSummaryActivity.this).create();
                    alertDialog.setTitle("Confirm to save? (Simpan?)");
                    alertDialog.setMessage("Edit is unable after save, please check carefully before save.\n(Pengubahan dihalang selepas simpan, pastikan semua betul sebelum simpan.)");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE (SIMPAN)",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Long feed_receive_id = saveFeedReceive();

                                    Intent mainIntent = new Intent(TempFeedReceiveSummaryActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);

                                    String printText = PrintUtils.printFeedReceive(db, feed_receive_id);

                                    Intent ppIntent = new Intent(TempFeedReceiveSummaryActivity.this, PrintPreviewActivity.class);
                                    ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
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
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(TempFeedReceiveSummaryActivity.this, "No detail enter, will not save feed in.", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
    }

    private void callTempFeedReceiveDetail() {
        Intent intent = new Intent(TempFeedReceiveSummaryActivity.this, TempFeedReceiveDetailActivity.class);
        intent.putExtra(I_KEY_COMPANY, company_id);
        intent.putExtra(I_KEY_LOCATION, location_id);
        intent.putExtra(I_KEY_QR_DATA, qr_data);
        startActivity(intent);
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_feed_receive_summary_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempFeedReceiveDetailController.getAll(db);

        adapter = new TempFeedReceiveSummaryAdapter(this, cursor, db);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final long id = (long) viewHolder.itemView.getTag();

                AlertDialog alertDialog = new AlertDialog.Builder(TempFeedReceiveSummaryActivity.this).create();
                alertDialog.setTitle("Delete Swiped Feed Receive Data ?");

                alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped feed receive data ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE(BUANG)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TempFeedReceiveDetailController.remove(db, id);
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
        adapter.swapCursor(TempFeedReceiveDetailController.getAll(db));
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(TempFeedReceiveSummaryActivity.this).create();
        alertDialog.setTitle("Discard entered data? (Buang rekod feed masuk ini?)");
        alertDialog.setMessage("Discard entered data will not recover. Do you still want to discard?\n" +
                "(Rekod feed masuk yang dibuang tidak akan kembali, anda pasti mahu buang?)");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD(BUANG)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TempFeedReceiveSummaryActivity.this.finish();
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

    private Long saveFeedReceive() {

        long feed_receive_id = FeedReceiveController.add(db,
                company_id,
                location_id,
                record_date,
                discharge_code,
                truck_code,
                running_no);

        Cursor tempDetail = TempFeedReceiveDetailController.getAll(db);

        while (tempDetail.moveToNext()) {
            int house_code = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedReceiveDetailEntry.COLUMN_HOUSE_CODE));
            int item_packing_id = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID));
            double weight = tempDetail.getDouble(tempDetail.getColumnIndex(TempFeedReceiveDetailEntry.COLUMN_WEIGHT));

            FeedReceiveDetailController.add(db,
                    feed_receive_id,
                    house_code,
                    item_packing_id,
                    weight);
        }

        return feed_receive_id;
    }
}
