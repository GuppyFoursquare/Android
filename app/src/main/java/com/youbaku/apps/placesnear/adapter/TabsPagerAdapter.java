/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 *
 * YouBaku Android App. - TabsPagerAdapter.java
 * This file is downloading category from server.
 * This adapter is for viewpager
 */

package com.youbaku.apps.placesnear.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.youbaku.apps.placesnear.LoginFragment;
import com.youbaku.apps.placesnear.NearMe;
import com.youbaku.apps.placesnear.SearchFragment;
import com.youbaku.apps.placesnear.place.PopularPlaceFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm=fm;

    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // ListCategory fragment
                return new SearchFragment();
            case 1:
                // Search fragment
                return new NearMe();
            case 2:
                // Nearme fragment
                return new PopularPlaceFragment();
            case 3:
                // Popular fragment
                return new PopularPlaceFragment();
            case 4:
                // Login fragment
                return new LoginFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}