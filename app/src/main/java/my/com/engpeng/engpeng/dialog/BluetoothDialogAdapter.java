package my.com.engpeng.engpeng.dialog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.model.Bluetooth;

public class BluetoothDialogAdapter extends RecyclerView.Adapter<BluetoothDialogAdapter.BluetoothViewHolder> {

    private List<String> nameList, addressList;
    private Listener listener;

    interface Listener {
        void onClicked(Bluetooth bluetooth);
    }

    public BluetoothDialogAdapter(List<String> nameList, List<String> addressList, Listener listener) {
        this.nameList = nameList;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BluetoothViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_bluetooth, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder bluetoothViewHolder, int i) {
        final String name = nameList.get(i);
        final String address = addressList.get(i);

        bluetoothViewHolder.tvName.setText(name);
        bluetoothViewHolder.tvAddress.setText(address);
        bluetoothViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClicked(new Bluetooth(name, address));
            }
        });

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;

        private BluetoothViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.li_bt_name);
            tvAddress = itemView.findViewById(R.id.li_bt_address);
        }
    }
}
