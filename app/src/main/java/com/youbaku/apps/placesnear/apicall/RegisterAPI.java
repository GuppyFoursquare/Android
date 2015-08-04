package com.youbaku.apps.placesnear.apicall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kemal on 03/08/15.
 */
public class RegisterAPI {




    /**
     *
     * @param activity
     *
     * This is the register method
     */
    public static void callRegister(final Activity activity ){

        final SharedPreferences sharedPref = activity.getSharedPreferences(App.sharedPreferenceKey, Context.MODE_PRIVATE);

        final String url = App.SitePath+"api/register.php";
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                JSONObject responseContent = response.getJSONObject("content");

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(App.KEY_APIKEY, responseContent.getString("apikey"));
                                editor.putString(App.KEY_TOKEN, responseContent.getString("token"));
                                editor.commit();

                                // Set token and apikey
                                App.setTokenAndAPIKey(activity);

                                activity.recreate();


                            }else{
                                Toast.makeText(activity.getApplicationContext(), "Response not success", Toast.LENGTH_SHORT).show();
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

    }


}
