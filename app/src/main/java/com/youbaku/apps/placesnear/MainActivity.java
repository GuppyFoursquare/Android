//
//  MainActivity
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.category.CategoryDownloaded;
import com.youbaku.apps.placesnear.category.CategoryList;
import com.youbaku.apps.placesnear.category.CategoryListFragment;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.AddPlace;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements CategoryDownloaded {
    public static Activity tt;
    private CategoryList categories;
    private enum onScreen{list,addPlace};
    private static onScreen screen=onScreen.list;
    private MenuItem toAdd;
    private MenuItem addDone;
    private AddPlace add;
    private boolean saving=false;

    public static boolean internetConnection=true;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        tt=this;
        ActionBar act=((ActionBar)getSupportActionBar());
        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.DefaultActionBarColor)));
        act.setDisplayShowHomeEnabled(true);
        act.setLogo(R.drawable.app_logo);
        act.setTitle("");
        act.setDisplayUseLogoEnabled(true);
        act.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_USE_LOGO);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
        ProgressBar pro=new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro,params);

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

        getSupportFragmentManager().addOnBackStackChangedListener(stackChangedListener);
        ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.VISIBLE);
        categories=CategoryList.getCategoryList(this);


        if(Build.VERSION.SDK_INT>10){
            ProgressBar bar=(ProgressBar)findViewById(R.id.progress_bar_activity_main);
            SpinKitDrawable1 spin=new SpinKitDrawable1(this);
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }

        Toast.makeText( getApplicationContext() , "Guppy - - Main Activity" , Toast.LENGTH_LONG).show();

    }

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
        toAdd=menu.getItem(0);
        toAdd.setVisible(false);
        addDone=menu.getItem(1);
        return true;
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
        addDone.setVisible(!visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(categories==null)
            return true;
        int id = item.getItemId();

        switch (id){
            case R.id.add_place_main_menu:
                add=new AddPlace();
                getSupportFragmentManager().beginTransaction().addToBackStack("AddPlace").replace(R.id.main_activity_main,add).commit();
                changeOnScreen(onScreen.addPlace);
                return true;

            case R.id.done_main_menu:
                if(!saving) {
                    saving=add.savePlace();
                }
                return true;
            case android.R.id.home:
                if(add!=null && !add.editable)
                    return true;
                getSupportFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(add!=null && !add.editable)
            return;
        super.onBackPressed();
    }




    ///BURASI INCELENMESI LAZIM
    @Override
    public void categoryDownloaded(ArrayList<Category> list) {
        ((RelativeLayout)findViewById(R.id.main_activity_main)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));
        ((ProgressBar)findViewById(R.id.progress_bar_activity_main)).setVisibility(View.GONE);
        CategoryListFragment fra=new CategoryListFragment();
        fra.setList(list);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_main, fra).commit();
    }

    @Override
    public void categoryDownloadError() {
        AlertDialog.Builder bu=new AlertDialog.Builder(this);
        final CategoryDownloaded aa=this;
        bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
        bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categories=CategoryList.getCategoryList(aa);
            }
        });
        bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel),null);
        bu.show();
    }




    FragmentManager.OnBackStackChangedListener stackChangedListener=new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if(screen==onScreen.addPlace){
                screen=onScreen.list;
                changeOnScreen(onScreen.list);
                saving=false;
            }else {
                changeOnScreen(onScreen.addPlace);
                screen=onScreen.addPlace;
            }
        }
    };

    private void changeOnScreen(onScreen s){
        ActionBar act=getSupportActionBar();
        if(s==onScreen.list){
            toAdd.setVisible(true);
            addDone.setVisible(false);
            ((RelativeLayout)findViewById(R.id.main_activity_main)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));
            act.setDisplayHomeAsUpEnabled(false);
            act.setDisplayUseLogoEnabled(true);
            act.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_USE_LOGO);
            act.setTitle("");
            act.setSubtitle("");
        }else if(s==onScreen.addPlace){
            toAdd.setVisible(false);
            addDone.setVisible(true);
            ((RelativeLayout)findViewById(R.id.main_activity_main)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
            act.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_TITLE);
            act.setDisplayHomeAsUpEnabled(true);
            act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
            act.setDisplayUseLogoEnabled(false);
            act.setTitle(getResources().getString(R.string.addplacetitlelabel));
            act.setSubtitle(getResources().getString(R.string.addplacesubtitlelabel));
        }
    }
}
