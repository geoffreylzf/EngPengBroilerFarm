package my.com.engpeng.engpeng.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.com.engpeng.engpeng.HouseListActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.MODULE_MORTALITY;
import static my.com.engpeng.engpeng.Global.MODULE_WEIGHT;
import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabFarmDataFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final AlertDialog alertDialog = UIUtils.getMessageDialog(getActivity(), "Error", "Please select company and location.");
        View view = inflater.inflate(R.layout.fragment_main_tab_farm_data, container, false);
        Button btnMortality = view.findViewById(R.id.main_f_btn_mortality);
        Button btnWeight = view.findViewById(R.id.main_f_btn_weight);

        btnMortality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent mortalityIntent = new Intent(getActivity(), HouseListActivity.class);
                    mortalityIntent.putExtra(I_KEY_MODULE, MODULE_MORTALITY);
                    startActivity(mortalityIntent);
                } else {
                    alertDialog.show();
                }
            }
        });
        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent weightIntent = new Intent(getActivity(), HouseListActivity.class);
                    weightIntent.putExtra(I_KEY_MODULE, MODULE_WEIGHT);
                    startActivity(weightIntent);
                } else {
                    alertDialog.show();
                }
            }
        });

        return view;
    }
}
