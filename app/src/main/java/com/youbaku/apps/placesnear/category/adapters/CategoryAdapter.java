/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.category.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.PlaceActivity;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private ArrayList<Category> list;

    private ImageLoader mImageLoader;
    NetworkImageView image;

    private ArrayList subCategoryList;

    public CategoryAdapter(Context context, ArrayList<Category> list) {
        super(context, R.layout.category_list_item);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.category_list_item,null);
        }

        final Category c = list.get(position);
       /* TextView t=((TextView)convertView.findViewById(R.id.text_category_list_item));
        t.setText(c.getName());*/


        //Image Location
        final String url = "http://youbaku.com/uploads/category_images/"+c.iconURL; // URL of the image

        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        image = (NetworkImageView)convertView.findViewById(R.id.image_category_list_item);

        image.setImageUrl(url,mImageLoader);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (c.getSubCatList() == null || c.getSubCatList().size() == 0) {
                    getCategoryPlaces(c.objectId);


                } else {

                    ArrayList<String> subCategoryList = new ArrayList<String>();
                    for (SubCategory s : c.getSubCatList()) {
                        subCategoryList.add(s.getId());

                    }


                }
                Intent in = new Intent(getContext(), PlaceActivity.class);

                SubCategory.SELECTED_SUB_CATEGORIES_ID=subCategoryList;
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(in);

            }
        });



        return convertView;
    }

    public void getCategoryPlaces(String mainCategoryID){

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
                                    }

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                                SubCategory.SELECTED_SUB_CATEGORIES_ID=subCategoryList;

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
