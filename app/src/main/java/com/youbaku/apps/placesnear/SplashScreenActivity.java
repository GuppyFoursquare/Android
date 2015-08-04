/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashScreenActivity extends Activity {

    private Activity activity;
    private SharedPreferences sharedPref;

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

        sharedPref = getSharedPreferences(App.sharedPreferenceKey, Context.MODE_PRIVATE);
        String token    = sharedPref.getString(App.KEY_TOKEN , null);
        String apikey   = sharedPref.getString(App.KEY_APIKEY, null);


        //Check api and token are still used
        //it will checked by request but this time we check sonly static value
        if( token==null || apikey==null){

            //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
            //----- ----- ----- ----- REQUEST PART ----- ----- ----- -----
            //----- ----- ----- ----- ----- ----- ----- ----- ----- -----
            RegisterAPI.callRegister( this );

        }else{

            //--GUPPY COMMENT IMPORTANT--
            //Check apikey and token valid
            //assume that keys are valid for now
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