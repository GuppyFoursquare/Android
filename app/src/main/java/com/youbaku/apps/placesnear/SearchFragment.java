package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.adapter.ExpandableListviewAdapter;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceActivity;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AnimatedExpandableListView listView;
    private Button searchBtn;
    public Activity activity;

    public static ExpandableListviewAdapter adapter;
    public static View view;

    public static boolean internetConnection = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        internetConnection = App.checkInternetConnection(getActivity());
        if (!internetConnection) {
            view = inflater.inflate(R.layout.need_network, container, false);

        } else {

            view = inflater.inflate(R.layout.fragment_search, container, false);
        }

        searchBtn = (Button) view.findViewById(R.id.searcBtn);

        if (Category.categoryList == null || Category.categoryList.size() == 0) {
            Category.fetchCategoryList(getActivity(), view);

        }


        try {


            Category.refreshSearchFragment(getActivity(), view);
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //GET SELECTED SUBCATEGORIES
                    ArrayList selectedSubCategory = new ArrayList();
                    for(Category c : Category.categoryList){
                        for(SubCategory s : c.getSubCatList()){
                            if(s.isSelected()){
                                selectedSubCategory.add(s.getId());
                            }
                        }

                    }

                    if(selectedSubCategory.size()==0 ){
                        Toast.makeText(getActivity(), "You should select at least one category!", Toast.LENGTH_LONG).show();
                    }
                    else{

                        Intent in = new Intent(getActivity(), PlaceActivity.class);
                        startActivity(in);
                    }
                }
            });


        } catch (NullPointerException e) {
            Log.e("xxxxx", e.toString());
        }


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    //Pulling list from category web service
    private void getCategoryList() {

        //Calling Api
        String url = App.SitePath + "api/category.php";

        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            ArrayList list = new ArrayList<Category>();
                            JSONArray jArray = response.getJSONArray("content");


                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);

                                final Category c = new Category();
                                c.title = obj.getString("cat_name") + "";
                                c.setObjectId(obj.getString("cat_id"));
                                c.iconURL = obj.getString("cat_image");
                                list.add(c);

                            }


                            //Setting Adapter
                            adapter = new ExpandableListviewAdapter(getActivity(), list);
                            listView = (AnimatedExpandableListView) view.findViewById(R.id.listView);
                            listView.setAdapter(adapter);


                            //set initial expand status to listview
                            listView.expandGroup(0);

                            // In order to show animations, we need to use a custom click handler
                            // for our ExpandableListView.
                            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    // We call collapseGroupWithAnimation(int) and
                                    // expandGroupWithAnimation(int) to animate group
                                    // expansion/collapse.


                                    if (listView.isGroupExpanded(groupPosition)) {
                                        listView.collapseGroupWithAnimation(groupPosition);
                                    } else {
                                        listView.expandGroupWithAnimation(groupPosition);

                                    }

                                    return true;
                                }

                            });


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
