package my.com.engpeng.engpeng;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.dialog.BluetoothDialogFragment;
import my.com.engpeng.engpeng.model.Bluetooth;
import my.com.engpeng.engpeng.utilities.BarcodeUtils;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;

public class PrintPreview2Activity extends AppCompatActivity {

    private ImageButton ibBt, ibBtRefresh, ibBtStart;
    private TextView tvBtStatus, tvBtName, tvBtAddress, tvPrint;
    private ImageView ivQr;

    private BluetoothSPP bt;
    private String btName = "";
    private String btAddress = "";

    private String strPrintText, strQRText;
    private Bitmap QRBitmap, QRBitmapTop, QRBitmapBottom;


    private String module;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview2);

        ibBt = findViewById(R.id.btn_bt);
        ibBtRefresh = findViewById(R.id.btn_bt_refresh);
        ibBtStart = findViewById(R.id.btn_bt_start);
        ibBtRefresh.setEnabled(false);
        ibBtStart.setEnabled(false);

        tvBtStatus = findViewById(R.id.tv_bt_status);
        tvBtName = findViewById(R.id.tv_bt_name);
        tvBtAddress = findViewById(R.id.tv_bt_address);

        ivQr = findViewById(R.id.iv_qr);
        tvPrint = findViewById(R.id.tv_print);


        bt = new BluetoothSPP(this);

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_PRINT_TEXT)) {
            strPrintText = intentStart.getStringExtra(I_KEY_PRINT_TEXT);
            tvPrint.setText(strPrintText);
        }
        if (intentStart.hasExtra(I_KEY_PRINT_QR_TEXT)) {
            strQRText = intentStart.getStringExtra(I_KEY_PRINT_QR_TEXT);

            QRBitmap = BarcodeUtils.encodeStringAsQrCodeBitmap(strQRText);
            QRBitmapTop = BarcodeUtils.getTopPartBitmap(QRBitmap);
            QRBitmapBottom = BarcodeUtils.getBottomPartBitmap(QRBitmap);

            ivQr.setImageBitmap(QRBitmap);
        }

        if (intentStart.hasExtra(I_KEY_MODULE)) {
            module = intentStart.getStringExtra(I_KEY_MODULE);
        }
        if (intentStart.hasExtra(I_KEY_ID)) {
            id = intentStart.getLongExtra(I_KEY_ID, 0);
        }

        ibBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDialogFragment.show(getSupportFragmentManager(),
                        Arrays.asList(bt.getPairedDeviceName()),
                        Arrays.asList(bt.getPairedDeviceAddress()),
                        new BluetoothDialogFragment.Listener() {
                            @Override
                            public void onSelect(Bluetooth bluetooth) {
                                SharedPreferencesUtils.savePrinterBluetooth(getApplicationContext(), bluetooth);
                                startPrinterBluetooth();
                            }
                        }
                );
            }
        });

        ibBtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPrinterBluetooth();
            }
        });

        ibBtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strQRText != null) {
                    bt.send(BarcodeUtils.decodeBitmapAsByteArray(QRBitmapTop), false);
                    bt.send(BarcodeUtils.decodeBitmapAsByteArray(QRBitmapBottom), false);
                }
                bt.send(strPrintText, true);
                bt.send("\n\n\n\n\n", true);
                afterPrintDone();
            }
        });

    }

    private void startPrinterBluetooth() {
        if (!bt.isBluetoothAvailable()) {
            tvBtStatus.setText(getString(R.string.bt_status, "Not Available"));
            ibBtRefresh.setEnabled(false);
            return;
        }
        if (!bt.isBluetoothEnabled()) {
            tvBtStatus.setText(getString(R.string.bt_status, "Not Enable"));
            ibBtRefresh.setEnabled(false);
            return;
        }
        ibBtRefresh.setEnabled(true);

        Bluetooth savedBt = SharedPreferencesUtils.getPrinterBluetooth(getApplicationContext());
        btName = savedBt.getName();
        btAddress = savedBt.getAddress();

        tvBtStatus.setText(getString(R.string.bt_status, "Not Connected"));
        tvBtName.setText(getString(R.string.bt_name, btName));
        tvBtAddress.setText(getString(R.string.bt_address, btAddress));

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceDisconnected() {
                tvBtStatus.setText(getString(R.string.bt_status, "Not Connected"));
                ibBtStart.setEnabled(false);
            }

            @Override
            public void onDeviceConnectionFailed() {
                tvBtStatus.setText(getString(R.string.bt_status, "Connection Failed"));
                ibBtStart.setEnabled(false);
            }

            @Override
            public void onDeviceConnected(String name, String address) {
                tvBtStatus.setText(getString(R.string.bt_status, "Connected To " + address));
                ibBtStart.setEnabled(true);
            }
        });

        bt.stopService();

        if (!bt.isServiceAvailable()) {
            bt.setupService();
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        }

        if (!btName.equals("") && !btAddress.equals("")) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        bt.connect(btAddress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bt.stopService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPrinterBluetooth();
    }

    public void afterPrintDone() {
        if (module != null) {
            if (module.equals(Global.MODULE_CATCH_BTA)) {
                EngPengDbHelper dbHelper = new EngPengDbHelper(this);
                CatchBTAController.increasePrintCount(dbHelper.getWritableDatabase(), id);
            }
        }
    }
}
