package my.com.engpeng.engpeng.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.model.Bluetooth;

public class BluetoothDialogFragment extends DialogFragment {

    public static final String tag = "BLUETOOTH_DIALOG_FRAGMENT";

    public static BluetoothDialogFragment show(FragmentManager fm, List<String> nameList, List<String> addressList, Listener listener) {
        BluetoothDialogFragment bdf = new BluetoothDialogFragment();
        bdf.nameList = nameList;
        bdf.addressList = addressList;
        bdf.listener = listener;
        bdf.show(fm, tag);
        return bdf;
    }

    public interface Listener {
        void onSelect(Bluetooth bluetooth);
    }

    private List<String> nameList;
    private List<String> addressList;
    private Listener listener;

    private RecyclerView rv;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(800, 800);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_bluetooth, container, false);
        rv = view.findViewById(R.id.rv);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new BluetoothDialogAdapter(nameList, addressList, new BluetoothDialogAdapter.Listener() {
            @Override
            public void onClicked(Bluetooth bluetooth) {
                listener.onSelect(bluetooth);
                dismiss();
            }
        }));

    }
}
