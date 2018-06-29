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

import my.com.engpeng.engpeng.adapter.TempFeedDischargeDetailFeedAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedDischargeDetailHouseAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedTransferDetailAdapter;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.controller.TempFeedDischargeDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.FeedItem;
import my.com.engpeng.engpeng.model.HouseCode;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;

public class TempFeedDischargeDetailActivity extends AppCompatActivity {

    private int company_id, location_id;

    private CheckBox cbShowAll;
    private Button btnSave, btnExit;
    private EditText etFilter, etWeight;

    private List<FeedItem> mFeedItemList;
    private List<HouseCode> mHouseList;

    private TempFeedDischargeDetailHouseAdapter mHouseAdapter;
    private TempFeedDischargeDetailFeedAdapter mFeedAdapter;
    private RecyclerView rvHouse, rvFeed;
    private SQLiteDatabase mDb;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_discharge_detail);

        cbShowAll = findViewById(R.id.temp_feed_discharge_detail_cb_show_all);
        btnSave = findViewById(R.id.temp_feed_discharge_detail_btn_save);
        btnExit = findViewById(R.id.temp_feed_discharge_detail_btn_exit);
        etWeight = findViewById(R.id.temp_feed_discharge_detail_et_weight);
        etFilter = findViewById(R.id.temp_feed_discharge_detail_et_filter);

        rvHouse = findViewById(R.id.temp_feed_discharge_detail_rv_house);
        rvFeed = findViewById(R.id.temp_feed_discharge_detail_rv_feed);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mToast = new Toast(this);

        setTitle("Feed Discharge Detail");

        setupStartIntent();
        setupHouseRecycleView();
        setupFeedRecycleView();
        setupListener();
        etWeight.requestFocus();
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

    private void setupHouseRecycleView() {
        rvHouse.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = HouseController.getHouseCodeByLocationId(mDb, location_id);

        mHouseList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int house_code = cursor.getInt(cursor.getColumnIndex(EngPengContract.HouseEntry.COLUMN_HOUSE_CODE));

            HouseCode hc = new HouseCode();
            hc.setHouseCode(house_code);

            mHouseList.add(hc);
        }
        mHouseAdapter = new TempFeedDischargeDetailHouseAdapter(this, mHouseList);
        rvHouse.setAdapter(mHouseAdapter);
    }

    private void setupFeedRecycleView(){
        rvFeed.setLayoutManager(new LinearLayoutManager(this));

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

        mFeedAdapter = new TempFeedDischargeDetailFeedAdapter(this, mFeedItemList);
        rvFeed.setAdapter(mFeedAdapter);
    }

    private void setupListener(){
        cbShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupFeedRecycleView();
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
                    setupFeedRecycleView();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    finish();
                }
            }
        });
    }

    public FeedItem getSelectedFeedItem() {
        for (FeedItem fi : mFeedItemList) {
            if (fi.isSelect()) {
                return fi;
            }
        }
        return null;
    }

    private boolean save(){
        if (mToast != null) {
            mToast.cancel();
        }

        int house_code = 0, item_packing_id = 0;
        double weight;

        for (HouseCode hc : mHouseList) {
            if (hc.getIsSelect()) {
                house_code = hc.getHouseCode();
                break;
            }
        }

        if (house_code == 0) {
            mToast = Toast.makeText(this, "Please select house", Toast.LENGTH_SHORT);
            mToast.show();
            return false;
        }

        if (getSelectedFeedItem() != null) {
            item_packing_id = getSelectedFeedItem().getErpId();
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

        TempFeedDischargeDetailController.add(mDb,
                house_code,
                item_packing_id,
                weight);

        return true;
    }
}
