package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempFeedReceiveDetailFeedAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedReceiveDetailHouseAdapter;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.controller.TempFeedReceiveDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.FeedItem;
import my.com.engpeng.engpeng.model.HouseCode;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.QR_LINE_TYPE_DETAIL;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_FIELD;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_LINE;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedReceiveDetailActivity extends AppCompatActivity
        implements TempFeedReceiveDetailFeedAdapter.TempFeedReceiveDetailFeedAdapterListener{

    private int company_id, location_id;
    private String qr_data;

    private Button btnSave, btnExit;
    private EditText etWeight;

    private List<FeedItem> mFeedItemList;
    private List<HouseCode> mHouseList;

    private TempFeedReceiveDetailFeedAdapter mFeedAdapter;
    private TempFeedReceiveDetailHouseAdapter mHouseAdapter;
    private RecyclerView rvHouse, rvFeed;
    private SQLiteDatabase mDb;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_receive_detail);

        btnSave = findViewById(R.id.temp_feed_receive_detail_btn_save);
        btnExit = findViewById(R.id.temp_feed_receive_detail_btn_exit);
        etWeight = findViewById(R.id.temp_feed_receive_detail_et_weight);

        rvFeed = this.findViewById(R.id.temp_feed_receive_detail_rv_feed);
        rvHouse = this.findViewById(R.id.temp_feed_receive_detail_rv_house);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mToast = new Toast(this);

        setTitle("Feed Receive Detail");

        setupStartIntent();
        setupFeedRecycleView();
        setupHouseRecycleView();
        setupListener();
        etWeight.requestFocus();
    }

    public void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
        if (intentStart.hasExtra(I_KEY_QR_DATA)) {
            qr_data = intentStart.getStringExtra(I_KEY_QR_DATA);
        }
    }

    public void setupFeedRecycleView() {
        rvFeed.setLayoutManager(new LinearLayoutManager(this));

        mFeedItemList = new ArrayList<>();
        String[] lines = qr_data.split(QR_SPLIT_LINE);

        for (String line : lines) {
            String[] fields = line.split(QR_SPLIT_FIELD);

            String type = fields[0];
            if (type.equals(QR_LINE_TYPE_DETAIL)) {

                int sku_id = Integer.parseInt(fields[1]);
                double weight = Double.parseDouble(fields[2]);


                String sku_code = "";
                String sku_name = "";
                Cursor cursor = FeedItemController.getByErpId(mDb, sku_id);
                if (cursor.moveToFirst()) {
                    sku_code = cursor.getString(cursor.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                    sku_name = cursor.getString(cursor.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
                }else{
                    sku_code = "New Feed";
                    sku_name = "ITEM_PACKING_ID: " + sku_id;
                }

                FeedItem fi = new FeedItem();
                fi.setErpId(sku_id);
                fi.setSkuCode(sku_code);
                fi.setSkuName(sku_name);
                fi.setWeight(weight);

                mFeedItemList.add(fi);
            }
        }

        mFeedAdapter = new TempFeedReceiveDetailFeedAdapter(this, mFeedItemList, this);
        rvFeed.setAdapter(mFeedAdapter);
    }

    @Override
    public void afterSelectFeed() {
        for (FeedItem fi : mFeedItemList) {
            if (fi.isSelect()) {

                etWeight.setText(fi.getWeight()+"");
                etWeight.selectAll();

                break;
            }
        }
    }

    public void setupHouseRecycleView() {
        rvHouse.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = HouseController.getHouseCodeByLocationId(mDb, location_id);

        mHouseList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int house_code = cursor.getInt(cursor.getColumnIndex(HouseEntry.COLUMN_HOUSE_CODE));

            HouseCode hc = new HouseCode();
            hc.setHouseCode(house_code);

            mHouseList.add(hc);
        }
        mHouseAdapter = new TempFeedReceiveDetailHouseAdapter(this, mHouseList);
        rvHouse.setAdapter(mHouseAdapter);
    }

    public FeedItem getSelectedFeedItem() {
        for (FeedItem fi : mFeedItemList) {
            if (fi.isSelect()) {
                return fi;
            }
        }
        return null;
    }

    private void setupListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    finish();
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean save() {
        if (mToast != null) {
            mToast.cancel();
        }

        int house_code = 0, item_packing_id = 0;
        double  weight;

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

        TempFeedReceiveDetailController.add(mDb,
                house_code,
                item_packing_id,
                weight);

        return true;
    }
}
