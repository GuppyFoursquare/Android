/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.ProfilActivity;
import com.youbaku.apps.placesnear.RegisterActivity;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by kemal on 19/07/15.
 */
public class User {

    private String user_name;
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
//                                Toast.makeText(activity , "User::" + myProfile.getUser_name() , Toast.LENGTH_LONG).show();

                                activity.finish();

                            }catch (JSONException e){
                                Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Response Content JSONException");
                                App.sendErrorToServer(activity, getClass().getName(), "userRegister", "Response Content JSONException---" + e.getMessage());

                                e.printStackTrace();
                            }

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



                            //if logout, finish current activity and go to home page
                            activity.finish();
                            Intent in=new Intent(activity,MainActivity.class);
                            MainActivity.doLogin.setIcon(null);
                            activity.startActivity(in);





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

                                Intent in =new Intent(activity.getApplication(),ProfilActivity.class);
                                activity.startActivity(in);

                            }catch (JSONException e){
                                Log.e("---GUPPY---", "User -> userInfo -> Response Content JSONException");
                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Response Content JSONException---" + e.getMessage());

                                e.printStackTrace();
                            }

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

    public void setUser_profile_picture(String user_profile_picture) {
        this.user_profile_picture = user_profile_picture;
    }
}
