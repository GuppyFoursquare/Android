/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.category;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.category.adapters.CategoryAdapter;
import com.youbaku.apps.placesnear.utils.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoryListFragment extends Fragment {
    View view;
    Fragment newFragment;
    private ArrayList<Category> list;
    private CategoryAdapter adap;
    private test.OnFragmentInteractionListener mListener;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CategoryListFragment newInstance(String param1, String param2) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public void setList(ArrayList<Category> cat) {
        list = cat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.category_layout, container, false);


        pullList();



        return view;
    }

    //Pulling list from category web service
    private void pullList(){

      //Calling Api
        String url = App.SitePath+"api/category.php";

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            list=new ArrayList<Category>();
                            JSONArray jArray = response.getJSONArray("content");


                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);

                                final Category c=new Category();
                                c.title=obj.getString("cat_name")+"";
                                c.setObjectId(obj.getString("cat_id"));
                                c.iconURL=obj.getString("cat_image");
                                list.add(c);


                                Log.i("GUPPY", c.title);
                            }
                            adap = new CategoryAdapter(getActivity(), list);
                            final GridView grLv = (GridView) view.findViewById(R.id.gridView1);
                            grLv.setAdapter(adap);




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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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