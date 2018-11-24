package my.com.engpeng.engpeng;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import my.com.engpeng.engpeng.bluetoothWeighing.BluetoothSpp;
import my.com.engpeng.engpeng.bluetoothWeighing.BluetoothState;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_BLUETOOTH_ADDRESS;
import static my.com.engpeng.engpeng.Global.I_KEY_BLUETOOTH_NAME;
import static my.com.engpeng.engpeng.Global.I_KEY_NETT_VALUE;
import static my.com.engpeng.engpeng.Global.PREF_KEY;
import static my.com.engpeng.engpeng.Global.P_KEY_BLUETOOTH_ADDRESS;
import static my.com.engpeng.engpeng.Global.P_KEY_BLUETOOTH_NAME;
import static my.com.engpeng.engpeng.Global.REQUEST_CODE_BLUETOOTH_DEVICE;

public class TempCatchBtaBluetoothActivity extends AppCompatActivity {

    private TextView tvStatus, tvGross, tvTare, tvNett, tvBtName, tvBtAddress;
    private Button btnStart, btnGet;
    private static final String DEFAULT_BT_WEIGHING = "HC-05";

    private BluetoothSpp bt;
    private Set<BluetoothDevice> btDevices;
    private String bluetoothWeighingName = "";
    private String bluetoothWeighingAddress = "";

    public static final String PREFIX_GROSS = "G.W.:";
    public static final String PREFIX_TARE = "T.W.:";
    public static final String PREFIX_NETT = "N.W.:";

    private double nettValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_catch_bta_bluetooth);

        tvGross = findViewById(R.id.temp_catch_bta_bluetooth_tv_gross_value);
        tvTare = findViewById(R.id.temp_catch_bta_bluetooth_tv_tare_value);
        tvNett = findViewById(R.id.temp_catch_bta_bluetooth_tv_nett_value);
        tvStatus = findViewById(R.id.temp_catch_bta_bluetooth_tv_status);
        tvBtName = findViewById(R.id.temp_catch_bta_bluetooth_tv_bt_name);
        tvBtAddress = findViewById(R.id.temp_catch_bta_bluetooth_tv_bt_address);

        btnStart = findViewById(R.id.temp_catch_bta_bluetooth_btn_start);
        btnGet = findViewById(R.id.temp_catch_bta_bluetooth_btn_get);

        setupListener();

        tvStatus.setText("Not connected to any device");
        setTitle("Sistem Timbangan Berat");

        setupBluetooth();

        SharedPreferences prefs = this.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if (prefs.contains(P_KEY_BLUETOOTH_NAME) && prefs.contains(P_KEY_BLUETOOTH_ADDRESS)) {
            bluetoothWeighingName = prefs.getString(P_KEY_BLUETOOTH_NAME, "");
            bluetoothWeighingAddress = prefs.getString(P_KEY_BLUETOOTH_ADDRESS, "");
            tvBtName.setText("Name : " + bluetoothWeighingName);
            tvBtAddress.setText("Address : " + bluetoothWeighingAddress);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                //bt.startService(BluetoothState.DEVICE_ANDROID);
                bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catch_bta_bluetooth_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_catch_bta_bluetooth) {
            startActivityForResult(new Intent(TempCatchBtaBluetoothActivity.this, BluetoothSelectionActivity.class), REQUEST_CODE_BLUETOOTH_DEVICE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH_DEVICE) {
            if (resultCode == RESULT_OK) {
                bluetoothWeighingName = data.getStringExtra(I_KEY_BLUETOOTH_NAME);
                bluetoothWeighingAddress = data.getStringExtra(I_KEY_BLUETOOTH_ADDRESS);
                tvBtName.setText("Name : " + bluetoothWeighingName);
                tvBtAddress.setText("Address : " + bluetoothWeighingAddress);
                btnStart.setEnabled(true);
                SharedPreferencesUtils.saveWeighingBluetooth(this, bluetoothWeighingName, bluetoothWeighingAddress);
            }
        }
    }

    public void setupBluetooth() {
        bt = new BluetoothSpp(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();

        } else {
            btDevices = bt.getBondedDevices();

        }

        bt.setOnDataReceivedListener(new BluetoothSpp.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                populateData(message);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSpp.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                tvStatus.setText("Status : Not connect");
            }

            public void onDeviceConnectionFailed() {
                tvStatus.setText("Status : Connection failed");
                btnStart.setEnabled(true);
            }

            public void onDeviceConnected(String name, String address) {
                tvStatus.setText("Status : Connected to " + name + " (" + address + ")");
            }
        });
    }

    public void connectNow() {
        String address = bluetoothWeighingAddress;
        if (address != null && !address.equals("") ) {
            bt.connect(address);
            btnStart.setEnabled(false);
        }else{
            UIUtils.showToastMessage(this, "No bluetooth weighing system set.");
        }
    }

    public void setupListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectNow();
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nettValue != 0) {
                    Intent intent = new Intent();
                    intent.putExtra(I_KEY_NETT_VALUE, nettValue);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    UIUtils.showToastMessage(TempCatchBtaBluetoothActivity.this, "Not validate value.");
                }
            }
        });
    }

    public void populateData(String data) {
        if (data.contains(PREFIX_GROSS)) {
            data = data.replace(PREFIX_GROSS, "");
            data = data.replace("kg", "");
            tvGross.setText(data.trim());
        } else if (data.contains(PREFIX_TARE)) {
            data = data.replace(PREFIX_TARE, "");
            data = data.replace("kg", "");
            tvTare.setText(data.trim());
        } else if (data.contains(PREFIX_NETT)) {
            data = data.replace(PREFIX_NETT, "");
            data = data.replace("kg", "");
            data = data.trim();
            try {
                nettValue = Double.parseDouble(data);
            } catch (NumberFormatException e) {
                nettValue = 0;
            }
            tvNett.setText(data.trim());
        }
    }
}
