//
//  Category
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.utils;

import android.graphics.Bitmap;

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
import java.util.List;
import java.util.Locale;

public class Category {

    public String title         = "";
    public String subtitle      = "";
    public Bitmap icon;
    public String iconURL       = "";
    public String markerURL     = "";
    public String color         = "";
    public String objectId      = "";
    public boolean favourite    = false;
    private JSONObject name;
    private List<SubCategory> subCatList = new ArrayList<>();


    public static List<Category> categoryList;

    //--- USED TO GET SELECTED CATEGORY ---
    public static String SELECTED_CATEGORY_ID   = "";
    public static String SELECTED_IMAGE_URL     = "";
    public static boolean IS_CATEGORIES_FETCHED   = false;


    @Override
    public String toString() {
        return title;
    }

    public void setName(JSONObject name) {
        this.name = name;
    }
    public String getName(){
        String name="";
        try{
            if(Locale.getDefault().getLanguage().equals("he"))
                name=this.name.getString("hr");
            else if(Locale.getDefault().getLanguage().equals("zh-CHS"))
                name=this.name.getString("zn_Hans");
            else if(Locale.getDefault().getLanguage().equals("zh-CHT"))
                name=this.name.getString("zn_Hant");
            else
                name=this.name.getString(Locale.getDefault().getLanguage());
        }catch (Exception e){
            try {
                name=this.name.getString("en");
            }catch (Exception ee){
                name=this.title;
            }
        }
        return name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public String getObjectId() {
        return objectId;
    }
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    public static List<Category> getCategoryList() {
        return categoryList;
    }
    public static void setCategoryList(List<Category> categoryList) {
        Category.categoryList = categoryList;
    }


    // *********************************************************************************************
    // ---------- ---------- ---------- INITIAL PROCESS ---------- ---------- ----------
    // *********************************************************************************************

    static {

        // Önce category'ler fetch edilir.
        // Daha sonra subCategory'ler fetch edilir

        // Initially gets category list
        fetchCategoryList();
    }

    public static void fetchCategoryList(){
        //Calling Api
        String url = App.SitePath+"api/category.php";

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                categoryList = new ArrayList<Category>();
                                JSONArray responseCategoryList = response.getJSONArray("content");

                                //Read JsonArray
                                for (int i = 0; i < responseCategoryList.length(); i++) {
                                    JSONObject obj = responseCategoryList.getJSONObject(i);

                                    Category category = new Category();

                                    category.title=obj.getString("cat_name");
                                    category.objectId = obj.getString("cat_id");
                                    category.iconURL=obj.getString("cat_image");

                                    categoryList.add(category);

                                    SubCategory.fetchSubcategoryList(category);
                                }

                                IS_CATEGORIES_FETCHED = true;

                            }else{
                                IS_CATEGORIES_FETCHED = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            IS_CATEGORIES_FETCHED = false;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        IS_CATEGORIES_FETCHED = false;
                    }
                });

        // Add the request to the queue
        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);

    }


    public List<SubCategory> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCategory> subCatList) {
        this.subCatList = subCatList;
    }
}
