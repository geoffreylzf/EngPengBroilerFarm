package my.com.engpeng.engpeng;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import my.com.engpeng.engpeng.controller.TempWeightDetailController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

import static my.com.engpeng.engpeng.Global.I_KEY_COMPANY;
import static my.com.engpeng.engpeng.Global.I_KEY_HOUSE_CODE;
import static my.com.engpeng.engpeng.Global.I_KEY_LOCATION;
import static my.com.engpeng.engpeng.Global.I_KEY_SECTION;

public class TempWeightDetailActivity extends AppCompatActivity {

    private Button btnSave, btnExit;
    private EditText etSection, etWeight, etQty;
    private RadioGroup rgGender;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_weight_detail);

        etSection = findViewById(R.id.temp_weight_detail_et_section);
        etWeight = findViewById(R.id.temp_weight_detail_et_weight);
        etQty = findViewById(R.id.temp_weight_detail_et_quantity);
        rgGender = findViewById(R.id.temp_weight_detail_rg_gender);
        btnSave = findViewById(R.id.temp_weight_detail_btn_save);
        btnExit = findViewById(R.id.temp_weight_detail_btn_exit);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setTitle("New Body Weight Detail");

        setupStartIntent();
        setupListener();
    }

    private void setupListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = new Toast(TempWeightDetailActivity.this);
                if (toast != null) {
                    toast.cancel();
                }
                int section, weight, qty;
                if (etSection.getText().length() == 0) {
                    etSection.setError(getString(R.string.error_field_required));
                    etSection.requestFocus();
                    return;
                }else{
                    try {
                        section = Integer.parseInt(etSection.getText().toString());
                    } catch (NumberFormatException ex) {
                        etSection.setError(getString(R.string.error_field_number_only));
                        etSection.requestFocus();
                        return;
                    }
                }

                if (etWeight.getText().length() == 0) {
                    etWeight.setError(getString(R.string.error_field_required));
                    etWeight.requestFocus();
                    return;
                }else{
                    try {
                        weight = Integer.parseInt(etWeight.getText().toString());
                    } catch (NumberFormatException ex) {
                        etWeight.setError(getString(R.string.error_field_number_only));
                        etWeight.requestFocus();
                        return;
                    }
                }

                if (etQty.getText().length() == 0) {
                    etQty.setError(getString(R.string.error_field_required));
                    etQty.requestFocus();
                    return;
                }else{
                    try {
                        qty = Integer.parseInt(etQty.getText().toString());
                    } catch (NumberFormatException ex) {
                        etQty.setError(getString(R.string.error_field_number_only));
                        etQty.requestFocus();
                        return;
                    }
                }

                int selectedRB = rgGender.getCheckedRadioButtonId();
                if (selectedRB == -1) {
                    toast = Toast.makeText(TempWeightDetailActivity.this, "Please select gender", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                String gender = "P";
                if (selectedRB == R.id.temp_weight_detail_rd_male) {
                    gender = "M";
                } else if (selectedRB == R.id.temp_weight_detail_rd_female) {
                    gender = "F";
                }

                TempWeightDetailController.add(db, section, weight, qty, gender);
                finish();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_SECTION)) {
            etSection.setText(String.valueOf(intentStart.getIntExtra(I_KEY_SECTION, 0)));
        }
    }
}
