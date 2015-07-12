//
//  MainActivity
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.youbaku.apps.placesnear.adapter.TabsPagerAdapter;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.youbaku.apps.placesnear.utils.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    public static Activity tt;
    public static MenuItem doLogin;
    public static boolean internetConnection=true;



    // Tab titles
    private String[] tabs = { "Home","Search", "Nearme", "Popular" , "Login"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checking internet connection here
        internetConnection=App.checkInternetConnection(this);
        if(!internetConnection){
            setContentView(R.layout.need_network);
            return;
        }else {
            setContentView(R.layout.activity_main);
        }

        if(MyLocation.checkLocationServices(getApplicationContext())){
            setContentView(R.layout.need_location_service);
            return;
        }

        ((RelativeLayout)findViewById(R.id.main_activity_main)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));
        ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT>10){
            ProgressBar bar=(ProgressBar)findViewById(R.id.progress_bar_activity_main);
            SpinKitDrawable1 spin=new SpinKitDrawable1(this);
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }

        // Initilization ActionBar
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

        //initialize viewpager
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        if(mAdapter.getCount()!=0){

            //make loader visible false
            ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.INVISIBLE);
        }



        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
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
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        doLogin = menu.getItem(0);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_main_menu:

                if(App.userapikey==null){
                    login();
                }
                else
                {

                    Toast.makeText(getApplicationContext(),"Go To Profile",Toast.LENGTH_LONG).show();

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


        private void login(){

            //Toast.makeText(getApplicationContext(), "Login is clicked", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View alertView = inflater.inflate(R.layout.dialog_login_layout, null);
            alertDialog.setView(alertView);

            /* When positive  is clicked */
            alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel(); // Your custom code

                    String loginUrl = App.SitePath + "api/auth.php?op=login";

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", ((EditText)alertView.findViewById(R.id.username)).getText().toString());
                    map.put("pass", ((EditText)alertView.findViewById(R.id.password)).getText().toString());

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                    JSONObject responseContent = response.getJSONObject("content");
                                    App.username = responseContent.getString("usr_username");
                                    App.userapikey = responseContent.getString("usr_apikey");

                                    doLogin.setIcon(R.drawable.ic_profilelogo);
                                    Toast.makeText( MainActivity.this ,App.username + " - " + App.userapikey , Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(MainActivity.this, response.getString("status") , Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                                bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                                bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
                                bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                bu.show();
                                e.printStackTrace();
                                return;
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

                    // Add the request to the queue
                    VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);

                }
            });

            /* When negative  button is clicked*/
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Your custom code
                }
            });

            alertDialog.show();
        }


    }


