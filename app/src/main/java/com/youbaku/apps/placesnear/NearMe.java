package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceDetailActivity;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.youbaku.apps.placesnear.utils.Category;

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
    private boolean havePlace = true;

    private Activity activity;
    private View view;
    private static GoogleMap nearMeMap;
    private LocationManager locationManager;
    private Marker nearMeMarker;
    private MapView mapView;

    private LatLng userLocation;

    public static ArrayList<Place> nearMePlacelist;
    private NearMeTouchableWrapper nearMeTouchView;
    private ArrayList<Category> catlist;
    private NearMeCategoryAdapter adap;
    private LinearLayout l;


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
        view = inflater.inflate(R.layout.nearme_maps, container, false);

        // Get Activity
        activity = getActivity();

        // Set Linear Layout
        l = (LinearLayout) view.findViewById(R.id.nearmecategory);
        l.setVisibility(View.INVISIBLE);

        MapsInitializer.initialize(getActivity());
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:

                mapView = (MapView) view.findViewById(R.id.nearmemap);
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            nearMeMap = googleMap;
                            nearMeMap.getUiSettings().setScrollGesturesEnabled(false);
                            nearMeMap.setOnMarkerClickListener(markerClickListener);

                            // --- If places are already fetched then not fetch again
                            if (Place.placesListNearMe == null || Place.placesListNearMe.size() == 0) {
                                getNearMePlaces();
                            } else {
                                setUpMapIfNeeded();
                            }

                            // --- If categories are already fetched then not fetch again
                            if (Category.categoryList == null || Category.categoryList.size() == 0) {
                                getNearMeCategoryList();
                            } else {
                                setNearMeCategoryList(getActivity());
                            }
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    /**
     * Bu method ile category view'i haritada görülür ya da gizlenir
     */
    public void categoryGridViewChangeVisibility() {
        l.setVisibility(l.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }


    public static void reloadPlaceMarkers(final Activity activity) {

        Set<String> placeKey = Place.placesListNearMe.keySet();

        nearMeMap.clear();

        // TODO: add marker to map
        Iterator ite = placeKey.iterator();
        while (ite.hasNext()) {
            Place p = Place.placesListNearMe.get(ite.next());
            LatLng placeLocation = new LatLng(p.getLatitude(), p.getLongitude());

            nearMeMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .title(p.getId())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_marker)));
        }


        nearMeMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            boolean not_first_time_showing_info_window;

            @Override
            public View getInfoWindow(final Marker marker) {

                // Getting view from the layout file info_window_layout
                View markerView = activity.getLayoutInflater().inflate(R.layout.nearme_marker, null);
                ImageView im = (ImageView) markerView.findViewById(R.id.nearmemarker_plc_img);

                // Find Place
                Place selectedPlace = null;
                for (Place p : nearMePlacelist) {
                    if (p.getId().equalsIgnoreCase(marker.getTitle())) {
                        selectedPlace = p;
                    }
                }

                if (selectedPlace != null) {

                    // -----------------------------------------------------
                    // Burada markerView gerekli datalar ile doldurulacaktır.
                    // -----------------------------------------------------
                    //Ratinglerin puana gore renklendirilmesi
                    TextView rateTxt = (TextView) markerView.findViewById(R.id.nearmeinfo_placerate);

                    if (selectedPlace.rating > 3.5 && selectedPlace.rating <= 5.0) {
                        rateTxt.setBackgroundColor(Color.parseColor(App.GreenColor));
                    } else if (selectedPlace.rating >= 3.0 && selectedPlace.rating <= 3.5) {
                        rateTxt.setBackgroundColor(Color.parseColor(App.YellowColor));
                    } else {
                        rateTxt.setBackgroundColor(Color.parseColor(App.ButtonColor));
                    }

                    rateTxt.setText(selectedPlace.getRating() + "/5.0");

                    ((TextView) markerView.findViewById(R.id.nearmeinfo_placename)).setText(selectedPlace.getName());

                    //Place resimlerini göstermek için Picasso kullanıldı. Sebebi volley - InfoAdapterde networkimageview kullanmak daha sorunlu picassoya gore.
                    //Aşağdakı kodun amacı resimleri ilk başta load yaparken gecikmesinin karşısını almaktır.
                    //---Related Link : http://stackoverflow.com/questions/18938187/add-an-image-from-url-into-custom-infowindow-google-maps-v2
                    String imgUrl = App.SitePath + "uploads/places_header_images/" + selectedPlace.getImgUrl(); // URL of the image
                    if (not_first_time_showing_info_window) {
                        Picasso.with(activity).load(imgUrl).placeholder(R.drawable.place_detail_image_placeholder).into(im);

                    } else {
                        not_first_time_showing_info_window = true;
                        Picasso.with(activity).load(imgUrl).placeholder(R.drawable.place_detail_image_placeholder).into(im, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (marker != null && marker.isInfoWindowShown()) {
                                    marker.showInfoWindow();
                                }

                            }

                            @Override
                            public void onError() {
                                Log.e(getClass().getSimpleName(), " error loading thumbnail");
                            }
                        });
                    }


                }


                return markerView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

        });

        nearMeMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Place.FOR_DETAIL = Place.placesListNearMe.get(marker.getTitle());
                Place.ID = Place.placesListNearMe.get(marker.getTitle()).getId();
                Place.EMAIL = Place.placesListNearMe.get(marker.getTitle()).getEmail();
                Intent in = new Intent(activity.getApplicationContext(), PlaceDetailActivity.class);
                in.putExtra("title", Place.placesListNearMe.get(marker.getTitle()).getName());
                activity.startActivity(in);
            }
        });
    }


    private void setUpMapIfNeeded() {

        //nearMeMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nearmefragmap)).getMap();
        MyLocation my = MyLocation.getMyLocation(getActivity());
        userLocation = new LatLng(my.latitude, my.longitude);
        CameraUpdate ca = CameraUpdateFactory.newLatLngZoom(userLocation, 12);
        nearMeMap.animateCamera(ca);
        nearMeMap.setOnMapClickListener(mapClickListener);

        reloadPlaceMarkers(activity);

        l.setVisibility(View.INVISIBLE);
    }


    // ---------- ---------- GOOGLE MAP FUNCTIONS ---------- ----------
    // ---------- ---------- GOOGLE MAP FUNCTIONS ---------- ----------
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            marker.showInfoWindow();

            return true;
        }
    };

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            categoryGridViewChangeVisibility();
        }
    };

    GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            if (!NearMeTouchableWrapper.mMapIsTouched) {

                Log.e("---GUPPY---", "on Camera Change " + cameraPosition.zoom);

                //nearMeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.3859933,49.8232647), cameraPosition.zoom));
                //NearMeTouchableWrapper.mMapIsTouched = true;
            }
        }
    };
    // ---------- ---------- GOOGLE MAP FUNCTIONS ---------- ----------
    // ---------- ---------- GOOGLE MAP FUNCTIONS ---------- ----------


    // ---------- ---------- VOLLEY FUNCTIONS ---------- ----------
    // ---------- ---------- VOLLEY FUNCTIONS ---------- ----------
    //Pulling list from category web service
    private void getNearMeCategoryList() {

        //Calling Api
//        String url = App.SitePath + "api/category.php";
        String url = App.SitePath+"api/category.php?token="+App.youbakuToken+"&apikey="+App.youbakuAPIKey;

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

                            adap = new NearMeCategoryAdapter(getActivity());
                            GridView grLv = (GridView) view.findViewById(R.id.nearmegrid);
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

    private void setNearMeCategoryList(Activity activity) {

        NearMeCategoryAdapter nearMeCategoryAdapter = new NearMeCategoryAdapter(activity);
        GridView nearMeGridView = (GridView) view.findViewById(R.id.nearmegrid);
        nearMeGridView.setAdapter(nearMeCategoryAdapter);

    }


    private void getNearMePlaces() {
        MyLocation my = MyLocation.getMyLocation(getActivity());

//        String nearMeURL = App.SitePath + "api/places.php?op=nearme&lat=" + my.latitude + "&lon=" + my.longitude;
        String nearMeURL = App.SitePath + "api/places.php?token="+App.youbakuToken+"&apikey="+App.youbakuAPIKey + "&op=nearme&lat=" + my.latitude + "&lon=" + my.longitude;
        JSONObject apiResponse = null;

        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, nearMeURL, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getString("status").equalsIgnoreCase("SUCCESS")) {

                                JSONArray places = response.getJSONArray("content");
                                setList(new ArrayList<Place>());

                                Place.placesListNearMe.clear();

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

                                                    //Getting User Image
                                                    if (obj.isNull("usr_profile_picture")) {
                                                        c.user_img = "";
                                                        Log.i("---GUPPY USER IMAGE---", "No Available Image");
                                                    } else {
                                                        c.user_img = obj.getString("usr_profile_picture").toString();
                                                    }

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
                                        p.setAddress(o.getString("plc_address"));
                                        p.setWeb(o.getString("plc_website"));
                                        p.email = o.getString("plc_email");
                                        p.setPhone(o.getString("plc_contact"));
                                        p.setOpen(o.getString("plc_intime"));
                                        p.setClose(o.getString("plc_outtime"));
                                        p.setIsActive(o.getString("plc_is_active").equalsIgnoreCase("1") ? true : false);
                                        p.setDescription(String.valueOf(Html.fromHtml(Html.fromHtml(o.getString("plc_info")).toString())));
                                        p.setLocation(Double.parseDouble(o.getString("plc_latitude")), Double.parseDouble(o.getString("plc_longitude")));

                                        getList().add(p);

                                        Place.placesListNearMe.put(p.getId(), p);
                                    }

                                    setUpMapIfNeeded();

                                } else {
                                    Toast.makeText(getActivity(), "Response place # is 0", Toast.LENGTH_LONG).show();
                                }

                                // ----- ----- RESPONSE STATUS != SUCCESS ----- -----
                                // ----- ----- RESPONSE STATUS != SUCCESS ----- -----
                            } else {

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
                            Log.e("--- GUPPY --- ", "Response JSON error");

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
    // ---------- ---------- VOLLEY FUNCTIONS ---------- ----------
    // ---------- ---------- VOLLEY FUNCTIONS ---------- ----------


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

    public ArrayList<Place> getList() {
        return nearMePlacelist;
    }

    public void setList(ArrayList<Place> list) {
        this.nearMePlacelist = list;
    }
    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------
    // ---------- ---------- IMPLEMENT LOCATION ---------- ----------


    /**
     * a
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


}
