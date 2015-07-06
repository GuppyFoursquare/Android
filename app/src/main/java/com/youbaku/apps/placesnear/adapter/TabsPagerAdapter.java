package com.youbaku.apps.placesnear.adapter;

/*****************************************************************************************
 * Copyright (c) 2015 Guppy org.
 * YouBaku Android App. - TabsPagerAdapter.java
 * This file is downloading category from server.
 * This adapter is for viewpager
 *
 ****************************************************************************************/

/**
 * Created by orxan on 01.07.2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.youbaku.apps.placesnear.LoginFragment;
import com.youbaku.apps.placesnear.NearMe;
import com.youbaku.apps.placesnear.category.CategoryListFragment;
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

                return new CategoryListFragment();
            case 1:
                // Nearme fragment
                return new NearMe();
            case 2:
                // Popular fragment
                return new PopularPlaceFragment();
            case 3:
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