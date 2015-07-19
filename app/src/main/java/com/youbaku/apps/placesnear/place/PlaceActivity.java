/*
 * YouBaku Project
 *
 * Copyright (C) 2015 Guppy Organization. All rights reserved.
 *
 * Modified by Guppy org.
 *
 * This Activty is PlaceActivty. Places with related category are listed
 * on PlaceActivty.
 */


package com.youbaku.apps.placesnear.place;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.SpinKitDrawable1;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.location.MyLocationSet;
import com.youbaku.apps.placesnear.place.comment.AllCommentsDownloaded;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.AllDealsDownloaded;
import com.youbaku.apps.placesnear.place.filter.FilterFragment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//

public class PlaceActivity extends ActionBarActivity implements AllCommentsDownloaded, AllDealsDownloaded, MyLocationSet {
    public static final String COLOR = "color";
    public static final String TITLE = "title";

    @Override
    public void locationSet() {
        refreshList();
    }


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
        act.setTitle(title);
        act.setSubtitle("Places for " + title);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 0x05;
        ProgressBar pro = new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro, params);

        if (MyLocation.checkLocationServices(getApplicationContext())) {
            setContentView(R.layout.need_location_service);
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



        // --GUPPY COMMENT IMPORTANT--
        /**
         *  Clear code olması adına aşağıdaki alan ve comment'li AdapterView.OnItemClickListener
         *  çalıştırılması gerekmektedir. Tabi bu durumda aşağıdaki refreshList methodu silinmesi
         *  gerekmektedir.
         *
         *  Ancak problemli olan bazı kısımlar bulunmaktadır. Örneğin bu durumda servis istenilen
         *  dataları dönmemektedir. Bu durumda servis tarafında değişiklik yapılması gerekmektedir.
         *
         *  Web servislerde değişiklik şu sekilde olmalıdır. İstenilen sonuç dönmesi durumunda
         *  sonuçların id'lerine sahip place değerlerinin tamamı gelmesi gerekmektedir.
         *
         */

        refreshList();

//        listFragment = new PlaceListFragment();
//        // IF popular places are fetched before then set it to adapter directly
//        // OTHERWISE fetch first and set to adapter
//        if(Place.placesArrayListSearch!=null && Place.placesArrayListSearch.size()>0){
//            listFragment.setList(Place.placesArrayListSearch);
//        }else{
//            listFragment.setAdapter(new PlaceAdapter(this,Place.placesArrayListSearch, Color.BLACK));
//            Place.fetchSearchPlaces(listFragment.getAdapter());
//        }
//
//        listFragment.setColor(App.DefaultBackgroundColor);
//        listFragment.setOnItemClickListener(listSelected);
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();

    }


    // --GUPPY COMMENT IMPORTANT--
