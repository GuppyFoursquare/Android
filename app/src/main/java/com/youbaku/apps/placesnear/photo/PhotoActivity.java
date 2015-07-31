/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.photo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;

import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.place.Place;

import java.util.ArrayList;

public class PhotoActivity extends ActionBarActivity {
    public static final String START_POSTİON="startPosition";

    private int startPosition=0;
    private ViewPager pager;
    private ArrayList<Photo> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo);

        pager=(ViewPager)findViewById(R.id.pager_activity_photo);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }

        arr= Place.FOR_DETAIL.getPhotos();
        SlideAdapter adap=new SlideAdapter(this, arr);
        pager.setAdapter(adap);

        startPosition=getIntent().getIntExtra(START_POSTİON,0);
        pager.setCurrentItem(startPosition);

        //((App)getApplication()).track(App.ANALYSIS_PHOTOS);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
