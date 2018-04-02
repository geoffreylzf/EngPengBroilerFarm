package my.com.engpeng.engpeng.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.com.engpeng.engpeng.R;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabGRNFragment extends Fragment {

    protected View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_tab_grn, container, false);

        this.mView = view;
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mView = null;
    }
}
