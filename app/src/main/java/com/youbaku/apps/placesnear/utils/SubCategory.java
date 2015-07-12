package com.youbaku.apps.placesnear.utils;

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
    private String img;

    public static String SELECTED_SUB_CATEGORY_ID="";
    public static String SELECTED_SUB_CATEGORY_NAME="";

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
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



    // *********************************************************************************************
    // ---------- ---------- ---------- INITIAL PROCESS ---------- ---------- ----------
    // *********************************************************************************************

    public static void fetchSubcategoryList(final Category categoryObj){
        //Calling Api
        String url = App.SitePath+"api/category.php?cat_id="+categoryObj.getObjectId();

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>(){

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

                                    categoryObj.getSubCatList().add(subcategory);
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
    }
}
