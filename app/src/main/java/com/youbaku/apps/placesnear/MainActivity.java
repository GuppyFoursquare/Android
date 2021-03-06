/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.youbaku.apps.placesnear.adapter.TabsPagerAdapter;
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.youbaku.apps.placesnear.utils.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    private static ViewPager viewPager;
    public static TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    public static MenuItem doLogin;
    public static boolean internetConnection=true;
    public Activity activity;

    private Menu menu;


    // Tab titles
    private String[] tabs = { "Home", "Nearme", "Most Popular" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = this;

        App.setTokenAndAPIKey(this);


        // ********** ********** ********** ********** **********
    // Checking internet connection here
    // ********** ********** ********** ********** **********
        internetConnection=App.checkInternetConnection(this);
        if(!internetConnection){
            setContentView(R.layout.need_network);
            return;
        }else {

            // Progress Bar will be added
            setContentView(R.layout.activity_main);
        }
        

        ((RelativeLayout)findViewById(R.id.main_activity_main)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));
        ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT>10){
            ProgressBar bar=(ProgressBar)findViewById(R.id.progress_bar_activity_main);
            SpinKitDrawable1 spin=new SpinKitDrawable1(this);
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }


    // ********** ********** ********** ********** **********
    // Initilization ActionBar
    // ********** ********** ********** ********** **********
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.DefaultActionBarColor)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.app_logo);
        actionBar.setTitle("");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
        ProgressBar pro=new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        actionBar.setCustomView(pro, params);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // ********** ********** ********** ********** **********
    // Initialize viewpager
    // ********** ********** ********** ********** **********
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        if(mAdapter.getCount()!=0){
            //make loader visible false
            ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.INVISIBLE);
        }


    // ********** ********** ********** ********** **********
    // Settings tabs
    // ********** ********** ********** ********** **********
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }


    // ********** ********** ********** ********** **********
    // Adding viewPager Listener
    // ********** ********** ********** ********** **********
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }



    @Override
    public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }



    //Main Activty overide methods
    @Override
    protected void onStart() {
        super.onStart();
        MyLocation my=MyLocation.getMyLocation(this);
        my.callHard();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        PlaceFilter.resetFilter();

        if(doLogin!=null && App.isAuth(activity)){
            doLogin.setIcon(R.drawable.ic_profilelogo);
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, this.menu);

        doLogin = menu.getItem(0);
        if(App.isAuth(activity)){
            doLogin.setIcon(R.drawable.ic_profilelogo);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_main_menu:

                if(!App.isAuth(activity)){
                    User.userLogin(MainActivity.this);
                }
                else
                {
                    Map<String, String> map = new HashMap<String,String>();
                    map.put("op", "info");

                    User.userInfo(
                            App.SitePath + "api/auth.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey(),
                            map,
                            activity
                    );
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}


