//
//  PlaceActivity
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.SpinKitDrawable1;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.category.CategoryList;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.location.MyLocationSet;
import com.youbaku.apps.placesnear.photo.Photo;
import com.youbaku.apps.placesnear.place.comment.AllCommentsDownloaded;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.AllDealsDownloaded;
import com.youbaku.apps.placesnear.place.deal.Deal;
import com.youbaku.apps.placesnear.place.favorites.FavoritesCallback;
import com.youbaku.apps.placesnear.place.favorites.FavoritesManager;
import com.youbaku.apps.placesnear.place.filter.FilterFragment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PlaceActivity extends ActionBarActivity implements AllCommentsDownloaded
                                                                ,AllDealsDownloaded
                                                                ,MyLocationSet {
    public static final String COLOR="color";
    public static final String TITLE="title";
    private enum Screen{list,filter,favorite}
    private Screen screen=Screen.list;

    private String color="";
    private String title="";
    private ArrayList<Place> list=new ArrayList<Place>();
    private boolean onScreen=true;//if this activity still on screen
    private boolean firstFilter=false;
    private boolean placesDownload=false;
    private boolean commentsDownload=false;
    private boolean dealsDownload=false;
    private MenuItem goFilter;
    private MenuItem doFilter;
    private ProgressBar bar;

    private PlaceListFragment listFragment;
    private FilterFragment fi;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_place);
        PlaceFilter filter=PlaceFilter.getInstance();

        Intent in=getIntent();
        color=in.getStringExtra(COLOR);
        title=in.getStringExtra(TITLE);
        filter.category=in.getStringExtra(Place.ID);

        ActionBar act=((ActionBar)getSupportActionBar());

        //Burası Category tıklandıktan sora Action Barın rengini değişir
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(App.DefaultActionBarColor));
        act.setBackgroundDrawable(colorDrawable);
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        act.setDisplayShowCustomEnabled(true);
        act.setTitle(title);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
        ProgressBar pro=new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro,params);

        if(MyLocation.checkLocationServices(getApplicationContext())){
            setContentView(R.layout.need_location_service);
            return;
        }

        ((RelativeLayout)findViewById(R.id.main_activity_place)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));

        if(Build.VERSION.SDK_INT>10){
            bar=(ProgressBar)findViewById(R.id.progressBar);
            SpinKitDrawable1 spin=new SpinKitDrawable1(this);

            //Burası loader'in rengini değişir
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }

        if(!CategoryList.getCategory(Category.SELECTED_CATEGORY_ID).favourite) {
            placesDownload = false;
            commentsDownload = false;
            dealsDownload = false;
            Comment.setSubscriber(this);
            Deal.setSubscriber(this);
            refreshList();
        }else {
            refreshFavourite();
            screen=Screen.favorite;
        }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place, menu);
        goFilter=menu.getItem(1);
        doFilter=menu.getItem(0);
        if(screen==Screen.favorite)
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

        switch (id){
            case R.id.go_filter_menu:
                if(!dealsDownload || !commentsDownload || !placesDownload)
                    return true;
                if(fi==null)
                    fi=new FilterFragment();
                fi.setColor(Color.parseColor(color));
                getSupportFragmentManager().beginTransaction().addToBackStack("filter").replace(R.id.main_activity_place,fi).commit();
                setScreen(Screen.filter);
                return true;
            case android.R.id.home:
                if(firstFilter) {
                    finish();
                    return true;
                }
                if(screen==Screen.filter){
                    getSupportFragmentManager().popBackStack();
                    goBack();
                    return true;
                }
                return false;
            case R.id.do_filter:
                InputMethodManager man=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                man.hideSoftInputFromWindow(fi.getWindowToken(),0);
                getSupportFragmentManager().popBackStack();
                if(listFragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(listFragment).commit();
                if(bar!=null)
                bar.setVisibility(View.VISIBLE);
                refreshList();
                doFilter.setVisible(false);
                setSupportProgressBarIndeterminateVisibility(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(firstFilter)
            finish();
        if(screen==Screen.filter){
            getSupportFragmentManager().popBackStack();
            goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        onScreen=true;
        if (CategoryList.getCategory(Category.SELECTED_CATEGORY_ID).favourite
                && FavoritesManager.isChanged()
                && !MyLocation.checkLocationServices(getApplicationContext()))
            refreshFavourite();
        super.onStart();
    }

    @Override
    protected void onStop() {
        onScreen=false;
        super.onStop();
    }

    private void goBack(){
        switch (screen){
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
        screen=sc;
    }

    AdapterView.OnItemClickListener listSelected=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place.FOR_DETAIL=list.get(position);
            Intent in=new Intent(getApplicationContext(), PlaceDetailActivity.class);
            startActivity(in);
        }
    };

    private void refreshList(){
        if(!App.checkInternetConnection(this) && onScreen) {
            App.showInternetError(this);
            return;
        }
        MyLocation my=MyLocation.getMyLocation(getApplicationContext());
        if(!my.isSet()){
            my.subscriber=this;
            my.callLocation();
            return;
        }
        final PlaceFilter filter=PlaceFilter.getInstance();
        placesDownload=false;
        commentsDownload=false;
        dealsDownload=false;
        ParseQuery<ParseObject> query=ParseQuery.getQuery(App.PARSE_PLACES);
        query.whereEqualTo(Place.ISACTIVE,true);
        query.whereContains(Place.NAME, filter.keyword);
        query.whereEqualTo(Place.CATEGORY, filter.category);

        if(my!=null) {
            ParseGeoPoint geo = new ParseGeoPoint(my.latitude, my.longitude);
            if (filter.metrics == PlaceFilter.DistanceSystem.km) {
                query.whereWithinKilometers(Place.POSITION, geo, filter.getDistance(PlaceFilter.DistanceSystem.km));
            } else {
                query.whereWithinMiles(Place.POSITION, geo, filter.getDistance(PlaceFilter.DistanceSystem.ml));
            }
        }

        list=new ArrayList<Place>();
        query.setLimit(filter.limit);
        final Activity tt=this;
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(!onScreen)
                    return;
                if(e!=null){
                    AlertDialog.Builder bu=new AlertDialog.Builder(tt);
                    bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                    bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel),null);
                    bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshList();
                        }
                    });
                    bu.show();
                    e.printStackTrace();
                    return;
                }
                if(parseObjects.size()>0) {
                    System.out.println("places downloaded " + parseObjects.size());
                    firstFilter=false;
                    placesDownload = true;
                    Comment.setLIMIT(parseObjects.size());
                    Deal.setLIMIT(parseObjects.size());
                    Photo.setLIMIT(parseObjects.size());
                    for (int i = 0; i < parseObjects.size(); i++) {
                        ParseObject o = parseObjects.get(i);
                        final Place p = new Place();
                        p.name = o.getString(Place.NAME);
                        p.description = o.getString(Place.DESCRIPTION);
                        p.category = CategoryList.getCategory(o.getString(Place.CATEGORY)).title;
                        p.color = color;
                        p.id = o.getObjectId();
                        p.likes = Integer.parseInt(o.getString(Place.LIKES));
                        p.isActive = o.getBoolean(Place.ISACTIVE);
                        p.rating = Double.parseDouble(o.getString(Place.RATING));
                        p.open = o.getString(Place.OPENHOUR);
                        p.close = o.getString(Place.CLOSEHOUR);
                        p.web = o.getString(Place.WEBPAGE);
                        p.facebook = o.getString(Place.FACEBOOK);
                        p.twitter = o.getString(Place.TWITTER);
                        p.phone = o.getString(Place.PHONE);
                        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        p.liked=pref.getBoolean(p.id,false);
                        p.isFavourite=FavoritesManager.isFavorite(tt,p.id);
                        p.address=o.getString(Place.ADDRESS);

                        ParseGeoPoint geo = o.getParseGeoPoint(Place.POSITION);
                        p.setLocation(geo.getLatitude(), geo.getLongitude());
                        MyLocation my = MyLocation.getMyLocation(getApplicationContext());
                        Location.distanceBetween(my.latitude, my.longitude, p.getLatitude(), p.getLongitude(), p.distance);

                        /*
                        try {
                            List<Address> go = new Geocoder(getApplicationContext()).getFromLocation(p.getLatitude(), p.getLongitude(), 1);
                            if (!go.isEmpty()) {
                                p.address = go.get(0).getAddressLine(0) + " " + go.get(0).getAddressLine(1);
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }*/

                        ParseQuery<ParseObject> q = ParseQuery.getQuery(App.PARSE_COMMENTS);
                        q.whereEqualTo(Comment.PLACE, o.getObjectId());
                        q.whereEqualTo(Comment.ISACTIVE, true);
                        q.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                for (int i = 0; i < parseObjects.size(); i++) {
                                    Comment c = new Comment();
                                    ParseObject o = parseObjects.get(i);
                                    c.created = o.getCreatedAt();
                                    c.name = o.getString(Comment.NAME);
                                    c.text = o.getString(Comment.TEXT);
                                    c.email = o.getString(Comment.EMAIL);
                                    c.rating = o.getDouble(Comment.RATING);
                                    c.isActive = true;
                                    c.place = o.getString(Comment.PLACE);
                                    p.comments.add(c);
                                }
                                Comment.increaseDownloaded();
                            }
                        });


                        ParseQuery<ParseObject> qq = ParseQuery.getQuery(App.PARSE_DEALS);
                        qq.whereEqualTo(Deal.PLACE, o.getObjectId());
                        qq.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                if (e != null)
                                    e.printStackTrace();
                                for (int i = 0; i < parseObjects.size(); i++) {
                                    Deal d = new Deal();
                                    ParseObject o = parseObjects.get(i);
                                    d.title = o.getString(Deal.TITLE);
                                    d.description = o.getString(Deal.DESCRIPTION);
                                    d.url = o.getString(Deal.URL);
                                    d.place = o.getString(Deal.PLACE);
                                    ParseFile file = o.getParseFile(Deal.PHOTO);
                                    d.photo = file.getUrl();
                                    d.startDate = o.getDate(Deal.START_DATE);
                                    d.endDate = o.getDate(Deal.END_DATE);
                                    p.deals.add(d);
                                }
                                Deal.increaseDownloaded();
                            }
                        });

                        ParseQuery<ParseObject> qp = ParseQuery.getQuery(App.PARSE_PHOTOS);
                        qp.whereEqualTo(Photo.PLACE, o.getObjectId());
                        qp.whereEqualTo(Photo.ISACTIVE, true);
                        qp.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                if (e != null)
                                    e.printStackTrace();
                                for (int i = 0; i < parseObjects.size(); i++) {
                                    Photo pp = new Photo();
                                    ParseObject o = parseObjects.get(i);
                                    pp.isActive = true;
                                    pp.place = o.getString(Photo.PLACE);
                                    ParseFile f = o.getParseFile(Photo.PHOTO);
                                    pp.url = f.getUrl();
                                    p.photos.add(pp);
                                }
                                Photo.increaseDownloaded();
                            }
                        });
                        boolean pop = true;
                        if (filter.popular && p.rating < 6.0) {
                            pop = false;
                        }
                        boolean open = true;
                        if (filter.open) {
                            open = p.isOpen();
                        }
                        if (pop && open)
                            list.add(p);
                    }
                    checkDownloads();
                }else{
                    setSupportProgressBarIndeterminateVisibility(false);
                    AlertDialog.Builder bu=new AlertDialog.Builder(tt);
                    bu.setMessage(getResources().getString(R.string.novenuemessage));
                    bu.setNegativeButton(getResources().getString(R.string.alertcancelbuttonlabel),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    bu.setPositiveButton(getResources().getString(R.string.newfilterbuttonlabel),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(fi==null)
                                fi=new FilterFragment();
                            firstFilter=true;
                            fi.setColor(Color.parseColor(color));
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place,fi).commit();
                            setScreen(Screen.filter);
                        }
                    });
                    bu.show();
                }
            }
        });
    }

    private void  refreshFavourite(){
        if(!App.checkInternetConnection(this) && onScreen) {
            App.showInternetError(this);
            return;
        }
        MyLocation my=MyLocation.getMyLocation(getApplicationContext());
        if(!my.isSet()){
            my.subscriber=this;
            my.callLocation();
            return;
        }

        FavoritesManager.refresh(this,new FavoritesCallback() {
            @Override
            public void favoritesReady(ArrayList<Place> places) {
                setSupportProgressBarIndeterminateVisibility(false);
                listFragment = new PlaceListFragment();

                if(places!=null) {
                    list = places;
                    listFragment.setList(list);
                    listFragment.setOnItemClickListener(listSelected);
                }
                listFragment.setEmptyText(getResources().getString(R.string.nofavoritedvenuemessage));
                listFragment.setColor(color);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();
                setScreen(Screen.favorite);
                if(bar!=null)
                    bar.setVisibility(View.GONE);
            }
        });
    }

    public void checkDownloads(){
        if(placesDownload && commentsDownload && dealsDownload){
            Collections.sort(list,new Comparator<Place>() {
                @Override
                public int compare(Place lhs, Place rhs) {
                    PlaceFilter fil=PlaceFilter.getInstance();
                    if(fil.sorting== PlaceFilter.SortBy.like){
                        if(lhs.likes<rhs.likes)
                            return 1;
                        else if(lhs.likes>rhs.likes)
                            return -1;
                        else
                            return 0;
                    }else if(fil.sorting== PlaceFilter.SortBy.rating){
                        if(lhs.rating<rhs.rating)
                            return 1;
                        else if(lhs.rating>rhs.rating)
                            return -1;
                        else
                            return 0;
                    }else{
                        if(lhs.distance[0]<rhs.distance[0]){
                            return -1;
                        }else if(lhs.distance[0]>rhs.distance[0]){
                            return 1;
                        }else{
                            return 0;
                        }
                    }
                }
            });

            setSupportProgressBarIndeterminateVisibility(false);
            setScreen(Screen.list);
            ActionBar act=(getSupportActionBar());
            PlaceFilter filter=PlaceFilter.getInstance();
            String sub=String.format(getResources().getString(R.string.categorydistanceradius),
                    App.getDistanceString(filter.metrics,filter.getDistance(filter.metrics)*1000),
                    list.size());
            act.setSubtitle(sub);

            if(firstFilter)
                getSupportFragmentManager().beginTransaction().remove(fi).commit();
            listFragment = new PlaceListFragment();
            listFragment.setList(list);
            listFragment.setColor(color);
            listFragment.setOnItemClickListener(listSelected);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();
            setScreen(Screen.list);
            if(bar!=null)
                bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void CommentsDownloaded() {
        commentsDownload=true;
        checkDownloads();
    }

    @Override
    public void dealsDownloaded() {
        dealsDownload=true;
        checkDownloads();
    }

    @Override
    public void locationSet(){
        if(!CategoryList.getCategory(Category.SELECTED_CATEGORY_ID).favourite)
            refreshList();
        else
            refreshFavourite();
    }

}
