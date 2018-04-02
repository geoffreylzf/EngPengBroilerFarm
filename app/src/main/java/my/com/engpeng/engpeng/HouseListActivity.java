package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import my.com.engpeng.engpeng.adapter.HouseListAdapter;
import my.com.engpeng.engpeng.barcode.BarcodeCaptureActivity;
import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.*;

public class HouseListActivity extends AppCompatActivity {

    private HouseListAdapter mAdapter;
    private SQLiteDatabase db;
    private RecyclerView houseListRecyclerView;
    private String module;
    private Button btnBarcode;
    private static final int RC_BARCODE_CAPTURE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list);

        Intent intentStart = getIntent();
        module = intentStart.getStringExtra(I_KEY_MODULE);

        btnBarcode = this.findViewById(R.id.house_list_btn_barcode);
        houseListRecyclerView = this.findViewById(R.id.house_list_view);
        houseListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        String moduleStr = "";
        if (module.equals(MODULE_MORTALITY)) {
            moduleStr = "Mortality";
        } else if (module.equals(MODULE_WEIGHT)) {
            moduleStr = "Body Weight";
        }

        setTitle(moduleStr + " - Select House");

        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cursor cursor = HouseController.getHouseCodeByLocationId(db, Global.sLocationId);
        mAdapter = new HouseListAdapter(this, cursor, db, module);
        houseListRecyclerView.setAdapter(mAdapter);
    }

    private void setupListener() {
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HouseListActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String scannedText = barcode.displayValue;

                    if (scannedText.length() == 6) {
                        int scanned_location_id = Integer.valueOf(scannedText.substring(0, 4));
                        int scanned_house_code = Integer.valueOf(scannedText.substring(4, 6));

                        if (scanned_location_id != sLocationId) {
                            Cursor cursor = BranchController.getBranchByErpId(db, scanned_location_id);
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                int new_company_id = cursor.getInt(cursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_COMPANY_ID));
                                SharedPreferencesUtils.saveCompanyIdLocationId(this, new_company_id, scanned_location_id);
                                Global.setupGlobalVariables(this, db);
                            } else {
                                UIUtils.getMessageDialog(this, "Data Error", "Location from barcode scanned is invalid.").show();
                                return;
                            }
                        }

                        if (HouseController.checkExistByLocationIdHouseCode(db, scanned_location_id, scanned_house_code)) {
                            if (module.equals(MODULE_MORTALITY)) {

                                Intent mortalityIntent = new Intent(this, MortalityActivity.class);
                                mortalityIntent.putExtra(I_KEY_HOUSE_CODE, scanned_house_code);
                                this.startActivity(mortalityIntent);

                            } else if (module.equals(MODULE_WEIGHT)) {

                                Intent tempWeightHeadIntent = new Intent(this, TempWeightHeadActivity.class);
                                tempWeightHeadIntent.putExtra(I_KEY_HOUSE_CODE, scanned_house_code);
                                this.startActivity(tempWeightHeadIntent);
                            }
                        } else {
                            UIUtils.getMessageDialog(this, "Data Error", "House code from barcode scanned is invalid.").show();
                        }
                    } else {
                        UIUtils.getMessageDialog(this, "Data Error", "Barcode scanned is invalid.").show();
                    }
                } else {
                    UIUtils.showToastMessage(this, "No barcode captured");
                }
            } else {
                UIUtils.showToastMessage(this, "Fail to read barcode");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
