package my.com.engpeng.engpeng.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.bluetoothPrinter.BluetoothConnection;

public class BluetoothSelectionAdapter extends RecyclerView.Adapter<BluetoothSelectionAdapter.BluetoothDeviceViewHolder> {

    private Context context;
    private Set<BluetoothDevice> btDevices;
    private BluetoothSelectionListener bsListener;

    public BluetoothSelectionAdapter(Context context, Set<BluetoothDevice> btDevices, BluetoothSelectionListener bsListener) {
        this.context = context;
        this.btDevices = btDevices;
        this.bsListener = bsListener;
    }

    public interface BluetoothSelectionListener{
        void afterBluetoothSelect(String name, String address);
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

                List<String> data = new ArrayList<>();
                data.add(device.getName());
                data.add(device.getAddress());
                holder.itemView.setTag(data);
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

    class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;

        private BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.li_bluetooth_device_tv_name);
            tvAddress = itemView.findViewById(R.id.li_bluetooth_device_tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> data = ((List<String>) view.getTag());
                    String name = data.get(0).toString();
                    String address = data.get(1).toString();
                    bsListener.afterBluetoothSelect(name, address);
                }
            });
        }
    }
}
