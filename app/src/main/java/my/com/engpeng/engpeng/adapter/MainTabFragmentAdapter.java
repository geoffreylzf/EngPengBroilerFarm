package my.com.engpeng.engpeng.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import my.com.engpeng.engpeng.fragment.MainTabFeedFragment;
import my.com.engpeng.engpeng.fragment.MainTabFarmDataFragment;
import my.com.engpeng.engpeng.fragment.MainTabHarvestFragment;
import my.com.engpeng.engpeng.fragment.MainTabManagementFragment;

/**
 * Created by Admin on 24/1/2018.
 */

public class MainTabFragmentAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainTabFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MainTabFarmDataFragment tabFarmData = new MainTabFarmDataFragment();
                return tabFarmData;
            case 1:
                MainTabHarvestFragment tabHarvest = new MainTabHarvestFragment();
                return tabHarvest;
            case 2:
                MainTabFeedFragment tabFeed = new MainTabFeedFragment();
                return tabFeed;
            case 3:
                MainTabManagementFragment tabManagement = new MainTabManagementFragment();
                return tabManagement;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
