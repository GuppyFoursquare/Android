//
//  CategoryAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.category.SubCategoryListActivty;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.utils.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearMeCategoryAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> list;
    private ArrayList subCategoryList;
    private ArrayList placeList;
    private ImageLoader mImageLoader;

    public NearMeCategoryAdapter(Context context, ArrayList<Category> list) {
        super(context, R.layout.category_list_item);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View catview, ViewGroup parent) {

            if(catview==null){
                LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                catview=inflater.inflate(R.layout.category_list_item,null);
            }

            final Category c = list.get(position);

            //Image Location
            final String url = "http://youbaku.com/uploads/category_images/"+c.iconURL; // URL of the image

            mImageLoader = VolleySingleton.getInstance().getImageLoader();
            NetworkImageView image = (NetworkImageView)catview.findViewById(R.id.image_category_list_item);

            image.setImageUrl(url,mImageLoader);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getCategoryPlaces(list.get(position).objectId);

                }
            });

            return catview;
    }


    private void getCategoryPlaces(String mainCategoryID){

        //Calling Api
        String url = App.SitePath+"api/category.php?cat_id="+mainCategoryID;

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            subCategoryList=new ArrayList<String>();
                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                try{
                                    JSONArray jArray = response.getJSONArray("content");
                                    //Read JsonArray
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject obj = jArray.getJSONObject(i);
                                        subCategoryList.add(obj.getString("cat_id"));
                                        //Log.e("---GUPPY---" , "Cat id " + obj.getString("cat_id"));
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }

                            getPlacesWithSearch(subCategoryList);

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



    private void getPlacesWithSearch(ArrayList categoryList){

        //Calling Api
        String url = App.SitePath+"api/places.php?op=search";
        Map<String, ArrayList> map = new HashMap<String, ArrayList>();
        map.put("subcat_list", categoryList);

        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            placeList=new ArrayList<Place>();

                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                try{
                                    JSONArray jArray = response.getJSONArray("content");
                                    //Read JsonArray
                                    for (int i = 0; i < jArray.length(); i++) {

                                        JSONObject obj = jArray.getJSONObject(i);

                                        Place place = new Place();
                                        place.setId(obj.getString("plc_id"));
                                        place.setName(obj.getString("plc_name"));
                                        place.setImgUrl(obj.getString("plc_header_image"));

                                        double latitude = Double.parseDouble(obj.getString("plc_latitude"));
                                        double longitude = Double.parseDouble(obj.getString("plc_longitude"));
                                        place.setLocation(latitude, longitude);

                                        placeList.add(place);
                                        //Log.e("---GUPPY--- " , obj.toString());
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }

                            NearMe.reloadPlaceMarkers();

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
