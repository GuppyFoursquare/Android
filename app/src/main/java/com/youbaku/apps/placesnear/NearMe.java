package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.youbaku.apps.placesnear.adapter.TabsPagerAdapter;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceDetailActivity;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.filter.FilterFragment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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

    private ArrayList<Place> list;
    private Map<String,Place> placeMap = new HashMap<>();
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
        //setUpMapIfNeeded();
        getNearMePlaces();
    }


    private void setUpMapIfNeeded(ArrayList<Place> place) {
        if (nearMeMap == null) {

            nearMeMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            nearMeMap.getUiSettings().setScrollGesturesEnabled(false);
            nearMeMap.setOnMarkerClickListener(markerClickListener);
            nearMeMap.setOnCameraChangeListener(cameraChangeListener);

            LatLng userLocation=new LatLng(40.3859933,49.8232647);
            CameraUpdate ca= CameraUpdateFactory.newLatLngZoom(userLocation, 15);
            nearMeMap.animateCamera(ca);

            for(Place p : place){
                LatLng istanbulKoordinat = new LatLng(p.getLatitude(), p.getLongitude());
                nearMeMap.addMarker(new MarkerOptions().position(istanbulKoordinat).title(p.getId()));
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


    private void getNearMePlaces(){

        final double userLatitude = 40.372877;
        final double userLongitude = 49.842825;

        String nearMeURL = App.SitePath + "api/places.php?op=nearme&lat="+userLatitude+"&lon="+userLongitude;
        JSONObject apiResponse = null;

        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, nearMeURL, apiResponse, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        if (response.getString("status").equalsIgnoreCase("SUCCESS")) {

                            JSONArray places = response.getJSONArray("content");
                            list = new ArrayList<Place>();

                            System.out.println("places downloaded " + places.length());

                            if (places.length() > 0) {

                                //firstFilter = false;
                                //placesDownload = true;

                                //Read JsonArray
                                for (int i = 0; i < places.length(); i++) {
                                    JSONObject o = places.getJSONObject(i);
                                    final PlaceFilter filter = PlaceFilter.getInstance();
                                    final Place p = new Place();

                                    if (o.has("rating")) {

                                        JSONArray arr = o.getJSONArray("rating");

                                        for (int j = 0; j < arr.length(); j++) {
                                            final Comment c = new Comment();
                                            JSONObject obj = arr.getJSONObject(j);
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            try {
                                                Date d = format.parse(obj.getString("places_rating_created_date"));
                                                c.created = d;
                                                c.text = obj.getString("place_rating_comment");
                                                c.comment_id = obj.getString("place_rating_id");
                                                c.rating = Double.parseDouble(obj.getString("place_rating_rating"));
                                                c.name = obj.getString("places_rating_by");
                                                p.comments.add(c);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                    double rating = 0.0;
                                    if (o.has("plc_avg_rating")) {
                                        rating = Double.parseDouble(o.getString("plc_avg_rating"));
                                    } else {
                                        rating = 0.0;
                                    }

                                    //Inflate places
                                    p.setId(o.getString("plc_id"));
                                    p.setName(o.getString("plc_name"));
                                    p.setImgUrl(o.getString("plc_header_image"));
                                    p.setRating(rating);
                                    p.address = o.getString("plc_address");
                                    p.web = o.getString("plc_website");
                                    p.phone = o.getString("plc_contact");
                                    p.open = o.getString("plc_intime");
                                    p.close = o.getString("plc_outtime");
                                    p.isActive = o.getString("plc_is_active").equalsIgnoreCase("1") ? true : false;
                                    p.setDescription(String.valueOf(Html.fromHtml(Html.fromHtml(o.getString("plc_info")).toString())));
                                    p.setLocation(Double.parseDouble(o.getString("plc_latitude")), Double.parseDouble(o.getString("plc_longitude")));

                                    list.add(p);
                                    placeMap.put(p.getId() , p);
                                }

                                setUpMapIfNeeded(list);

                                // --GUPPY COMMENT IMPORTANT--
                                    /*
                                     * try-catch kaldırılması durumunda server geciktirilme durumunda(sleep)
                                     * Volley sonucunun fragment'i değiştirme isteğinden ve ilgili fragment'in
                                     * olmamasından hata ile karşılaşılıyor.
                                     */

//                                    try{
//                                        checkDownloads();
//                                    }catch (IllegalStateException e){
//                                        Log.e("--- GUPPY ---" , "Error occur on replace fragment");
//                                    }

                            } else {
                                Toast.makeText(getActivity() , "Response place # is 0" , Toast.LENGTH_LONG).show();
                            }

                        // ----- ----- RESPONSE STATUS != SUCCESS ----- -----
                        // ----- ----- RESPONSE STATUS != SUCCESS ----- -----
                        }else{

                        }

                    } catch (JSONException e) {

                        AlertDialog.Builder bu = new AlertDialog.Builder(getActivity());
                        bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                        bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
                        bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                    refreshList();
                            }
                        });
                        bu.show();

                        e.printStackTrace();
                        Log.e("--- GUPPY --- " , "Response JSON error");

                        return;
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

    GoogleMap.OnMapClickListener mapClickListener=new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(nearMeMarker==null)
                nearMeMarker=nearMeMap.addMarker(new MarkerOptions().position(latLng));
            else
                nearMeMarker.setPosition(latLng);
        }
    };

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Place.FOR_DETAIL = placeMap.get(marker.getTitle());
            Place.ID = placeMap.get(marker.getTitle()).getId();
            Intent in = new Intent(getActivity().getApplicationContext(), PlaceDetailActivity.class);
            in.putExtra("title", placeMap.get(marker.getTitle()).getName());
            startActivity(in);
            return true;
        }
    };

    GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            nearMeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.3859933,49.8232647), cameraPosition.zoom));
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
