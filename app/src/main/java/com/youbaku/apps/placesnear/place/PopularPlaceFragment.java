package com.youbaku.apps.placesnear.place;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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



        //String url2 = App.SitePath+"api/places.php?op=nearme&lat="+my.latitude+"&lon="+my.longitude+"&scat_id="+ SubCategory.SELECTED_SUB_CATEGORY_ID;
        //For testing Places request
        String url2 = App.SitePath + "api/places.php?op=search&popular=1";
        JSONObject apiResponse = null;
        final Activity tt = getActivity();
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jArray = response.getJSONArray("content");
                            list = new ArrayList<Place>();
                            System.out.println("places downloaded " + jArray.length());

                            if (jArray.length() > 0) {


                                //Read JsonArray
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject o = jArray.getJSONObject(i);


                                    double rating = 0.0;
                                    if (o.has("plc_avg_rating")) {

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


                                                //Toast.makeText(getApplicationContext(),"Yes! There is rating array which is "+c.text,Toast.LENGTH_LONG).show();

                                            }

                                        }

                                        rating = Double.parseDouble(o.getString("plc_avg_rating"));
                                        //Inflate places
                                        p.setId(o.getString("plc_id"));
                                        p.setName(o.getString("plc_name"));
                                        p.setImgUrl(o.getString("plc_header_image"));
                                        p.setAddress(o.getString("plc_address"));
                                        p.setRating(rating);
                                        p.setWeb(o.getString("plc_website"));
                                        p.email = o.getString("plc_email");
                                        p.setPhone(o.getString("plc_contact"));
                                        //p.open = o.getString("plc_intime");
                                        //p.close = o.getString("plc_outtime");


                                        String isActive = o.getString("plc_is_active");
                                        if (isActive == "1") {
                                            p.setIsActive(true);
                                        } else {
                                            p.setIsActive(false);
                                        }

                                        String PLACE_INFO_WITHOUT_HTML_TAG = String.valueOf(Html.fromHtml(Html.fromHtml(o.getString("plc_info")).toString()));
                                        p.setDescription(PLACE_INFO_WITHOUT_HTML_TAG);


                                        double latitude = Double.parseDouble(o.getString("plc_latitude"));
                                        double longitude = Double.parseDouble(o.getString("plc_longitude"));
                                        p.setLocation(latitude, longitude);

                                        p.setLocation(latitude, longitude);
                                        MyLocation my = MyLocation.getMyLocation(getActivity());
                                        //Location.distanceBetween(my.latitude, my.longitude, p.getLatitude(), p.getLongitude(), p.distance);
                                        Location.distanceBetween(40.372877, 49.842825, p.getLatitude(), p.getLongitude(), p.distance);//For testing

                                        boolean pop = true;
                                        if (filter.popular && p.rating < 3.0) {
                                            pop = false;
                                        }
                                        boolean open = true;
                                        if (filter.open) {
                                            open = p.isOpen();
                                        }


                                        boolean filterKeyword = filter.keyword.length() != 0 ? p.getName().toLowerCase().contains(filter.keyword.toLowerCase()) : true;
                                        boolean filterDistance = filter.getDistance(PlaceFilter.DistanceSystem.km) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.km) > p.distance[0] / 1000 : true;
                                        boolean filterDistanceMl = filter.getDistance(PlaceFilter.DistanceSystem.ml) != 0 ? filter.getDistance(PlaceFilter.DistanceSystem.ml) > p.distance[0] / 1000 * 0.6214 : true;


                                        if (pop && open && filterKeyword && filterDistance && filterDistanceMl) {
                                            list.add(p);
                                        }
                                    } else {
                                        rating = 0.0;
                                    }




                                }

                                listFragment = new PlaceListFragment();
                                listFragment.setList(list);
                                listFragment.setColor(App.DefaultBackgroundColor);
                                listFragment.setOnItemClickListener(listSelected);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.main_activity_popular_place, listFragment);
                                ft.commit();
                                //getChildFragmentManager().beginTransaction().replace(R.id.main_activity_place, listFragment).commit();

                                // --GUPPY COMMENT IMPORTANT--
                                /*
                                 * try-catch kaldırılması durumunda server geciktirilme durumunda(sleep)
                                 * Volley sonucunun fragment'i değiştirme isteğinden ve ilgili fragment'in
                                 * olmamasından hata ile karşılaşılıyor.
                                 */

                                try {

                                } catch (IllegalStateException e) {
                                    Log.e("--- GUPPY ---", "Error occur on replace fragment");
                                }

                            } else {

                                AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                                bu.setMessage(getResources().getString(R.string.novenuemessage));
                                bu.setNegativeButton(getResources().getString(R.string.alertcancelbuttonlabel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                bu.setPositiveButton(getResources().getString(R.string.newfilterbuttonlabel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                bu.show();
                            }


                        } catch (JSONException e) {

                            AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                            bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                            bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
                            bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            bu.show();
                            e.printStackTrace();
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


        return view;


    }

    AdapterView.OnItemClickListener listSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place.FOR_DETAIL = list.get(position);
            Place.ID = list.get(position).getId();
            Place.EMAIL = list.get(position).getEmail();
            Intent in = new Intent(getActivity(), PlaceDetailActivity.class);
            in.putExtra("title", list.get(position).getName());
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
