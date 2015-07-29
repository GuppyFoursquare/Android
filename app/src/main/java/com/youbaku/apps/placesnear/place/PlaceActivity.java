/*
 * YouBaku Project
 *
 * Copyright (C) 2015 CasbianSoft. All rights reserved.
 *
 * Modified by Guppy org.
 *
 * TODO :: This Activty is PlaceActivty. Places with related category are listed on PlaceActivty.
 */


package com.youbaku.apps.placesnear.place;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.SpinKitDrawable1;
import com.youbaku.apps.placesnear.adapter.Adapter;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.location.MyLocationSet;
import com.youbaku.apps.placesnear.place.comment.AllCommentsDownloaded;
import com.youbaku.apps.placesnear.place.deal.AllDealsDownloaded;
import com.youbaku.apps.placesnear.place.filter.FilterFragment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PlaceActivity extends ActionBarActivity implements AllCommentsDownloaded, AllDealsDownloaded, MyLocationSet {
    public static final String COLOR = "color";
    public static final String TITLE = "title";

    @Override
    public void locationSet() {}

    private enum Screen {list, filter, favorite}

    private Screen screen = Screen.list;
    private String color = "";
    private String title = "";
    private ArrayList<Place> list = new ArrayList<>();
    private boolean onScreen = true;//if this activity still on screen
    private boolean firstFilter = false;
    private boolean placesDownload = false;
    private boolean commentsDownload = false;
    private boolean dealsDownload = false;
    private MenuItem goFilter;
    private MenuItem doFilter;
    private ProgressBar bar;

    private PlaceListFragment listFragment;
    private FilterFragment filterFragment;
    private FilterFragment fi;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_place);
        PlaceFilter filter = PlaceFilter.getInstance();

        Intent in = getIntent();
        //color=in.getStringExtra(COLOR);
        title = in.getStringExtra(TITLE);
        filter.category = in.getStringExtra(Place.ID);

        ActionBar act = ((ActionBar) getSupportActionBar());


        //Burası Category tıklandıktan sora Action Barın rengini değişir
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(App.DefaultActionBarColor));
        act.setBackgroundDrawable(colorDrawable);
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        act.setDisplayShowCustomEnabled(true);
        act.setTitle("Places within 10km");
        act.setSubtitle("YouBaku");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 0x05;
        ProgressBar pro = new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro, params);

        bar = (ProgressBar) findViewById(R.id.progressBar);


        if (!MyLocation.checkLocationServices(getApplicationContext())) {
            setContentView(R.layout.need_location_service);
            App.sendErrorToServer(PlaceActivity.this, getClass().getName(), "onCreate", "CheckLocationServices Problem");
            return;
        }


        ((RelativeLayout) findViewById(R.id.main_activity_place)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));
        if (Build.VERSION.SDK_INT > 10) {
            bar = (ProgressBar) findViewById(R.id.progressBar);
            SpinKitDrawable1 spin = new SpinKitDrawable1(this);

            //Burası loader'in rengini değişir
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }


        // --GUPPY COMMENT UPDATE--
        listFragment = new PlaceListFragment();
        listFragment.setAdapter(new PlaceAdapter(this, Place.placesArrayListSearch, Color.BLACK));
        listFragment.setColor(App.DefaultBackgroundColor);
        listFragment.setOnItemClickListener(listSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();
        Place.fetchSearchPlaces(this, listFragment.getAdapter());
        listFragment.getAdapter().notifyDataSetChanged();
        ProgressBar br=(ProgressBar) findViewById(R.id.progressBar);
        br.setVisibility(View.INVISIBLE);


    }


    // --GUPPY COMMENT UPDATE--
    AdapterView.OnItemClickListener listSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            /**
             * Before start Activity we have to fetch place info from server then
             * start activity. Otherwise place info may not be includes all information.
             *
             * Place info fetched at PlaceDetailActivity > PlaceDetailFragment
             */
            Place.FOR_DETAIL = Place.placesArrayListSearch.get(position);
            Place.ID = Place.placesArrayListSearch.get(position).getId();
            Place.EMAIL = Place.placesArrayListSearch.get(position).getEmail();
            Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
            in.putExtra("title", Place.placesArrayListSearch.get(position).getName());
            startActivity(in);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place, menu);
        goFilter = menu.getItem(1);
        doFilter = menu.getItem(0);
        if (screen == Screen.favorite)
            goFilter.setVisible(false);
        return true;
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.go_filter_menu:

                if (Place.placesArrayListSearch!=null && Place.placesArrayListSearch.size()>0 ){
                    if (filterFragment == null)
                        filterFragment = new FilterFragment();

                    filterFragment.setColor(Color.parseColor(App.DefaultBackgroundColor));
                    getSupportFragmentManager().beginTransaction().addToBackStack("filter").replace(R.id.main_activity_place, filterFragment).commit();
                    setScreen(Screen.filter);
                    return true;

                }else{
                    Toast.makeText(this , "PlaceActivity->placesArrayListSearch is null", Toast.LENGTH_LONG).show();
                    return true;
                }

            case android.R.id.home:
                if (firstFilter) {
                    finish();
                    return true;
                }
                if (screen == Screen.filter) {
                    getSupportFragmentManager().popBackStack();
                    goBack();
                    return true;
                }
                return false;

            case R.id.do_filter:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(filterFragment.getWindowToken(), 0);
                getSupportFragmentManager().popBackStack();

                //-0- ACTIONBAR VIEW SETTINGs
                doFilter.setVisible(false);
                setSupportProgressBarIndeterminateVisibility(true);

                //-1- GET FILTER PROPERTIES
                final PlaceFilter filter = PlaceFilter.getInstance();

                //-2-   APPLY FILTER TO SEARCH PLACES
                //-2.1- sort list according to filter
                Collections.sort(Place.placesArrayListSearch, new Comparator<Place>() {
                    @Override
                    public int compare(Place lhs, Place rhs) {

                        if (filter.sorting == PlaceFilter.SortBy.distance) {
                            if (lhs.getDistance()[0] < rhs.getDistance()[0]) {
                                return -1;
                            } else if (lhs.getDistance()[0] > rhs.getDistance()[0]) {
                                return 1;
                            } else {
                                return 0;
                            }

                        } else if (filter.sorting == PlaceFilter.SortBy.rating) {
                            if (lhs.getRating() < rhs.getRating())
                                return 1;
                            else if (lhs.getRating() > rhs.getRating())
                                return -1;
                            else
                                return 0;

                        } else {

                            if (lhs.likes < rhs.likes)
                                return 1;
                            else if (lhs.likes > rhs.likes)
                                return -1;
                            else
                                return 0;
                        }
                    }
                });

                //-2.2- fetch places
                Place.placesArrayListFilter.clear();
                for(Place searchPlace : Place.placesArrayListSearch){
                    boolean filterKeyword       = filter.keyword.length() != 0 ? searchPlace.getName().toLowerCase().contains(filter.keyword.toLowerCase()) : true;
                    boolean filterDistanceKM    = filter.getDistance(PlaceFilter.DistanceSystem.km) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.km) > searchPlace.getDistance()[0] / 1000 : true;
                    boolean filterDistanceML    = filter.getDistance(PlaceFilter.DistanceSystem.ml) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.ml) > searchPlace.getDistance()[0] / 1000 * 0.6214 : true;
                    boolean filterIsOpen        = filter.open ? searchPlace.isOpen() : true;
                    boolean isPlaceOK = filterKeyword && filterDistanceKM && filterDistanceML && filterIsOpen ;

                    if(isPlaceOK){
                        Place.placesArrayListFilter.add(searchPlace);
                    }

                }

                //-3- UPDATE ACTIONBAR
                setSupportProgressBarIndeterminateVisibility(false);

                //-4- SET VIEW
                // --GUPPY COMMENT IMPORTANT--
                // -- If filter return size == 0 then replace with different fragment
                listFragment = new PlaceListFragment();
                listFragment.setList(Place.placesArrayListFilter);
                listFragment.setColor(App.DefaultBackgroundColor);
                listFragment.setOnItemClickListener(listSelected);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();
                setScreen(Screen.list);

                ActionBar act = (getSupportActionBar());
                act.setSubtitle("Filter results...");

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (firstFilter)
            finish();
        if (screen == Screen.filter) {
            getSupportFragmentManager().popBackStack();
            goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        onScreen = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        onScreen = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void goBack() {
        switch (screen) {
            case filter:
                setScreen(Screen.list);
                break;
        }
    }

    private void setScreen(Screen sc) {
        if (goFilter != null) {
            goFilter.setVisible(false);
            doFilter.setVisible(false);
            switch (sc) {
                case list:
                    goFilter.setVisible(true);
                    break;
                case filter:
                    doFilter.setVisible(true);
                    break;
            }
        }
        screen = sc;
    }


    @Override
    public void CommentsDownloaded() {
        commentsDownload = true;
    }

    @Override
    public void dealsDownloaded() {
        dealsDownload = true;
    }

}
