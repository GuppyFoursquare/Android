package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.place.Place;
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
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    public static String username;
    public static String userapikey;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
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
        view = inflater.inflate(R.layout.fragment_login, container, false);;

        Button loginButton = (Button)view.findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginUrl = App.SitePath + "api/auth.php?op=login";
                JSONObject apiResponse = null;
                final Activity tt = getActivity();
                // Request a json response

                Map<String, String> map = new HashMap<String, String>();
                map.put("name", "kemalsami");
                map.put("pass", "kemalsami");

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    Log.i("--- GUPPY ---" , response.getString("status"));

                                    JSONObject responseContent = response.getJSONObject("content");
                                    username = responseContent.getString("usr_username");
                                    userapikey = responseContent.getString("usr_apikey");

                                    Toast.makeText( tt ,username + " - " + userapikey , Toast.LENGTH_LONG).show();

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

            }
        });



        Button sendComment = (Button)view.findViewById(R.id.btnSendComment);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginUrl = App.SitePath + "api/auth.php?op=comment&apikey="+userapikey;
                JSONObject apiResponse = null;
                final Activity tt = getActivity();
                // Request a json response

                Map<String, String> map = new HashMap<String, String>();
                map.put("plc_id", "37");
                map.put("message", "sasasa");
                map.put("score", "4");

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    Log.i("--- GUPPY ---" , response.getString("status"));

                                    String responseContent = response.getString("content");

                                    Toast.makeText( tt ,responseContent , Toast.LENGTH_LONG).show();

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
