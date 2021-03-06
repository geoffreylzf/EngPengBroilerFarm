package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempFeedInDetailCompartmentAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedInDetailFeedAdapter;
import my.com.engpeng.engpeng.adapter.TempFeedInDetailHouseAdapter;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.controller.TempFeedInDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.model.Compartment;
import my.com.engpeng.engpeng.model.FeedItem;
import my.com.engpeng.engpeng.model.HouseCode;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_QR_DATA;
import static my.com.engpeng.engpeng.Global.QR_LINE_TYPE_COMPARTMENT;
import static my.com.engpeng.engpeng.Global.QR_LINE_TYPE_DETAIL;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_FIELD;
import static my.com.engpeng.engpeng.Global.QR_SPLIT_LINE;
import static my.com.engpeng.engpeng.Global.UOM_CODE_BG;
import static my.com.engpeng.engpeng.Global.UOM_CODE_MT;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedInDetailActivity extends AppCompatActivity
        implements TempFeedInDetailFeedAdapter.TempFeedInDetailFeedAdapterListener,
        TempFeedInDetailCompartmentAdapter.TempFeedInDetailCompartmentAdapterListener {

    private int company_id, location_id;
    private String qr_data;

    private Button btnSave, btnExit;
    private TextInputLayout tilQty;
    private EditText etQty, etWeight;

    private List<FeedItem> mFeedItemList;
    private List<HouseCode> mHouseList;
    private List<Compartment> mCompartmentList;

    private TempFeedInDetailFeedAdapter mFeedAdapter;
    private TempFeedInDetailHouseAdapter mHouseAdapter;
    private TempFeedInDetailCompartmentAdapter mCompartmentAdapter;
    private RecyclerView rvHouse, rvFeed, rvCompartment;
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
        etWeight = findViewById(R.id.temp_feed_in_detail_et_weight);

        rvFeed = this.findViewById(R.id.temp_feed_in_detail_rv_feed);
        rvHouse = this.findViewById(R.id.temp_feed_in_detail_rv_house);
        rvCompartment = this.findViewById(R.id.temp_feed_in_detail_rv_compartment);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mToast = new Toast(this);

        setTitle("Feed In Detail");

        setupStartIntent();
        setupFeedRecycleView();
        setupHouseRecycleView();
        setupCompartmentRecycleView();
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

                Long doc_detail_id = Long.parseLong(fields[1]);
                int sku_id = Integer.parseInt(fields[2]);
                double qty = Double.parseDouble(fields[3]);
                double weight = Double.parseDouble(fields[4]);
                String item_oum_code = fields[5];
                double std_weight = Double.parseDouble(fields[6]);


                String sku_code = "";
                String sku_name = "";
                Cursor cursor = FeedItemController.getByErpId(mDb, sku_id);
                if (cursor.moveToFirst()) {
                    sku_code = cursor.getString(cursor.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                    sku_name = cursor.getString(cursor.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
                } else {
                    sku_code = "New Feed";
                    sku_name = "ITEM_PACKING_ID: " + sku_id;
                }

                Cursor c = TempFeedInDetailController.getAllByByItemPackingId(mDb, sku_id);
                double qty_assigned = 0;
                double weight_assigned = 0;
                while (c.moveToNext()) {
                    qty_assigned += c.getDouble(c.getColumnIndex(TempFeedInDetailEntry.COLUMN_QTY));
                    weight_assigned += c.getDouble(c.getColumnIndex(TempFeedInDetailEntry.COLUMN_WEIGHT));
                }
                qty -= qty_assigned;
                weight -= weight_assigned;

                FeedItem fi = new FeedItem();
                fi.setErpId(sku_id);
                fi.setSkuCode(sku_code);
                fi.setSkuName(sku_name);
                fi.setDocDetailId(doc_detail_id);
                fi.setQty(qty);
                fi.setWeight(weight);
                fi.setItemUomCode(item_oum_code);
                fi.setStdWeight(std_weight);

                mFeedItemList.add(fi);
            }
        }

        mFeedAdapter = new TempFeedInDetailFeedAdapter(this, mFeedItemList, this);
        rvFeed.setAdapter(mFeedAdapter);
    }

    @Override
    public void afterSelectFeed() {
        for (FeedItem fi : mFeedItemList) {
            if (fi.isSelect()) {
                tilQty.setHint("Quantity (" + fi.getItemUomCode() + ")" + "  ~  (1" + fi.getItemUomCode() + "=" + fi.getStdWeight() + "KG)");

                if (fi.getItemUomCode().equals(UOM_CODE_BG)) {
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (fi.getItemUomCode().equals(UOM_CODE_MT)) {
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }

                etQty.setText("");
                etWeight.setText("");
                setupCompartmentRecycleView();

                break;
            }
        }
    }

    public void setupCompartmentRecycleView() {
        rvCompartment.setLayoutManager(new LinearLayoutManager(this));

        FeedItem fi = getSelectedFeedItem();

        if (fi != null) {
            Long selected_doc_detail_id = fi.getDocDetailId();

            mCompartmentList = new ArrayList<>();
            String[] lines = qr_data.split(QR_SPLIT_LINE);

            boolean compartmentMode = false;

            for (String line : lines) {

                String[] fields = line.split(QR_SPLIT_FIELD);
                String type = fields[0];
                if (type.equals(QR_LINE_TYPE_DETAIL)) {
                    Long doc_detail_id = Long.parseLong(fields[1]);

                    if (compartmentMode) {
                        compartmentMode = false;
                    } else {
                        if (doc_detail_id.equals(selected_doc_detail_id)) {
                            compartmentMode = true;
                        }
                    }

                } else if (type.equals(QR_LINE_TYPE_COMPARTMENT)) {
                    if (compartmentMode) {
                        String compartment = fields[1];
                        double qty = Double.parseDouble(fields[2]);
                        double weight = Double.parseDouble(fields[3]);

                        Cursor cursor = TempFeedInDetailController.getAllByByItemPackingIdCompartment(mDb, fi.getErpId(), compartment);
                        double qty_assigned = 0;
                        double weight_assigned = 0;
                        while (cursor.moveToNext()) {
                            qty_assigned += cursor.getDouble(cursor.getColumnIndex(TempFeedInDetailEntry.COLUMN_QTY));
                            weight_assigned += cursor.getDouble(cursor.getColumnIndex(TempFeedInDetailEntry.COLUMN_WEIGHT));
                        }
                        qty -= qty_assigned;
                        weight -= weight_assigned;

                        Compartment c = new Compartment();
                        c.setCompartmentNo(compartment);
                        c.setQty(qty);
                        c.setWeight(weight);

                        mCompartmentList.add(c);
                    }
                }
            }

            if (mCompartmentList != null && mCompartmentList.size() == 0) {
                etQty.setText(fi.getQty() + "");
                etWeight.setText(fi.getWeight() + "");
                etQty.selectAll();
            }
        }

        mCompartmentAdapter = new TempFeedInDetailCompartmentAdapter(this, mCompartmentList, this);
        rvCompartment.setAdapter(mCompartmentAdapter);
    }

    @Override
    public void afterSelectCompartment() {
        for (Compartment c : mCompartmentList) {
            if (c.isSelect()) {
                etQty.setText(c.getQty() + "");
                etWeight.setText(c.getWeight() + "");
                etQty.selectAll();
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
        mHouseAdapter = new TempFeedInDetailHouseAdapter(this, mHouseList);
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

    public Compartment getSelectedCompartment() {
        for (Compartment c : mCompartmentList) {
            if (c.isSelect()) {
                return c;
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
        etQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (etQty.getText().length() != 0) {
                        try {
                            if (getSelectedFeedItem() != null) {
                                double qty = Double.parseDouble(etQty.getText().toString());
                                etWeight.setText(String.valueOf(qty * getSelectedFeedItem().getStdWeight()));
                            }
                        } catch (NumberFormatException ex) {
                            etQty.setError(getString(R.string.error_field_number_only));
                            etQty.requestFocus();
                        }
                    }
                }
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {
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
                if (etWeight.getText().length() != 0) {
                    try {
                        if (getSelectedFeedItem() != null) {
                            double weight = Double.parseDouble(etWeight.getText().toString());
                            etQty.setText(String.valueOf(weight / getSelectedFeedItem().getStdWeight()));
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean save() {
        if (mToast != null) {
            mToast.cancel();
        }

        Long doc_detail_id = 0L;
        int house_code = 0, item_packing_id = 0;
        double qty, weight;
        String compartment_no = "";

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
            doc_detail_id = getSelectedFeedItem().getDocDetailId();
            item_packing_id = getSelectedFeedItem().getErpId();
        }

        if (item_packing_id == 0 || doc_detail_id == 0L) {
            mToast = Toast.makeText(this, "Please select feed", Toast.LENGTH_SHORT);
            mToast.show();
            return false;
        }

        if (getSelectedFeedItem().getItemUomCode().equals(UOM_CODE_MT)) {
            if (getSelectedCompartment() == null) {
                mToast = Toast.makeText(this, "Please select compartment", Toast.LENGTH_SHORT);
                mToast.show();
                return false;
            } else {
                compartment_no = getSelectedCompartment().getCompartmentNo();
            }
        }

        if (etQty.getText().length() == 0) {
            etQty.setError(getString(R.string.error_field_required));
            etQty.requestFocus();
            return false;
        } else {
            try {
                qty = Double.parseDouble(etQty.getText().toString());
                if(qty == 0){
                    etQty.setError(getString(R.string.error_field_no_zero));
                    etQty.requestFocus();
                    return false;
                }
                etWeight.setText(String.valueOf(qty * getSelectedFeedItem().getStdWeight()));
            } catch (NumberFormatException ex) {
                etQty.setError(getString(R.string.error_field_number_only));
                etQty.requestFocus();
                return false;
            }
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

        TempFeedInDetailController.add(mDb,
                doc_detail_id,
                house_code,
                item_packing_id,
                compartment_no,
                qty,
                weight);

        return true;
    }
}
