package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Hide Status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Lock Portrait Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_splash_screen);

        if(!App.checkInternetConnection(getApplication())){
            App.showGenericInfoActivity(getApplication(),App.typeConnection,getResources().getString(R.string.networkconnectionerrormessage));
            return;
        }

        String url = App.SitePath+"api/register.php";
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
                                }else {
                                    Intent i = new Intent(getApplication(),MainActivity.class);
                                    startActivity(i);
                                }
                            }else{
                                Toast.makeText(getApplicationContext() , "Response not success", Toast.LENGTH_SHORT).show();
                                Log.e("---GUPPY ERROR---", "SplassScreenActivity -> apikey request");
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
}
