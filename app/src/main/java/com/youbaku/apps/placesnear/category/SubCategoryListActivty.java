package com.youbaku.apps.placesnear.category;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.category.adapters.SubCategoryAdapter;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.FavoriteCategory;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SubCategoryListActivty extends ActionBarActivity {
    public static final String COLOR="color";
    public static final String TITLE="title";

    private ArrayList<SubCategory> sublist;
    private String color="";
    private String title="";

    private Screen screen=Screen.list;
    private boolean onScreen=true;
    private boolean firstFilter=false;
    public static Activity tt;

    private SubCategoryAdapter adap;

    private enum Screen{list,filter,favorite}
    private MenuItem goFilter;
    private MenuItem doFilter;
    private Category c =new Category();
    private ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_sub_category_list_activty);

        Intent in=getIntent();
        //color=in.getStringExtra(COLOR);
        title=in.getStringExtra("title");
        final String cat_id=in.getStringExtra("ListId");
        Toast.makeText(getApplicationContext(), "Guppppy " + cat_id,
                Toast.LENGTH_SHORT).show();


        ActionBar act=((ActionBar)getSupportActionBar());
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(App.DefaultActionBarColor));
        act.setBackgroundDrawable(colorDrawable);
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        act.setDisplayShowCustomEnabled(true);
        act.setTitle(title);
        act.setSubtitle("YouBaku");

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
       

        if(MyLocation.checkLocationServices(getApplicationContext())){
            setContentView(R.layout.need_location_service);
            return;
        }

        ((RelativeLayout)findViewById(R.id.main_activity_subcategory)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));



        String url2 = "http://192.168.1.38/youbaku/api/category.php?cat_id="+cat_id;
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            JSONArray jArray = response.getJSONArray("content");
                            FavoriteCategory f = new FavoriteCategory();
                            sublist=new ArrayList<SubCategory>();

                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final SubCategory s = new SubCategory();


                                s.setTitle(obj.getString("cat_name"));
                                Toast.makeText(getApplicationContext(), s.getTitle(),
                                        Toast.LENGTH_SHORT).show();


                                sublist.add(s);


                            }
                            adap = new SubCategoryAdapter(getApplicationContext(),sublist);
                            final GridView grLv = (GridView) findViewById(R.id.subGridView);
                            grLv.setAdapter(adap);

                        } catch (JSONException e) {
                            e.printStackTrace();
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
    protected void onStart() {
        onScreen=true;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub_category_list_activty, menu);

        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
