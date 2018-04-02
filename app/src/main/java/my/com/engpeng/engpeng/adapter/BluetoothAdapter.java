package my.com.engpeng.engpeng.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import my.com.engpeng.engpeng.Global;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.bluetooth.BluetoothConnection;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 28/2/2018.
 */

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.BluetoothDeviceViewHolder> implements BluetoothConnection.BluetoothConnectionListener{

    private Context context;
    private Set<BluetoothDevice> btDevices;
    private String printText;
    private SQLiteDatabase db;
    private String module;
    private Long id;

    public BluetoothAdapter(Context context, Set<BluetoothDevice> btDevices, String printText, SQLiteDatabase db, String module, Long id) {
        this.context = context;
        this.btDevices = btDevices;
        this.printText = printText;
        this.db = db;
        this.module = module;
        this.id = id;
    }

    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_bluetooh_device, parent, false);
        return new BluetoothDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothDeviceViewHolder holder, int position) {
        int order = 0;
        for (BluetoothDevice device : btDevices) {
            if (order == position) {
                holder.tvName.setText(device.getName());
                holder.tvAddress.setText(device.getAddress());

                holder.itemView.setTag(device.getAddress());
            }
            order++;
        }
    }

    @Override
    public int getItemCount() {
        return btDevices.size();
    }

    public void swapCursor(Set<BluetoothDevice> newDevices) {
        btDevices = newDevices;
        if (newDevices != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void afterPrintDone() {
        if (module.equals(Global.MODULE_CATCH_BTA)) {
            CatchBTAController.increasePrintCount(db, id);


        }
    }

    class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;
        LinearLayout ll;

        private BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_weight_history_ll);
            tvName = itemView.findViewById(R.id.li_bluetooth_device_tv_name);
            tvAddress = itemView.findViewById(R.id.li_bluetooth_device_tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String address = (String) view.getTag();
                    BluetoothConnection conn = new BluetoothConnection();
                    conn.initPrint(context, address, printText, BluetoothAdapter.this);

                    Cursor cursor = CatchBTAController.getById(db, id);
                    cursor.moveToFirst();
                    Log.i("printcount", cursor.getInt(cursor.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_PRINT_COUNT))+"");
                }
            });
        }
    }
}
