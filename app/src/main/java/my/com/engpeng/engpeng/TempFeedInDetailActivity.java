package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempFeedInDetailFeedAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedInDetailHouseAdapter;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.FeedItem;
import my.com.engpeng.engpeng.model.HouseCode;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_ID_LIST;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;

public class TempFeedInDetailActivity extends AppCompatActivity {

    private int company_id, location_id;
    private String type, feed_list;

    private Button btnSave, btnExit;
    private TextInputLayout tilQty;
    private EditText etQty;

    private List<FeedItem> mFeedItemList;
    private List<HouseCode> mHouseList;

    private TempFeedInDetailFeedAdapter mFeedAdapter;
    private TempFeedInDetailHouseAdapter mHouseAdapter;
    private RecyclerView rvHouse, rvFeed;
    private SQLiteDatabase mDb;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_in_detail);

        btnSave = findViewById(R.id.temp_feed_in_detail_btn_save);
        btnExit = findViewById(R.id.temp_feed_in_detail_btn_exit);
        tilQty = findViewById(R.id.temp_feed_in_detail_til_qty);
        etQty = findViewById(R.id.temp_feed_in_detail_et_qty);

        rvFeed = this.findViewById(R.id.temp_feed_in_detail_rv_feed);
        rvHouse = this.findViewById(R.id.temp_feed_in_detail_rv_house);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mToast = new Toast(this);

        setTitle("Feed In Detail");

        setupStartIntent();
        setupFeedRecycleView();
        setupHouseRecycleView();
        setupListener();
        etQty.requestFocus();
    }

    public void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
        if (intentStart.hasExtra(I_KEY_TYPE)) {
            type = intentStart.getStringExtra(I_KEY_TYPE);
            if(type.equals("BG")){
                tilQty.setHint("Quantity (Bag)");
                etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
            }else if (type.equals("MT")){
                tilQty.setHint("Quantity (MT)");
                etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else{
                tilQty.setHint("Quantity");
            }
        }
        if (intentStart.hasExtra(I_KEY_ID_LIST)) {
            feed_list = intentStart.getStringExtra(I_KEY_ID_LIST);
        }
    }

    public void setupFeedRecycleView(){
        rvFeed.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = FeedItemController.getByMultiErpId(mDb, feed_list);

        mFeedItemList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int erp_id = cursor.getInt(cursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_ERP_ID));
            String sku_code = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_CODE));
            String sku_name = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_NAME));

            FeedItem fi = new FeedItem();
            fi.setErpId(erp_id);
            fi.setSkuCode(sku_code);
            fi.setSkuName(sku_name);

            mFeedItemList.add(fi);
        }
        mFeedAdapter = new TempFeedInDetailFeedAdapter(this, mFeedItemList);
        rvFeed.setAdapter(mFeedAdapter);
    }

    public void setupHouseRecycleView(){
        rvHouse.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = HouseController.getHouseCodeByLocationId(mDb, location_id);

        mHouseList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int house_code = cursor.getInt(cursor.getColumnIndex(EngPengContract.HouseEntry.COLUMN_HOUSE_CODE));

            HouseCode hc = new HouseCode();
            hc.setHouseCode(house_code);

            mHouseList.add(hc);
        }
        mHouseAdapter = new TempFeedInDetailHouseAdapter(this, mHouseList);
        rvHouse.setAdapter(mHouseAdapter);
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
        double qty;

        for (HouseCode hc: mHouseList){
            if(hc.getIsSelect()){
                house_code = hc.getHouseCode();
                break;
            }
        }
        if(house_code == 0){
            mToast = Toast.makeText(this, "Please select house", Toast.LENGTH_SHORT);
            mToast.show();
            return false;
        }

        for (FeedItem fi: mFeedItemList){
            if(fi.getIsSelect()){
                item_packing_id = fi.getErpId();
                break;
            }
        }
        if(item_packing_id == 0){
            mToast = Toast.makeText(this, "Please select feed", Toast.LENGTH_SHORT);
            mToast.show();
            return false;
        }

        if (etQty.getText().length() == 0) {
            etQty.setError(getString(R.string.error_field_required));
            etQty.requestFocus();
            return false;
        } else {
            try {
                qty = Double.parseDouble(etQty.getText().toString());
            } catch (NumberFormatException ex) {
                etQty.setError(getString(R.string.error_field_number_only));
                etQty.requestFocus();
                return false;
            }
        }

        TempFeedInDetailController.add(mDb, house_code, item_packing_id, qty);
        return true;
    }
}
