package com.mosis.paw;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LeaderBoardPagerAdapter extends FragmentStatePagerAdapter {
    int tabsNumber;

    public LeaderBoardPagerAdapter(FragmentManager fm, int tabsNumber) {
        super(fm);
        this.tabsNumber = tabsNumber;
    }

    @Override
    public Fragment getItem(int position) {

        LeaderBoardTabFragment fragment = new LeaderBoardTabFragment();
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                // BY POINTS
                bundle.putString("SORTBY", "points");
                fragment.setArguments(bundle);
                break;
            case 1:
                // BY POINTS
                bundle.putString("SORTBY", "helps");
                fragment.setArguments(bundle);
                break;
            case 2:
                // BY POINTS
                bundle.putString("SORTBY", "friends");
                fragment.setArguments(bundle);
                break;
            default:
                fragment = null;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
