package com.youbaku.apps.placesnear;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.PlaceInfo;
import com.youbaku.apps.placesnear.utils.FavoriteCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestActivty extends ActionBarActivity {
    private ArrayList<PlaceInfo> list;
    ProgressDialog progress;
    RequestQueue queue;
    JsonObjectRequest request;
    TextView t;
    Map<String, String> map = new HashMap<String, String>();
    private PlaceInfo pi;
    private PlaceInfo pii;

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
        ArrayList<PlaceInfo>list=new ArrayList<>();

        t=(TextView)findViewById(R.id.hello_txt);

        map.put("keyword","cook");
        queue = VolleySingleton.getRequestQueue();
        String url =App.SitePath+"api/places.php?op=search";// the URL
        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                url,
                new JSONObject(map), // the parameters for the php
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        // here you parse the json response

                        try {


                            JSONArray jArray = response.getJSONArray("content");
                            FavoriteCategory f = new FavoriteCategory();
                            t=(TextView)findViewById(R.id.hello_txt);


                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final PlaceInfo s = new PlaceInfo();

                                s.setDescription(obj.getString("plc_name"));
                                Log.i("Places are: ", s.getDescription());
                                t.setText(s.getDescription());

                                ((ProgressBar)findViewById(R.id.progressBar2)).setVisibility(View.INVISIBLE);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            t.setText(e.toString());
                        }

                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                      /* here you can warn the user that there
                      was an error while trying to get the json
                      information from the php  */
                    }
                });

        // executing the quere to get the json information
        queue.add(request);




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
