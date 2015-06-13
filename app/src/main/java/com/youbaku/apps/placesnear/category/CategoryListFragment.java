//
//  CategoryListFragment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public  class CategoryListFragment extends Fragment{
    private ArrayList<Category> list;
    private CategoryAdapter adap;
    private test.OnFragmentInteractionListener mListener;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    public void setList(ArrayList<Category> cat){
        list=cat;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      view=inflater.inflate(R.layout.category_layout, container, false);

        adap=new CategoryAdapter(getActivity(),list);
        GridView grLv=(GridView)view.findViewById(R.id.gridView1);
        grLv.setAdapter(adap);

        grLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "başarılı" + position + "-" + list.get(position).objectId,
                        Toast.LENGTH_SHORT).show();
                Intent in=new Intent(getActivity(), PlaceActivity.class);
                in.putExtra(PlaceActivity.COLOR,list.get(position).color);
                in.putExtra(PlaceActivity.TITLE,list.get(position).getName());
                in.putExtra(Place.ID,list.get(position).objectId);
                Category.SELECTED_CATEGORY_ID=list.get(position).objectId;

            /***************************************************************************************
             ***************************************************************************************
             *                          SAMPLE API CALL
             ***************************************************************************************
             **************************************************************************************/
                String url = "http://192.168.2.50/youbaku/api/restaurant.php";
                JSONObject apiResponse = null;
                // Request a json response
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    String glossary = response.getJSONObject("glossary").getJSONObject("GlossDiv").getJSONObject("GlossList").getJSONObject("GlossEntry").getString("Abbrev");
                                    //String title = glossary.getString("title");

                                    Log.i("GUPPY" , glossary);

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
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsObjRequest);

            /***************************************************************************************
             ***************************************************************************************
             *                          SAMPLE API CALL
             ***************************************************************************************
             **************************************************************************************/


                //startActivity(in);
            }
        });


        return view;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // TODO: Rename and change types and number of parameters
    public static CategoryListFragment newInstance(String param1, String param2) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public CategoryListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
/*public class CategoryListFragment extends ListFragment {
    private ArrayList<Category> list;
    private CategoryAdapter adap;

    public CategoryListFragment() {}

    public void setList(ArrayList<Category> cat){
        list=cat;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(list==null){
            System.err.println("CategoryListFragment.setList should be used before fragment used on screen");
            return;
        }
        adap=new CategoryAdapter(getActivity(),list);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((App)getActivity().getApplication()).track(App.ANALYSIS_CATEGORIES);

        getListView().setDivider(null);
        getListView().setSelector(getResources().getDrawable(R.drawable.category_list_selector));
        setListAdapter(adap);
    }
*/
  /*  @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent in=new Intent(getActivity(), PlaceActivity.class);
        in.putExtra(PlaceActivity.COLOR,list.get(position).color);
        in.putExtra(PlaceActivity.TITLE,list.get(position).getName());
        in.putExtra(Place.ID,list.get(position).objectId);
        Category.SELECTED_CATEGORY_ID=list.get(position).objectId;
        startActivity(in);
    }
}
*/