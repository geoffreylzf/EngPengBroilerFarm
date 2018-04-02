package my.com.engpeng.engpeng;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;

import java.util.Set;

import my.com.engpeng.engpeng.adapter.BluetoothAdapter;
import my.com.engpeng.engpeng.bluetooth.BluetoothConnection;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter adapter;
    private BluetoothConnection conn;
    private SQLiteDatabase db;
    private String printText;
    private boolean is_bt_paired = false;
    private String module;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = 800;
        params.width = 550;

        this.getWindow().setAttributes(params);

        setupIntent();

        conn = new BluetoothConnection();

        setupRecycleView();

        setTitle("Tap a bluetooth printer to print");
    }

    private void setupIntent(){
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_PRINT_TEXT)) {
            printText = intentStart.getStringExtra(I_KEY_PRINT_TEXT);
        }
        if (intentStart.hasExtra(I_KEY_MODULE)) {
            module = intentStart.getStringExtra(I_KEY_MODULE);
        }
        if (intentStart.hasExtra(I_KEY_ID)) {
            id = intentStart.getLongExtra(I_KEY_ID, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(is_bt_paired){
            refreshRecycleView();
        }
    }

    public void setupRecycleView() {
        RecyclerView rv = this.findViewById(R.id.bluetooth_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Set<BluetoothDevice> btDevices = conn.getPairedBluetoothDevicesList(this);

        try{
            btDevices.toString();
            is_bt_paired = true;
        }catch (NullPointerException e){
            is_bt_paired = false;
            UIUtils.getMessageDialog(this, "No Bluetooth Printer Paired", "Please pair bluetooth printer before print.").show();
        }

        if(is_bt_paired){
            adapter = new BluetoothAdapter(this, btDevices, printText, db, module, id);
            rv.setAdapter(adapter);
        }
    }

    private void refreshRecycleView(){
        adapter.swapCursor(conn.getPairedBluetoothDevicesList(this));
    }
}
