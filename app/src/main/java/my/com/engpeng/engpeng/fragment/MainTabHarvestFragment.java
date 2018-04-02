package my.com.engpeng.engpeng.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.com.engpeng.engpeng.BluetoothActivity;
import my.com.engpeng.engpeng.HouseListActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.TempCatchBTAHeadActivity;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.*;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabHarvestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final AlertDialog alertDialog = UIUtils.getMessageDialog(getActivity(), "Error", "Please select company and location.");
        View view = inflater.inflate(R.layout.fragment_main_tab_harvest, container, false);

        Button btnCatchBTA = view.findViewById(R.id.main_f_btn_catch_bta);
        btnCatchBTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent catchBTAIntent = new Intent(getActivity(), TempCatchBTAHeadActivity.class);
                    startActivity(catchBTAIntent);
                } else {
                    alertDialog.show();
                }
            }
        });

        Button btnDung = view.findViewById(R.id.main_f_btn_dung);
        btnDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        return view;
    }
}
