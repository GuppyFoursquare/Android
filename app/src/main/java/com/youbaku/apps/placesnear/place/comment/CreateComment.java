//
//  CreateComment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.comment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateComment extends Fragment {
    private Comment c;
    private boolean isSaving = false;
    private EditText lastFocus;
    private EditText name;
    private EditText email;
    private EditText comment;

    public CreateComment() {
        c = new Comment();
        c.isActive = !App.moderateReviews;
        c.place = Place.FOR_DETAIL.id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((App)getActivity().getApplication()).track(App.ANALYSIS_WRITE_REVIEW);

        ((RatingBar) getView().findViewById(R.id.rating_new_comment)).setOnRatingBarChangeListener(barChanged);
        ((RatingBar) getView().findViewById(R.id.rating_new_comment)).setRating(3);
        c.rating = 3;


        comment = ((EditText) getView().findViewById(R.id.comment_new_comment));
        comment.addTextChangedListener(commentWatcher);

    }

    public void saveComment() {

        if (comment.getText().toString().equals("") || String.valueOf(c.rating).equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.formvalidationmessage), Toast.LENGTH_LONG).show();
            return;
        }
        String loginUrl = App.SitePath +"api/auth.php?token="+App.youbakuToken+"&apikey="+App.youbakuAPIKey + "&op=comment";
        JSONObject apiResponse = null;
        final Activity tt = getActivity();

        // Request a json response
        Map<String, String> map = new HashMap<String, String>();
        map.put("plc_id", Place.ID);
        map.put("message", comment.getText().toString());
        map.put("score", String.valueOf(c.rating));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.i("--- GUPPY ---", response.getString("status"));
                            String responseContent = response.getString("content");
                            //Toast.makeText( tt ,responseContent , Toast.LENGTH_LONG).show();
                            //Toast.makeText(getActivity(),getResources().getString(R.string.reviewsentsuccessmessage),Toast.LENGTH_LONG).show();
                            AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                            bu.setMessage(getResources().getString(R.string.reviewsentsuccessmessage));
                            bu.setPositiveButton(getResources().getString(R.string.alertokbuttonlabel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            bu.show();

                        } catch (JSONException e) {

                            AlertDialog.Builder bu = new AlertDialog.Builder(tt);
                            bu.setMessage(getResources().getString(R.string.reviewsentunsuccessmessage));
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



     /*
        ((PlaceDetailActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        setEditable(false);
        final double rat=(Place.FOR_DETAIL.rating+c.rating*2)/2;
        ParseObject o=new ParseObject(App.PARSE_COMMENTS);
        o.put(Comment.PLACE,c.place);
        o.put(Comment.RATING,(c.rating*2)+"");
        o.put(Comment.EMAIL,c.email);
        o.put(Comment.ISACTIVE,c.isActive);
        o.put(Comment.NAME, c.name);
        o.put(Comment.TEXT, c.text);
        System.out.println(c.place+" "+c.rating+" "+c.email+" "+c.name+" "+c.text);
        o.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    setEditable(false);
                    e.printStackTrace();
                    ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                    Toast.makeText(getActivity(),getResources().getString(R.string.reviewsentunsuccessmessage),Toast.LENGTH_LONG).show();
                    return;
                }
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(App.PARSE_PLACES);
                //query.whereEqualTo(Place.ID, Place.FOR_DETAIL.id);
                query.getInBackground(Place.FOR_DETAIL.id,new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(e!=null){
                            setEditable(false);
                            e.printStackTrace();
                            ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                            Toast.makeText(getActivity(),getResources().getString(R.string.reviewsentunsuccessmessage),Toast.LENGTH_LONG).show();
                            return;
                        }
                        parseObject.put(Place.RATING,rat+"");
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                setEditable(false);
                                if(e!=null){
                                    e.printStackTrace();
                                    ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                                    Toast.makeText(getActivity(),getResources().getString(R.string.reviewsentunsuccessmessage),Toast.LENGTH_LONG).show();
                                    return;
                                }
                                saveDone();
                            }
                        });
                    }
                });
            }
        });
    */

    }

    private void setEditable(boolean editable) {
        ((RatingBar) getView().findViewById(R.id.rating_new_comment)).setFocusable(editable);

        ((EditText) getView().findViewById(R.id.comment_new_comment)).setFocusable(editable);
        isSaving = !editable;
    }

    public boolean isSaving() {
        return isSaving;
    }

    private void saveDone() {
        Toast.makeText(getActivity(), getResources().getString(R.string.donebuttonlabel), Toast.LENGTH_SHORT).show();
        InputMethodManager man = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindowToken() != null)
            man.hideSoftInputFromWindow(getWindowToken(), 0);
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        getActivity().getSupportFragmentManager().popBackStack();
        ((PlaceDetailActivity) getActivity()).goBack();
    }

    public IBinder getWindowToken() {
        if (lastFocus != null)
            return lastFocus.getWindowToken();
        else
            return null;
    }


    RatingBar.OnRatingBarChangeListener barChanged = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor(App.YellowColor), PorterDuff.Mode.SRC_ATOP);
                c.rating = rating;
            }
            else{

            }

        }
    };

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            c.name = s.toString();
            lastFocus = name;
        }
    };

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            c.email = s.toString();
            lastFocus = email;
        }
    };

    TextWatcher commentWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            c.text = s.toString();
            lastFocus = comment;
        }
    };
}