//    AdapterView.OnItemClickListener listSelected = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            /**
//             * Before start Activity we have to fetch place info from server then
//             * start activity. Otherwise place info may not be includes all information.
//             *
//             * Place info fetched at PlaceDetailActivity > PlaceDetailFragment
//             */
//            Place.FOR_DETAIL = Place.placesArrayListSearch.get(position);
//            Place.ID = Place.placesArrayListSearch.get(position).getId();
//            Place.EMAIL = Place.placesArrayListSearch.get(position).getEmail();
//            Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
//            in.putExtra("title", Place.placesArrayListSearch.get(position).getName());
//            startActivity(in);
//        }
//    };

    AdapterView.OnItemClickListener listSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place.FOR_DETAIL = list.get(position);
            Place.ID = list.get(position).getId();
            Place.EMAIL = list.get(position).getEmail();
            Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
            in.putExtra("title", list.get(position).getName());
            startActivity(in);
        }
    };

    private void refreshList() {
        if (!App.checkInternetConnection(this) && onScreen) {
            App.showInternetError(this);
            return;
        }

        MyLocation my = MyLocation.getMyLocation(getApplicationContext());
        if (!my.isSet()) {
            my.subscriber = this;
            my.callLocation();
            return;
        }

        //String url2 = App.SitePath+"api/places.php?op=nearme&lat="+my.latitude+"&lon="+my.longitude+"&scat_id="+ SubCategory.SELECTED_SUB_CATEGORY_ID;

        //GET SELECTED SUBCATEGORIES
        ArrayList selectedSubCategory = new ArrayList();
        for(Category c : Category.categoryList){
            for(SubCategory s : c.getSubCatList()){
                if(s.isSelected()){
                    selectedSubCategory.add(s.getId());
                }
            }
        }

        //Calling Api
        String url2 = App.SitePath+"api/places.php?op=search";
        Map<String, ArrayList> map = new HashMap<String, ArrayList>();
        map.put("subcat_list", selectedSubCategory);

        final Activity tt = this;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url2, new JSONObject(map), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jArray = response.getJSONArray("content");
                            list = new ArrayList<Place>();
                            System.out.println("places downloaded " + jArray.length());

                            if (jArray.length() > 0) {

                                firstFilter = false;
                                placesDownload = true;
                                //Read JsonArray
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject o = jArray.getJSONObject(i);
                                    final PlaceFilter filter = PlaceFilter.getInstance();
                                    final Place p = new Place();

                                    if (o.has("rating")) {

                                        JSONArray arr = o.getJSONArray("rating");


                                        for (int j = 0; j < arr.length(); j++) {
                                            final Comment c = new Comment();
                                            JSONObject obj = arr.getJSONObject(j);
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            try {
                                                Date d = format.parse(obj.getString("places_rating_created_date"));
                                                c.created = d;
                                                c.text = obj.getString("place_rating_comment");
                                                c.comment_id = obj.getString("place_rating_id");
                                                c.rating = Double.parseDouble(obj.getString("place_rating_rating"));
                                                c.name = obj.getString("usr_username");

                                                //Getting User Image
                                                if(obj.isNull("usr_profile_picture")){
                                                    c.user_img="";
                                                    Log.i("---GUPPY USER IMAGE---","No Available Image");
                                                }
                                                else{
                                                    c.user_img = obj.getString("usr_profile_picture").toString();
                                                }


                                                p.comments.add(c);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                            //Toast.makeText(getApplicationContext(),"Yes! There is rating array which is "+c.text,Toast.LENGTH_LONG).show();

                                        }

                                    }


                                    double rating = 0.0;
                                    if (o.has("plc_avg_rating")) {
                                        rating = Double.parseDouble(o.getString("plc_avg_rating"));
                                    } else {
                                        rating = 0.0;
                                    }


                                    //Inflate places
                                    p.setId(o.getString("plc_id"));
                                    p.setName(o.getString("plc_name"));
                                    p.setImgUrl(o.getString("plc_header_image"));
                                    p.setAddress(o.getString("plc_address"));
                                    p.setRating(rating);
                                    p.setWeb(o.getString("plc_website"));
                                    p.email=o.getString("plc_email");
                                    p.setPhone(o.getString("plc_contact"));
                                    p.open = o.getString("plc_intime");
                                    p.close = o.getString("plc_outtime");


                                    String isActive = o.getString("plc_is_active");
                                    if (isActive == "1") {
                                        p.setIsActive(true);
                                    } else {
                                        p.setIsActive(false);
                                    }

                                    String PLACE_INFO_WITHOUT_HTML_TAG = String.valueOf(Html.fromHtml(Html.fromHtml(o.getString("plc_info")).toString()));
                                    p.setDescription(PLACE_INFO_WITHOUT_HTML_TAG);


                                    double latitude = Double.parseDouble(o.getString("plc_latitude"));
                                    double longitude = Double.parseDouble(o.getString("plc_longitude"));
                                    p.setLocation(latitude, longitude);

                                    MyLocation my = MyLocation.getMyLocation(getApplicationContext());
                                    Location.distanceBetween(my.latitude, my.longitude, p.getLatitude(), p.getLongitude(), p.getDistance());
                                   // Location.distanceBetween(40.372877, 49.842825, p.getLatitude(), p.getLongitude(), p.getDistance());//For testing

                                    boolean pop = true;
                                    if (filter.popular && p.rating < 3.0) {
                                        pop = false;
                                    }
                                    boolean open = true;
                                    if (filter.open) {
                                        open = p.isOpen();
                                    }


                                    boolean filterKeyword = filter.keyword.length() != 0 ? p.getName().toLowerCase().contains(filter.keyword.toLowerCase()) : true;
                                    boolean filterDistance = filter.getDistance(PlaceFilter.DistanceSystem.km) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.km) > p.getDistance()[0] / 1000 : true;
                                    boolean filterDistanceMl = filter.getDistance(PlaceFilter.DistanceSystem.ml) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.ml) > p.getDistance()[0] / 1000 * 0.6214 : true;


                                    if (pop && open && filterKeyword && filterDistance && filterDistanceMl) {
                                        list.add(p);
                                    }

                                }

                                // --GUPPY COMMENT IMPORTANT--
                                /*
                                 * try-catch kaldırılması durumunda server geciktirilme durumunda(sleep)
                                 * Volley sonucunun fragment'i değiştirme isteğinden ve ilgili fragment'in
                                 * olmamasından hata ile karşılaşılıyor.
                                 */

                                try {
                                    checkDownloads();
                                } catch (IllegalStateException e) {
                                    Log.e("--- GUPPY ---", "Error occur on replace fragment");
                                }

                            } else {
                                setSupportProgressBarIndeterminateVisibility(false);
                                AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                                bu.setMessage(getResources().getString(R.string.novenuemessage));
                                bu.setNegativeButton(getResources().getString(R.string.alertcancelbuttonlabel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                bu.setPositiveButton(getResources().getString(R.string.newfilterbuttonlabel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (fi == null)
                                            fi = new FilterFragment();
                                        firstFilter = true;
                                        fi.setColor(Color.parseColor(color));
                                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, fi).commit();
                                        setScreen(Screen.filter);
                                    }
                                });
                                bu.show();
                            }


                        } catch (JSONException e) {

                            AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                            bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                            bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
                            bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    refreshList();
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
                if (!placesDownload)//!dealsDownload || !commentsDownload ||
                    return true;
                if (fi == null)
                    fi = new FilterFragment();
                fi.setColor(Color.parseColor(App.DefaultBackgroundColor));
                getSupportFragmentManager().beginTransaction().addToBackStack("filter").replace(R.id.main_activity_place, fi).commit();
                setScreen(Screen.filter);
                return true;
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
                InputMethodManager man = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                man.hideSoftInputFromWindow(fi.getWindowToken(), 0);
                getSupportFragmentManager().popBackStack();
                listFragment.setList(list);
                if (listFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(listFragment).commit();
                }

                if (bar != null)
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


    /* private void  refreshFavourite(){
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
     }*/
    @Override
    public void CommentsDownloaded() {
        commentsDownload = true;
        checkDownloads();
    }

    @Override
    public void dealsDownloaded() {
        dealsDownload = true;
        checkDownloads();
    }


    public void checkDownloads() {
        //if(placesDownload && commentsDownload && dealsDownload){
        if (placesDownload) {
            Collections.sort(list, new Comparator<Place>() {
                @Override
                public int compare(Place lhs, Place rhs) {
                    PlaceFilter fil = PlaceFilter.getInstance();
                    if (fil.sorting == PlaceFilter.SortBy.like) {
                        if (lhs.likes < rhs.likes)
                            return 1;
                        else if (lhs.likes > rhs.likes)
                            return -1;
                        else
                            return 0;
                    } else if (fil.sorting == PlaceFilter.SortBy.rating) {
                        if (lhs.rating < rhs.rating)
                            return 1;
                        else if (lhs.rating > rhs.rating)
                            return -1;
                        else
                            return 0;
                    } else {
                        if (lhs.getDistance()[0] < rhs.getDistance()[0]) {
                            return -1;
                        } else if (lhs.getDistance()[0] > rhs.getDistance()[0]) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }


                }
            });

            setSupportProgressBarIndeterminateVisibility(false);
            setScreen(Screen.list);
            ActionBar act = (getSupportActionBar());
            PlaceFilter filter = PlaceFilter.getInstance();
            String sub = String.format(getResources().getString(R.string.categorydistanceradius),
                    App.getDistanceString(filter.metrics, filter.getDistance(filter.metrics) * 1000),
                    list.size());
            act.setSubtitle(sub);

            if (firstFilter)
                getSupportFragmentManager().beginTransaction().remove(fi).commit();
            listFragment = new PlaceListFragment();
            listFragment.setList(list);
            listFragment.setColor(App.DefaultBackgroundColor);
            listFragment.setOnItemClickListener(listSelected);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();
            setScreen(Screen.list);
            if (bar != null)
                bar.setVisibility(View.GONE);
        }
    }

}
