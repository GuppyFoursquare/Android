package com.youbaku.apps.placesnear;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestActivty extends ActionBarActivity {
    public static ArrayList<PlaceInfo> list;
    ProgressDialog progress;
    RequestQueue queue;
    JsonObjectRequest request;
    TextView t;
    Map<String, String> map = new HashMap<String, String>();
    private PlaceInfo pi;
    private PlaceInfo pii;

    public static String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activty);
        ActionBar act = ((ActionBar) getSupportActionBar());
        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.DefaultActionBarColor)));
        act.setDisplayShowHomeEnabled(true);
        act.setLogo(R.drawable.app_logo);
        act.setTitle("");
        act.setDisplayUseLogoEnabled(true);
        act.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 0x05;
        ProgressBar pro = new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro, params);



        if (Build.VERSION.SDK_INT > 10) {
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar2);
            SpinKitDrawable1 spin = new SpinKitDrawable1(this);
            spin.setColorFilter(Color.parseColor(App.LoaderColor), PorterDuff.Mode.SRC_OVER);
            bar.setIndeterminateDrawable(spin);
        }


        ArrayList categoryList=new ArrayList();
        categoryList.add("1");
        categoryList.add("18");

        //Calling Api
        String url = "http://193.140.63.162/youbaku/api/places.php?op=search";
        Map<String, ArrayList> map = new HashMap<String, ArrayList>();
        map.put("subcat_list", categoryList);

        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                try{
                                    JSONArray jArray = response.getJSONArray("content");
                                    //Read JsonArray
                                    for (int i = 0; i < jArray.length(); i++) {

                                        JSONObject obj = jArray.getJSONObject(i);

                                        Place place = new Place();
                                        place.setId(obj.getString("plc_id"));
                                        place.setName(obj.getString("plc_name"));
                                        place.setImgUrl(obj.getString("plc_header_image"));

                                        double latitude = Double.parseDouble(obj.getString("plc_latitude"));
                                        double longitude = Double.parseDouble(obj.getString("plc_longitude"));
                                        place.setLocation(latitude, longitude);

                                        t=(TextView)findViewById(R.id.hello_txt);
                                        t.setText(place.getName());
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }



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






       /*// 2- We will call api
        String url2 = App.SitePath+"api/places.php?op=info&plc_id=1";
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            JSONArray jArray = response.getJSONArray("content");
                            FavoriteCategory f = new FavoriteCategory();
                            list=new ArrayList<PlaceInfo>();

                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final PlaceInfo s = new PlaceInfo();
                                final Place p=new Place();

                                p.description=obj.getString("plc_meta_description");
                                s.setDescription(obj.getString("plc_email"));
                                Log.i("Places are: ", s.getDescription());
                                t.setText(s.getDescription());
                                list.add(s);
                                ((ProgressBar)findViewById(R.id.progressBar2)).setVisibility(View.INVISIBLE);


                            }

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

*/


}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_activty, menu);
        return true;
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
