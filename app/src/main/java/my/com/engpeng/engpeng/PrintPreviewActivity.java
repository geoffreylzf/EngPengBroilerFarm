package my.com.engpeng.engpeng;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.PrintPreviewAsyncTaskLoader;
import my.com.engpeng.engpeng.utilities.BarcodeUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_BOTTOM;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TOP;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;

public class PrintPreviewActivity extends AppCompatActivity
        implements AppLoader.AppLoaderListener, PrintPreviewAsyncTaskLoader.PrintPreviewAsyncTaskLoaderListener {

    private TextView tvText;
    private ImageView ivQrCode;
    private Button btnPrint;
    private String strPrintText, strQRText, module;
    private byte[] byteQRCodeTop, byteQRCodeBottom;
    private Long id;
    private Dialog progressDialog;

    private Bitmap QRBitmap, QRBitmapTop, QRBitmapBottom;

    private AppLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);

        tvText = findViewById(R.id.print_preview_tv_text);
        ivQrCode = findViewById(R.id.print_preview_iv_qr);
        btnPrint = findViewById(R.id.print_preview_btn_print);

        progressDialog = UIUtils.getProgressDialog(this);

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_PRINT_TEXT)) {
            strPrintText = intentStart.getStringExtra(I_KEY_PRINT_TEXT);
        }

        if (intentStart.hasExtra(I_KEY_PRINT_QR_TEXT)) {
            strQRText = intentStart.getStringExtra(I_KEY_PRINT_QR_TEXT);
            QRBitmap = BarcodeUtils.encodeStringAsQrCodeBitmap(strQRText);
            QRBitmapTop = BarcodeUtils.getTopPartBitmap(QRBitmap);
            QRBitmapBottom = BarcodeUtils.getBottomPartBitmap(QRBitmap);

            ivQrCode.setImageBitmap(QRBitmap);
        }

        if (intentStart.hasExtra(I_KEY_MODULE)) {
            module = intentStart.getStringExtra(I_KEY_MODULE);
        }
        if (intentStart.hasExtra(I_KEY_ID)) {
            id = intentStart.getLongExtra(I_KEY_ID, 0);
        }

        tvText.setText(strPrintText);
        setupListener();

        setTitle("Print Preview");

        loader = new AppLoader(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new PrintPreviewAsyncTaskLoader(this, this);
    }

    @Override
    public void beforePrintPreviewAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public String afterPrintPreviewAsyncTaskLoaderStart() {

        if(strQRText!=null){
            byteQRCodeTop = BarcodeUtils.decodeBitmapAsByteArray(QRBitmapTop);
            byteQRCodeBottom= BarcodeUtils.decodeBitmapAsByteArray(QRBitmapBottom);
        }

        return "true";
    }

    @Override
    public void afterLoaderDone(String data) {
        progressDialog.hide();
        Intent btIntent = new Intent(PrintPreviewActivity.this, BluetoothActivity.class);
        btIntent.putExtra(I_KEY_PRINT_TEXT, strPrintText);
        btIntent.putExtra(I_KEY_PRINT_QR_TEXT, strQRText);
        btIntent.putExtra(I_KEY_PRINT_QR_TOP, byteQRCodeTop);
        btIntent.putExtra(I_KEY_PRINT_QR_BOTTOM, byteQRCodeBottom);
        btIntent.putExtra(I_KEY_MODULE, module);
        btIntent.putExtra(I_KEY_ID, id);
        startActivity(btIntent);
    }

    private void setupListener(){
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle queryBundle = new Bundle();
                LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.initLoader(Global.PRINT_PREVIEW_LOADER_ID, queryBundle, loader);
            }
        });
    }
}
