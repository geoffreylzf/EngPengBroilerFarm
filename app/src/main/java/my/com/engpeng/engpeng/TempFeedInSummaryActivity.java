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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import my.com.engpeng.engpeng.adapter.TempFeedInSummaryAdapter;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_ID_LIST;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;
import static my.com.engpeng.engpeng.Global.MODULE_CATCH_BTA;
import static my.com.engpeng.engpeng.Global.sLocationName;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedInSummaryActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private Button btnEnd;
    private TextView tvLocation, tvDocNumber, tvTruckCode;
    private RecyclerView rv;

    private int company_id, location_id;
    private Long doc_id;
    private String record_date, doc_number, truck_code, qr_data;
    private SQLiteDatabase db;
    private Toast mToast;
    private TempFeedInSummaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_in_summary);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();
        mToast = new Toast(this);

        fabAdd = findViewById(R.id.temp_feed_in_summary_fab_add);
        btnEnd = findViewById(R.id.temp_feed_in_summary_btn_end);

        tvLocation = findViewById(R.id.temp_feed_in_summary_tv_location_name);
        tvDocNumber = findViewById(R.id.temp_feed_in_summary_tv_doc_number);
        tvTruckCode = findViewById(R.id.temp_feed_in_summary_tv_truck_code);

        setupStartIntent();
        setupListener();
        setupRecycleView();
        setupSummary();

        setTitle("New Feed In Summary");

        Cursor cursor = TempFeedInDetailController.getAll(db);
        if (cursor.getCount() == 0) {
            callTempFeedInDetail();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
    }

    public void setupStartIntent() {
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
        if (intentStart.hasExtra(I_KEY_DOC_ID)) {
            doc_id = intentStart.getLongExtra(I_KEY_DOC_ID, 0);
        }
        if (intentStart.hasExtra(I_KEY_DOC_NUMBER)) {
            doc_number = intentStart.getStringExtra(I_KEY_DOC_NUMBER);
        }
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
        if (intentStart.hasExtra(I_KEY_QR_DATA)) {
            qr_data = intentStart.getStringExtra(I_KEY_QR_DATA);
        }
    }

    public void setupSummary() {
        tvLocation.setText("Location : " + sLocationName);
        tvDocNumber.setText("Doc Number : " + doc_number + " (" + doc_id + ")");
        tvTruckCode.setText("Truck Code : " + truck_code);
    }

    private void setupListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTempFeedInDetail();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TempFeedInDetailController.getAll(db).getCount() > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TempFeedInSummaryActivity.this).create();
                    alertDialog.setTitle("Confirm to save? (Simpan?)");
                    alertDialog.setMessage("Edit is unable after save, please check carefully before save.\n(Pengubahan dihalang selepas simpan, pastikan semua betul sebelum simpan.)");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE (SIMPAN)",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Long feed_in_id = saveFeedIn();

                                    Intent mainIntent = new Intent(TempFeedInSummaryActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);

                                    String printText = PrintUtils.printFeedIn(db, feed_in_id);

                                    Intent ppIntent = new Intent(TempFeedInSummaryActivity.this, PrintPreviewActivity.class);
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
                    mToast = Toast.makeText(TempFeedInSummaryActivity.this, "No detail enter, will not save feed in.", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
    }

    private void callTempFeedInDetail() {
        Intent feedInDetailIntent = new Intent(TempFeedInSummaryActivity.this, TempFeedInDetailActivity.class);
        feedInDetailIntent.putExtra(I_KEY_COMPANY, company_id);
        feedInDetailIntent.putExtra(I_KEY_LOCATION, location_id);
        feedInDetailIntent.putExtra(I_KEY_QR_DATA, qr_data);
        startActivity(feedInDetailIntent);
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_feed_in_summary_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempFeedInDetailController.getAll(db);

        adapter = new TempFeedInSummaryAdapter(this, cursor, db);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final long id = (long) viewHolder.itemView.getTag();

                AlertDialog alertDialog = new AlertDialog.Builder(TempFeedInSummaryActivity.this).create();
                alertDialog.setTitle("Delete Swiped Feed In Data ?");

                alertDialog.setMessage("This action can't be undo. Do you still want to delete swiped feed in data ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE(BUANG)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TempFeedInDetailController.remove(db, id);
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
        adapter.swapCursor(TempFeedInDetailController.getAll(db));
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(TempFeedInSummaryActivity.this).create();
        alertDialog.setTitle("Discard entered data? (Buang rekod feed masuk ini?)");
        alertDialog.setMessage("Discard entered data will not recover. Do you still want to discard?\n" +
                "(Rekod feed masuk yang dibuang tidak akan kembali, anda pasti mahu buang?)");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD(BUANG)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TempFeedInSummaryActivity.this.finish();
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

    private Long saveFeedIn() {

        long feed_in_id = FeedInController.add(db,
                company_id,
                location_id,
                record_date,
                doc_id,
                doc_number,
                truck_code);

        Cursor tempDetail = TempFeedInDetailController.getAll(db);

        while (tempDetail.moveToNext()) {
            long doc_detail_id = tempDetail.getLong(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_DOC_DETAIL_ID));
            int house_code = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_HOUSE_CODE));
            int item_packing_id = tempDetail.getInt(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID));
            String compartment_no = tempDetail.getString(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_COMPARTMENT_NO));
            double qty = tempDetail.getDouble(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_QTY));
            double weight = tempDetail.getDouble(tempDetail.getColumnIndex(TempFeedInDetailEntry.COLUMN_WEIGHT));

            FeedInDetailController.add(db,
                    feed_in_id,
                    doc_detail_id,
                    house_code,
                    item_packing_id,
                    compartment_no,
                    qty,
                    weight);
        }

        return feed_in_id;
    }
}
