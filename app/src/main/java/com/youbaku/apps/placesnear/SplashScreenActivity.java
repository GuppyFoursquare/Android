/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplashScreenActivity extends Activity {

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity=this;

        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Hide Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Lock Portrait Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_splash_screen);

        //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
        //----- ----- ----- ----- CHECKER PART ----- ----- ----- -----
        //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
        if(!App.checkInternetConnection(getApplication())){
            App.showGenericInfoActivity(getApplication(), App.typeConnection, getResources().getString(R.string.networkconnectionerrormessage));
            return;
        }
        if(!MyLocation.checkLocationServices(getApplicationContext())){
            setContentView(R.layout.need_location_service);
            App.sendErrorToServer(activity, getClass().getName(), "onCreate", "Location Service Not Work ");
            return;
        }else{
            MyLocation location = MyLocation.getMyLocation(this);
            location.callHard();
        }


        //Check api and token are still used
        //it will checked by request but this time we check only static value
        if(App.youbakuAPIKey==null || App.youbakuToken==null){

            //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
            //----- ----- ----- ----- REQUEST PART ----- ----- ----- -----
            //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            String url = App.SitePath+"api/register.php";
            App.sendErrorToServer(this, getClass().getName(), "onCreate", "GUPPY-CASE-01-- url::"+url);
            // Request a json response
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                    JSONObject responseContent = response.getJSONObject("content");
                                    App.youbakuToken = responseContent.getString("token");
                                    App.youbakuAPIKey = responseContent.getString("apikey");

                                    if(!App.checkInternetConnection(getApplication())){
                                        App.showGenericInfoActivity(getApplication(),App.typeConnection,getResources().getString(R.string.networkconnectionerrormessage));
                                        return;
                                    }
                                    else if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                                        buildAlertMessageNoGps();
                                    }
                                    else {
                                        Intent i = new Intent(getApplication(),MainActivity.class);
                                        startActivity(i);
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext() , "Response not success", Toast.LENGTH_SHORT).show();
                                    Log.e("---GUPPY ERROR---", "SplassScreenActivity -> apikey request");
                                    App.sendErrorToServer(activity, getClass().getName() , "onCreate", "JSON RESPONSE not SUCCESS");
                                }

                            } catch (JSONException e) {
                                App.sendErrorToServer(activity, getClass().getName() , "onCreate", e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            App.sendErrorToServer(activity, getClass().getName(), "onCreate_Response.ErrorListener", error.getMessage());
                        }
                    });

            // Add the request to the queue
            VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);

        }else{

            //--GUPPY COMMENT IMPORTANT--
            //Check apikey and token valid
            //assume that there are valid keys
            Intent i = new Intent(getApplication(),MainActivity.class);
            startActivity(i);

        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        App.showGenericInfoActivity(getApplication(),App.typeInfo,"We are sorry! You should first open GPS to use Application.");
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}