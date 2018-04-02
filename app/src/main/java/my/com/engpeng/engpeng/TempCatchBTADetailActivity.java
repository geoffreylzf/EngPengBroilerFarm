package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempCatchBTADetailAdapter;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.controller.TempCatchBTADetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_CAGE_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_CONTINUE_NEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_QTY;
import static my.com.engpeng.engpeng.Global.I_KEY_WITH_COVER_QTY;
import static my.com.engpeng.engpeng.Global.sLocationName;

public class TempCatchBTADetailActivity extends AppCompatActivity {

    private Spinner snHouseCode, snWithCoverQty;
    private Button btnSave, btnSaveAndReturn, btnExit;
    private EditText etWeight, etQty;
    private RadioGroup rgCageQty;
    private RadioButton rbCage1, rbCage2, rbCage3, rbCage4, rbCage5;
    private TempCatchBTADetailAdapter adapter;
    private RecyclerView rv;

    private int company_id, location_id;
    private int last_house_code;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_catch_bta_detail);

        etWeight = findViewById(R.id.temp_catch_bta_detail_et_weight);
        etQty = findViewById(R.id.temp_catch_bta_detail_et_qty);
        snWithCoverQty = findViewById(R.id.temp_catch_bta_detail_sn_with_cover_qty);
        rgCageQty = findViewById(R.id.temp_catch_bta_detail_rg_cage_qty);
        btnSave = findViewById(R.id.temp_catch_bta_detail_btn_save);
        btnSaveAndReturn = findViewById(R.id.temp_catch_bta_detail_btn_save_and_return);
        btnExit = findViewById(R.id.temp_catch_bta_detail_btn_exit);
        snHouseCode = findViewById(R.id.temp_catch_bta_detail_sn_house_code);

        rbCage1 = findViewById(R.id.temp_catch_bta_detail_rd_1_cage);
        rbCage2 = findViewById(R.id.temp_catch_bta_detail_rd_2_cage);
        rbCage3 = findViewById(R.id.temp_catch_bta_detail_rd_3_cage);
        rbCage4 = findViewById(R.id.temp_catch_bta_detail_rd_4_cage);
        rbCage5 = findViewById(R.id.temp_catch_bta_detail_rd_5_cage);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("New Catch BTA Detail (" + sLocationName + ")");

        setupRecycleView();
        setupSpinnerWithCoverQty(0, null);
        setupStartIntent();
        loadSpinnerData();
        setupListener();
        etWeight.requestFocus();
    }

    private void loadSpinnerData() {

        Cursor cursor = HouseController.getHouseCodeByLocationId(db, location_id);

        List<String> labels = new ArrayList<>();

        labels.add(" - ");
        while (cursor.moveToNext()) {
            labels.add(cursor.getString(cursor.getColumnIndex(EngPengContract.HouseEntry.COLUMN_HOUSE_CODE)));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, labels);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        snHouseCode.setAdapter(dataAdapter);

        if (last_house_code > 0) {
            int spinnerPosition = dataAdapter.getPosition(last_house_code + "");
            snHouseCode.setSelection(spinnerPosition);
        }
    }

    private void setupSpinnerWithCoverQty(int qty, Integer default_qty) {
        List<String> labels = new ArrayList<>();
        labels.add(" - ");
        for (int i = 0; i <= qty; i++) {
            labels.add(String.valueOf(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, labels);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        snWithCoverQty.setAdapter(dataAdapter);

        if (default_qty != null) {
            int spinnerPosition = dataAdapter.getPosition(default_qty + "");
            snWithCoverQty.setSelection(spinnerPosition);
        }
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_COMPANY)) {
            company_id = intentStart.getIntExtra(I_KEY_COMPANY, 0);
        }
        if (intentStart.hasExtra(I_KEY_LOCATION)) {
            location_id = intentStart.getIntExtra(I_KEY_LOCATION, 0);
        }
        if (intentStart.hasExtra(I_KEY_HOUSE_CODE)) {
            last_house_code = intentStart.getIntExtra(I_KEY_HOUSE_CODE, 0);
        }
        if (intentStart.hasExtra(I_KEY_QTY)) {
            etQty.setText(String.valueOf(intentStart.getIntExtra(I_KEY_QTY, 0)));
        }
        if (intentStart.hasExtra(I_KEY_CAGE_QTY)) {
            int cage_qty = intentStart.getIntExtra(I_KEY_CAGE_QTY, 0);
            if (cage_qty == 1) {
                rbCage1.setChecked(true);
            } else if (cage_qty == 2) {
                rbCage2.setChecked(true);
            } else if (cage_qty == 3) {
                rbCage3.setChecked(true);
            } else if (cage_qty == 4) {
                rbCage4.setChecked(true);
            } else if (cage_qty == 5) {
                rbCage5.setChecked(true);
            }
            if (intentStart.hasExtra(I_KEY_WITH_COVER_QTY)) {
                setupSpinnerWithCoverQty(cage_qty, intentStart.getIntExtra(I_KEY_WITH_COVER_QTY, 0));
            }
        }
    }

    private void setupListener() {
        rbCage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpinnerWithCoverQty(1, 1);
            }
        });
        rbCage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpinnerWithCoverQty(2, 2);
            }
        });
        rbCage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpinnerWithCoverQty(3, 3);
            }
        });
        rbCage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpinnerWithCoverQty(4, 4);
            }
        });
        rbCage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpinnerWithCoverQty(5, 5);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    Intent intent = new Intent();
                    intent.putExtra(I_KEY_CONTINUE_NEXT, true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        btnSaveAndReturn.setOnClickListener(new View.OnClickListener() {
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
        Toast toast = new Toast(TempCatchBTADetailActivity.this);
        if (toast != null) {
            toast.cancel();
        }

        int qty;
        double weight;

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

        if (etQty.getText().length() == 0) {
            etQty.setError(getString(R.string.error_field_required));
            etQty.requestFocus();
            return false;
        } else {
            try {
                qty = Integer.parseInt(etQty.getText().toString());
            } catch (NumberFormatException ex) {
                etQty.setError(getString(R.string.error_field_number_only));
                etQty.requestFocus();
                return false;
            }
        }

        int house_code;
        if (snHouseCode.getSelectedItemPosition() == 0) {
            toast = Toast.makeText(TempCatchBTADetailActivity.this, "Please select house", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            house_code = Integer.parseInt(snHouseCode.getSelectedItem().toString());
        }

        int selectedCageQtyRB = rgCageQty.getCheckedRadioButtonId();
        if (selectedCageQtyRB == -1) {
            toast = Toast.makeText(TempCatchBTADetailActivity.this, "Please select cage quantity", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        int cage_qty = 1;
        if (selectedCageQtyRB == R.id.temp_catch_bta_detail_rd_1_cage) {
            cage_qty = 1;
        } else if (selectedCageQtyRB == R.id.temp_catch_bta_detail_rd_2_cage) {
            cage_qty = 2;
        } else if (selectedCageQtyRB == R.id.temp_catch_bta_detail_rd_3_cage) {
            cage_qty = 3;
        } else if (selectedCageQtyRB == R.id.temp_catch_bta_detail_rd_4_cage) {
            cage_qty = 4;
        } else if (selectedCageQtyRB == R.id.temp_catch_bta_detail_rd_5_cage) {
            cage_qty = 5;
        }

        int with_cover_qty = 0;
        if (snWithCoverQty.getSelectedItemPosition() == 0) {
            toast = Toast.makeText(TempCatchBTADetailActivity.this, "Please select cage qty with cover", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            with_cover_qty = Integer.parseInt(snWithCoverQty.getSelectedItem().toString());
        }

        TempCatchBTADetailController.add(db, weight, qty, house_code, cage_qty, with_cover_qty);
        return true;
    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.temp_catch_bta_detail_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = TempCatchBTADetailController.getAll(db);

        adapter = new TempCatchBTADetailAdapter(this, cursor);
        rv.setAdapter(adapter);
    }
}
