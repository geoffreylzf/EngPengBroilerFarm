package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempFeedInItemSelectionAdapter;
import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.FeedItem;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_DOC_NUMBER;
import static my.com.engpeng.engpeng.Global.I_KEY_ID_LIST;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_RECORD_DATE;
import static my.com.engpeng.engpeng.Global.I_KEY_TRUCK_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_TYPE;

public class TempFeedInItemSelectionActivity extends AppCompatActivity {

    private TempFeedInItemSelectionAdapter mAdapter;
    private List<FeedItem> mFeedItemList;
    private Button btnStart;
    private RecyclerView rv;
    private SQLiteDatabase mDb;

    private int company_id, location_id, doc_number;
    private String record_date, type, truck_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_in_item_selection);

        btnStart = this.findViewById(R.id.temp_feed_in_item_selection_btn_start);
        rv = this.findViewById(R.id.temp_feed_in_item_selection_rv);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        setTitle("Select Multiple Item (Pilih Baja Yang Dihantar)");

        setupStartIntent();
        setupRecycleView();
        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TempFeedInDetailController.delete(mDb);
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
        if (intentStart.hasExtra(I_KEY_DOC_NUMBER)) {
            doc_number = intentStart.getIntExtra(I_KEY_DOC_NUMBER, 0);
        }
        if (intentStart.hasExtra(I_KEY_TYPE)) {
            type = intentStart.getStringExtra(I_KEY_TYPE);
        }
        if (intentStart.hasExtra(I_KEY_TRUCK_CODE)) {
            truck_code = intentStart.getStringExtra(I_KEY_TRUCK_CODE);
        }
    }



    public void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        int item_uom_id = 0;
        if (type.equals("BG")) {
            item_uom_id = FeedItemController.BG_UOM_ID;
        } else if (type.equals("MT")) {
            item_uom_id = FeedItemController.MT_UOM_ID;
        }

        Cursor cursor = FeedItemController.getByUom(mDb, item_uom_id);

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

        mAdapter = new TempFeedInItemSelectionAdapter(this, mFeedItemList);
        rv.setAdapter(mAdapter);
    }

    protected void setupListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_erp_id = "";
                boolean isFirst = true;
                for (FeedItem fi : mFeedItemList) {
                    if (fi.getIsSelect()) {
                        if (isFirst) {
                            str_erp_id += fi.getErpId();
                            isFirst = false;
                        } else {
                            str_erp_id += "," + fi.getErpId();
                        }
                    }
                }

                Intent sumIntent = new Intent(TempFeedInItemSelectionActivity.this, TempFeedInSummaryActivity.class);
                sumIntent.putExtra(I_KEY_COMPANY, company_id);
                sumIntent.putExtra(I_KEY_LOCATION, location_id);
                sumIntent.putExtra(I_KEY_RECORD_DATE, record_date);
                sumIntent.putExtra(I_KEY_DOC_NUMBER, doc_number);
                sumIntent.putExtra(I_KEY_TYPE, type);
                sumIntent.putExtra(I_KEY_TRUCK_CODE, truck_code);
                sumIntent.putExtra(I_KEY_ID_LIST, str_erp_id);
                startActivity(sumIntent);
            }
        });
    }
}
