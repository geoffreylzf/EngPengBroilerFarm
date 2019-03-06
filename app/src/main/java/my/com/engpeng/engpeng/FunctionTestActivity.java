package my.com.engpeng.engpeng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import my.com.engpeng.engpeng.barcode.BarcodeCaptureActivity;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.REQUEST_CODE_BARCODE_CAPTURE;

public class FunctionTestActivity extends AppCompatActivity {

    private Button btnScan, btnPrint;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_test);

        btnScan = findViewById(R.id.function_test_btn_scan);
        btnPrint = findViewById(R.id.function_test_btn_print);

        setupListener();

        setTitle("Function Test");
    }

    private void setupListener(){
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FunctionTestActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                startActivityForResult(intent, REQUEST_CODE_BARCODE_CAPTURE);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ppIntent = new Intent(FunctionTestActivity.this, PrintPreviewActivity.class);
                ppIntent.putExtra(I_KEY_PRINT_TEXT, "TEST-TEST-TEST-TEST-TEST-TEST\nTEST-TEST-TEST-TEST-TEST-TEST\nTEST-TEST-TEST-TEST-TEST-TEST\n");
                ppIntent.putExtra(I_KEY_PRINT_QR_TEXT, "TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST-TEST");
                startActivity(ppIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String scannedText = barcode.displayValue;

                    UIUtils.getMessageDialog(this, "Scan Result", scannedText).show();

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
