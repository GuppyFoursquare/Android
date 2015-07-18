package com.youbaku.apps.placesnear.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by orxan on 17.06.2015.
 */
public class SubCategory {

    private String title;
    private String id;
    private String subcategoy_image;
    private String subcategoy_name;
    private boolean selected;

    public static String SELECTED_SUB_CATEGORY_ID="";
    public static String SELECTED_SUB_CATEGORY_NAME="";

    public static ArrayList SELECTED_SUB_CATEGORIES_ID;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // *********************************************************************************************
    // ---------- ---------- ---------- INITIAL PROCESS ---------- ---------- ----------
    // *********************************************************************************************

    public static void fetchSubcategoryList(final Activity activity , final View view , final Category categoryObj){
        //Calling Api
        String url = App.SitePath+"api/category.php?cat_id="+categoryObj.getObjectId();

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>(){

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                categoryObj.getSubCatList().clear();
                                JSONArray responseSubcategoryList = response.getJSONArray("content");

                                //Read JsonArray
                                for (int i = 0; i < responseSubcategoryList.length(); i++) {
                                    JSONObject obj = responseSubcategoryList.getJSONObject(i);

                                    SubCategory subcategory = new SubCategory();
                                    subcategory.id = obj.getString("cat_id");
                                    subcategory.setTitle(obj.getString("cat_name"));

                                    subcategory.setSubcategoy_name(obj.getString("cat_name"));
                                    subcategory.setSubcategoy_image(obj.getString("cat_image"));
                                    categoryObj.getSubCatList().add(subcategory);
                                }


                                Category.FETCHED_SUBCATEGORY_NUM++;
                                if(Category.FETCHED_SUBCATEGORY_NUM==Category.categoryList.size()){
                                    Category.IS_SUBCATEGORIES_FETCHED = true;
                                    Category.refreshSearchFragment(activity,view);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("---GUPPY SubCategory---", "SubCategory JSON Exception...");
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


    public String getSubcategoy_image() {
        return subcategoy_image;
    }

    public void setSubcategoy_image(String subcategoy_image) {
        this.subcategoy_image = subcategoy_image;
    }

    public String getSubcategoy_name() {
        return subcategoy_name;
    }

    public void setSubcategoy_name(String subcategoy_name) {
        this.subcategoy_name = subcategoy_name;
    }
}
