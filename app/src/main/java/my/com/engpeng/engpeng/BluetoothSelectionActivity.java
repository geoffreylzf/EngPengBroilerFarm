package my.com.engpeng.engpeng;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import java.util.Set;

import my.com.engpeng.engpeng.adapter.BluetoothAdapter;
import my.com.engpeng.engpeng.adapter.BluetoothSelectionAdapter;
import my.com.engpeng.engpeng.bluetoothPrinter.BluetoothConnection;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_BLUETOOTH_ADDRESS;
import static my.com.engpeng.engpeng.Global.I_KEY_BLUETOOTH_NAME;

public class BluetoothSelectionActivity extends AppCompatActivity {

    private BluetoothSelectionAdapter adapter;
    private boolean isBtPaired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = 800;
        params.width = 550;

        this.getWindow().setAttributes(params);

        setupRecycleView();

        setTitle("Tap a bluetooth printer to pair");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isBtPaired){
            refreshRecycleView();
        }
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.bluetooth_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Set<BluetoothDevice> btDevices = BluetoothConnection.getPairedBluetoothDevicesList(this);

        try{
            btDevices.toString();
            isBtPaired = true;
        }catch (NullPointerException e){
            isBtPaired = false;
            UIUtils.getMessageDialog(this, "No Bluetooth Printer Paired", "Please pair bluetooth printer before print.").show();
        }

        if(isBtPaired){
            adapter = new BluetoothSelectionAdapter(this, btDevices, new BluetoothSelectionAdapter.BluetoothSelectionListener() {
                @Override
                public void afterBluetoothSelect(String name, String address) {
                    Intent intent = new Intent();
                    intent.putExtra(I_KEY_BLUETOOTH_NAME, name);
                    intent.putExtra(I_KEY_BLUETOOTH_ADDRESS, address);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            rv.setAdapter(adapter);
        }
    }

    private void refreshRecycleView(){
        adapter.swapCursor(BluetoothConnection.getPairedBluetoothDevicesList(this));
    }

}
