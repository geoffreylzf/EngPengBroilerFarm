package my.com.engpeng.engpeng.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.TempFeedDischargeHeadActivity;
import my.com.engpeng.engpeng.TempFeedInHeadActivity;
import my.com.engpeng.engpeng.TempFeedReceiveHeadActivity;
import my.com.engpeng.engpeng.TempFeedTransferHeadActivity;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.sCompanyId;
import static my.com.engpeng.engpeng.Global.sLocationId;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabFeedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final AlertDialog alertDialog = UIUtils.getMessageDialog(getActivity(), "Error", "Please select company and location.");
        View view = inflater.inflate(R.layout.fragment_main_tab_feed, container, false);

        Button btnFeedIn = view.findViewById(R.id.main_f_btn_feed_in);
        Button btnFeedOut = view.findViewById(R.id.main_f_btn_feed_transfer);
        Button btnFeedDischarge = view.findViewById(R.id.main_f_btn_feed_discharge);
        Button btnFeedReceive = view.findViewById(R.id.main_f_btn_feed_receive);

        btnFeedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent tempFeedInHeadIntent = new Intent(getActivity(), TempFeedInHeadActivity.class);
                    startActivity(tempFeedInHeadIntent);
                } else {
                    alertDialog.show();
                }
            }
        });

        btnFeedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent tempFeedTransferHeadIntent = new Intent(getActivity(), TempFeedTransferHeadActivity.class);
                    startActivity(tempFeedTransferHeadIntent);
                } else {
                    alertDialog.show();
                }
            }
        });

        btnFeedDischarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent intent = new Intent(getActivity(), TempFeedDischargeHeadActivity.class);
                    startActivity(intent);
                } else {
                    alertDialog.show();
                }
            }
        });

        btnFeedReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCompanyId != 0 && sLocationId != 0) {
                    Intent intent = new Intent(getActivity(), TempFeedReceiveHeadActivity.class);
                    startActivity(intent);
                } else {
                    alertDialog.show();
                }
            }
        });

        return view;
    }

}
