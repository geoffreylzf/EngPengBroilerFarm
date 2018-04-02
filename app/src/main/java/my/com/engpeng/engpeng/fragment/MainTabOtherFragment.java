package my.com.engpeng.engpeng.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.com.engpeng.engpeng.CompanyListActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.LocationInfoActivity;
import my.com.engpeng.engpeng.UploadActivity;
import my.com.engpeng.engpeng.utilities.UIUtils;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabOtherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final AlertDialog alertDialog = UIUtils.getMessageDialog(getActivity(), "Error", "Please select company and location.");
        View view = inflater.inflate(R.layout.fragment_main_tab_other, container, false);
        Button btnUpload = view.findViewById(R.id.main_f_btn_upload);
        Button btnLocation = view.findViewById(R.id.main_f_btn_location);
        Button btnSync = view.findViewById(R.id.main_f_btn_sync);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadActivity.class));
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CompanyListActivity.class));
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LocationInfoActivity.class));
            }
        });

        return view;
    }
}
