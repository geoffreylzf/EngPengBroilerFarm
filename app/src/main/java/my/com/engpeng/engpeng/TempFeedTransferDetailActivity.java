package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempFeedTransferDetailAdapter;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.FeedTransferController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.FeedItem;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DISCHARGE_HOUSE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_RECEIVE_HOUSE;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.RUNNING_CODE_TRANSFER;
import static my.com.engpeng.engpeng.Global.sUsername;

public class TempFeedTransferDetailActivity extends AppCompatActivity {

    private Button btnSave;
    private CheckBox cbShowAll;
    private EditText etFilter, etWeight;
    private RecyclerView rv;
    private SQLiteDatabase mDb;
    private TempFeedTransferDetailAdapter adapter;

    private List<FeedItem> mFeedItemList;
    private String record_date;
    private int company_id, location_id, discharge_house, receive_house;
    private Toast mToast;
    private Long feed_transfer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_transfer_detail);

        btnSave = findViewById(R.id.temp_feed_transfer_detail_btn_save);
        cbShowAll = findViewById(R.id.temp_feed_transfer_detail_cb_show_all);
        etFilter = findViewById(R.id.temp_feed_transfer_detail_et_filter);
        etWeight = findViewById(R.id.temp_feed_transfer_detail_et_weight);
        rv = this.findViewById(R.id.temp_feed_transfer_detail_rv);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mToast = new Toast(this);

        setTitle("Select Feed (Pilih Baja Yang Dihantar)");

        setupStartIntent();
        setupListener();
        setupRecycleView();
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
        if (intentStart.hasExtra(I_KEY_DISCHARGE_HOUSE)) {
            discharge_house = intentStart.getIntExtra(I_KEY_DISCHARGE_HOUSE, 0);
        }
        if (intentStart.hasExtra(I_KEY_RECEIVE_HOUSE)) {
            receive_house = intentStart.getIntExtra(I_KEY_RECEIVE_HOUSE, 0);
        }
    }

    private void setupRecycleView() {

        rv.setLayoutManager(new LinearLayoutManager(this));

        mFeedItemList = new ArrayList<>();
        Cursor feedItemCursor;
        if (cbShowAll.isChecked()) {
            String filter = etFilter.getText().toString();
            feedItemCursor = FeedItemController.getByFilter(mDb, filter);
        } else {
            String feed_in_ids = FeedInController.getAllIdByCL(mDb, company_id, location_id);
            String item_packing_ids = FeedInDetailController.getItemPackingIdByMultiFeedInId(mDb, feed_in_ids);
            feedItemCursor = FeedItemController.getByMultiErpId(mDb, item_packing_ids);
        }

        while (feedItemCursor.moveToNext()) {
            FeedItem fi = new FeedItem();
            fi.setErpId(feedItemCursor.getInt(feedItemCursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_ERP_ID)));
            fi.setSkuCode(feedItemCursor.getString(feedItemCursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_CODE)));
            fi.setSkuName(feedItemCursor.getString(feedItemCursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_NAME)));
            mFeedItemList.add(fi);
        }

        adapter = new TempFeedTransferDetailAdapter(this, mFeedItemList);
        rv.setAdapter(adapter);
    }

    private void setupListener() {
        cbShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRecycleView();
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cbShowAll.isChecked()) {
                    setupRecycleView();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    Intent mainIntent = new Intent(TempFeedTransferDetailActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                    String printText = PrintUtils.printFeedTransfer(mDb, feed_transfer_id);

                    Intent ppIntent = new Intent(TempFeedTransferDetailActivity.this, PrintPreview2Activity.class);
                    ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                    startActivity(ppIntent);
                }
            }
        });
    }

    private boolean save() {
        if (mToast != null) {
            mToast.cancel();
        }

        int item_packing_id = 0;
        double weight = 0;

        for (FeedItem fi : mFeedItemList) {
            if (fi.isSelect()) {
                item_packing_id = fi.getErpId();
                break;
            }
        }

        if (item_packing_id == 0) {
            mToast = Toast.makeText(this, "Please select feed", Toast.LENGTH_SHORT);
            mToast.show();
            return false;
        }

        if (etWeight.getText().length() == 0) {
            etWeight.setError(getString(R.string.error_field_required));
            etWeight.requestFocus();
            return false;

        } else {
            try {
                weight = Double.parseDouble(etWeight.getText().toString());
            } catch (NumberFormatException ex) {
                etWeight.setError(getString(R.string.error_field_number_only));
                etWeight.requestFocus();
                return false;
            }
        }

        String running_no = RUNNING_CODE_TRANSFER +"-" + sUsername + "-1";
        String last_running_no = FeedTransferController.getLastRunningNo(mDb);
        if (!last_running_no.equals("")) {
            String[] arr = last_running_no.split("-");
            int new_no = Integer.parseInt(arr[2]) + 1;
            running_no = RUNNING_CODE_TRANSFER +"-" + sUsername + "-" + new_no;
        }

        feed_transfer_id = FeedTransferController.add(mDb,
                company_id,
                location_id,
                record_date,
                running_no,
                discharge_house,
                receive_house,
                item_packing_id,
                weight
        );

        return true;
    }
}
