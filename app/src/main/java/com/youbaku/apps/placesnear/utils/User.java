/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.ProfilActivity;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.RegisterActivity;
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceDetailActivity;
import com.youbaku.apps.placesnear.place.comment.Comment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kemal on 19/07/15.
 */
public class User {

    private String user_name;
    private String user_contact;

    private String user_firstName;
    private String user_lastName;
    private String user_email;
    private String user_profile_picture;

    private double userLatitude = 40.372877;
    private double userLongitude = 49.842825;


    private static User myProfile = new User( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private User(){ }

    /* Static 'instance' method */
    public static User getInstance( ) {
        if(myProfile==null){
            myProfile = new User();
            return myProfile;
        }
        return myProfile;
    }










    //---------------------------------- REQUEST PART -----------------------------------/
    /**
     *
     * @param currentActivity
     *
     * This method will be used for login to App
     */
    public static void userLogin(final Activity currentActivity){

        //Toast.makeText(getApplicationContext(), "Login is clicked", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_login_layout, null);
        alertDialog.setView(alertView);

            /* When positive  is clicked */
        alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel(); // Your custom code

                String loginUrl = App.SitePath + "api/auth.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey() + "&op=login";

                Map<String, String> map = new HashMap<String, String>();
                map.put("name", ((EditText) alertView.findViewById(R.id.username)).getText().toString());
                map.put("pass", ((EditText) alertView.findViewById(R.id.password)).getText().toString());

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (response.getString("status").equalsIgnoreCase("SUCCESS")) {

                                        JSONObject responseContent = response.getJSONObject("content");
                                        User.getInstance().setUser_name(responseContent.getString("usr_username"));
                                        User.getInstance().setUser_email(responseContent.getString("usr_email"));

                                        SharedPreferences sharedPref = currentActivity.getSharedPreferences(App.sharedPreferenceKey, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(App.KEY_ISAUTH, true);
                                        editor.commit();

                                        MainActivity.doLogin.setIcon(R.drawable.ic_profilelogo);

                                    }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                                        //We should get new apikey and token
                                        RegisterAPI.callRegister(currentActivity);

                                        //Error Info
                                        Log.e("531-FAILURE_PERMISSION" , "User->userRegister-> api key missing error");
                                        Toast.makeText(currentActivity, "We are try to register again...", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(currentActivity, response.getString("status"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {

                                    App.sendErrorToServer(currentActivity, getClass().getName(), "login Errror---", e.getMessage());
                                    Toast.makeText(currentActivity, "MainActivity login()", Toast.LENGTH_SHORT).show();

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
        });

            /* When register  button is clicked*/
        alertDialog.setNeutralButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(currentActivity, RegisterActivity.class);
                currentActivity.startActivity(in);
            }
        });

            /* When negative  button is clicked*/
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Your custom code
            }
        });

        alertDialog.show();
    }






    /**
     * @param URL
     * @param parameters
     * @param activity
     *
     *                          Example :: PlaceAdapter
     */
    public static void userRegister( String URL, Map<String,String> parameters, final Activity activity) {

        if (activity!=null && !App.checkInternetConnection(activity) ) {
            App.showInternetError(activity);
            return;
        }

        // Request a json response
        JSONObject params = (parameters!=null) ? (new JSONObject(parameters)) : (null);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        myProfile = new User();
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        // Check response is SUCCESS
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {

                            try{

                                JSONObject userProfile = response.getJSONObject(App.RESULT_CONTENT);


                                // GET response value from and set to singleton Object
                                myProfile.setUser_firstName(App.getJsonValueIfExist(userProfile, "usr_first_name"));
                                myProfile.setUser_lastName(App.getJsonValueIfExist(userProfile, "usr_last_name"));
                                myProfile.setUser_name(App.getJsonValueIfExist(userProfile, "usr_username"));

                                // SET SharedPrefences
                                SharedPreferences sharedPref = activity.getSharedPreferences(App.sharedPreferenceKey, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean(App.KEY_ISAUTH, true);
                                editor.commit();

                                activity.finish();

                            }catch (JSONException e){
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Response Content JSONException");
                                App.sendErrorToServer(activity, getClass().getName(), "userRegister", "Response Content JSONException---" + e.getMessage());

                                e.printStackTrace();
                            }


                        }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                            //We should get new apikey and token
                            RegisterAPI.callRegister(activity);

                            //Error Info
                            Log.e("531-FAILURE_PERMISSION" , "User->userRegister-> api key missing error");
                            Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();


                        }else{
                            try{
                                String resultStatus = App.getJsonValueIfExist(response, App.RESULT_STATUS);
                                if(resultStatus.equalsIgnoreCase("FAILURE_ALREADY_EXIST")){
                                    RegisterActivity.switchToMain(activity);
                                    App.showUserError(activity,false,"User already exist");
                                }
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + resultStatus);
                            }catch (NullPointerException e){
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + "NULL");
                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Status RETURN NULL----" + e.getMessage());
                            }
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "onErrorResponse----" + error.getMessage());
                    }
                });

        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }

    public static void userLogout( String URL, Map<String,String> parameters, final Activity activity) {

        if (activity!=null && !App.checkInternetConnection(activity) ) {
            App.showInternetError(activity);
            return;
        }

        // Request a json response
        JSONObject params = (parameters!=null) ? (new JSONObject(parameters)) : (null);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        myProfile = new User();
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        // Check response is SUCCESS
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {


                            SharedPreferences sharedPref = activity.getSharedPreferences(App.sharedPreferenceKey, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean(App.KEY_ISAUTH, false);
                            editor.commit();

                            //if logout, finish current activity and go to home page
                            activity.finish();
                            Intent in=new Intent(activity,MainActivity.class);
                            MainActivity.doLogin.setIcon(null);
                            activity.startActivity(in);


                        }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                            //We should get new apikey and token
                            RegisterAPI.callRegister(activity);

                            //Error Info
                            Log.e("531-FAILURE_PERMISSION" , "User->userLogout-> api key missing error");
                            Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();


                        }else{
                            try{
                                String resultStatus = App.getJsonValueIfExist(response, App.RESULT_STATUS);
                                if(resultStatus.equalsIgnoreCase("FAILURE_ALREADY_EXIST")){
                                    RegisterActivity.switchToMain(activity);
                                    App.showUserError(activity,false,"User already exist");
                                }
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + resultStatus);
                            }catch (NullPointerException e){
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + "NULL");
                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Status RETURN NULL----" + e.getMessage());
                            }
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "onErrorResponse----" + error.getMessage());
                    }
                });

        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }



    /**
     * @param URL
     * @param parameters
     * @param activity
     *
     */
    public static void userInfo( String URL, Map<String,String> parameters, final Activity activity) {

        if (activity!=null && !App.checkInternetConnection(activity) ) {
            App.showInternetError(activity);
            return;
        }

        // Request a json response
        JSONObject params = (parameters!=null) ? (new JSONObject(parameters)) : (null);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        myProfile = new User();
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        // Check response is SUCCESS
                        // ----- ----- ----- ----- - ----- ----- ----- -----
                        if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {

                            try{

                                JSONObject userProfile = response.getJSONObject(App.RESULT_CONTENT);


                                // GET response value from and set to singleton Object
                                myProfile.setUser_firstName(App.getJsonValueIfExist(userProfile, "usr_first_name"));
                                myProfile.setUser_lastName(App.getJsonValueIfExist(userProfile, "usr_last_name"));
                                myProfile.setUser_name(App.getJsonValueIfExist(userProfile, "usr_username"));
                                myProfile.setUser_email(App.getJsonValueIfExist(userProfile, "usr_email"));
                                myProfile.setUser_profile_picture(App.getJsonValueIfExist(userProfile, "usr_profile_picture"));
                                myProfile.setUser_contact(App.getJsonValueIfExist(userProfile, "usr_contact"));



                                Intent in =new Intent(activity.getApplication(),ProfilActivity.class);
                                activity.startActivity(in);

                            }catch (JSONException e){
                                Log.e("---GUPPY---", "User -> userInfo -> Response Content JSONException");
                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Response Content JSONException---" + e.getMessage());

                                e.printStackTrace();
                            }

                        }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                            //We should get new apikey and token
                            RegisterAPI.callRegister(activity);

                            //Error Info
                            Log.e("531-FAILURE_PERMISSION" , "User->userInfo-> api key missing error");
                            Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();

                        }else{
                            try{
                                String resultStatus = App.getJsonValueIfExist(response, App.RESULT_STATUS);
                                Log.e("---GUPPY---", "User -> userInfo -> Status " + resultStatus);

                                String resultContent = App.getJsonValueIfExist(response, App.RESULT_CONTENT);
                                Log.e("---GUPPY---", "User -> userInfo -> Content " + resultContent);
                            }catch (NullPointerException e){
                                Log.e("---GUPPY---", "User -> userInfo -> Status " + "NULL");
                                App.sendErrorToServer(activity, getClass().getName(), "userInfo", "Status RETURN NULL----" + e.getMessage());
                            }
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "onErrorResponse----" + error.getMessage());
                    }
                });

        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }


    //---------------------------------- REQUEST PART -----------------------------------/
    //---------------------------------- REQUEST PART -----------------------------------/
    //---------------------------------- REQUEST PART -----------------------------------/











    // *********************************************************************************************
    // ---------- ---------- ---------- ENCAPSULATION ---------- ---------- ----------
    // *********************************************************************************************

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_firstName() {
        return user_firstName;
    }

    public void setUser_firstName(String user_firstName) {
        this.user_firstName = user_firstName;
    }

    public String getUser_lastName() {
        return user_lastName;
    }

    public void setUser_lastName(String user_lastName) {
        this.user_lastName = user_lastName;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_profile_picture() {
        return user_profile_picture;
    }

    public String getUser_contact() {
        return user_contact;
    }

    public void setUser_contact(String user_contact) {
        this.user_contact = user_contact;
    }

    public void setUser_profile_picture(String user_profile_picture) {
        this.user_profile_picture = user_profile_picture;
    }
}
