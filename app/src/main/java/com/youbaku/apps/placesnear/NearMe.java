package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.youbaku.apps.placesnear.adapter.TabsPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearMe.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearMe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearMe extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private boolean havePlace=true;
    private GoogleMap nearMeMap;
    private LocationManager locationManager;
    private Marker nearMeMarker;

    private View view;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearMe.
     */
    // TODO: Rename and change types and number of parameters
    public static NearMe newInstance(String param1, String param2) {
        NearMe fragment = new NearMe();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NearMe() {
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

        view = inflater.inflate(R.layout.activity_maps, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        if (nearMeMap == null) {

            nearMeMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            nearMeMap.getUiSettings().setScrollGesturesEnabled(false);

            LatLng istanbulKoordinat = new LatLng(41.021161, 29.004065);
            nearMeMap.addMarker(new MarkerOptions().position(istanbulKoordinat).title("Kız Kulesi"));
            nearMeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbulKoordinat, 13));

            if (false && nearMeMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        nearMeMap.setMyLocationEnabled(true);

        if(!havePlace) {
            nearMeMap.setOnMapClickListener(mapClickListener);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            havePlace = true;
        }else{
            LatLng ll=new LatLng(40.3859933,49.8232647);
            CameraUpdate ca= CameraUpdateFactory.newLatLngZoom(ll, 15);
            nearMeMap.animateCamera(ca);
            //m=mMap.addMarker(new MarkerOptions().position(ll));

            nearMeMap.addMarker(new MarkerOptions().position(new LatLng(40.3859933,49.8232647)));
            nearMeMap.addMarker(new MarkerOptions().position(new LatLng(40.4081835,49.8740803)));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    GoogleMap.OnMapClickListener mapClickListener=new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(nearMeMarker==null)
                nearMeMarker=nearMeMap.addMarker(new MarkerOptions().position(latLng));
            else
                nearMeMarker.setPosition(latLng);
        }
    };

    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------
    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------
    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------

    /**a
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
