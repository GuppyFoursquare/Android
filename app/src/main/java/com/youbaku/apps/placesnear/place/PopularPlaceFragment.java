package com.youbaku.apps.placesnear.place;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PopularPlaceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PopularPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularPlaceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ArrayList<Place> list = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private PlaceListFragment listFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularPlaceFragment newInstance(String param1, String param2) {
        PopularPlaceFragment fragment = new PopularPlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PopularPlaceFragment() {
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_popular_place, container, false);


        // Eğer popular list fetch edilmiş ise adapter'a ekle
        // Değilse fetch et ve ekle
        if(Place.placesArrayListNearMe!=null && Place.placesArrayListNearMe.size()>0){

            listFragment = new PlaceListFragment();
            listFragment.setList(Place.placesArrayListNearMe);
            listFragment.setColor(App.DefaultBackgroundColor);
            listFragment.setOnItemClickListener(listSelected);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_activity_popular_place, listFragment);
            ft.commit();

        }else{

            listFragment = new PlaceListFragment();
            listFragment.setAdapter(new PlaceAdapter(getActivity(),null, Color.parseColor("#00000000")));
            listFragment.setColor(App.DefaultBackgroundColor);
            listFragment.setOnItemClickListener(listSelected);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_activity_popular_place, listFragment);
            ft.commit();

            Place.fetchPopularPlaces(listFragment.getAdapter());
        }

        return view;

    }

    AdapterView.OnItemClickListener listSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place.FOR_DETAIL = Place.placesArrayListNearMe.get(position);
            Place.ID = Place.placesArrayListNearMe.get(position).getId();
            Place.EMAIL = Place.placesArrayListNearMe.get(position).getEmail();
            Intent in = new Intent(getActivity(), PlaceDetailActivity.class);
            in.putExtra("title", Place.placesArrayListNearMe.get(position).getName());
            startActivity(in);
        }
    };

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

}
