/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place.comment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
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

    public void saveComment(final Activity activity , final ProgressDialog progress) {

        if (comment.getText().toString().equals("") || String.valueOf(c.rating).equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.formvalidationmessage), Toast.LENGTH_LONG).show();
            return;
        }
        String loginUrl = App.SitePath +"api/auth.php?token="+ App.getYoubakuToken() +"&apikey="+ App.getYoubakuAPIKey() + "&op=comment";
        JSONObject apiResponse = null;
        final Activity tt = getActivity();

        // Request a json response
        Map<String, String> map = new HashMap<String, String>();
        map.put("plc_id", Place.ID);
        map.put("message", comment.getText().toString());
        map.put("score", String.valueOf(c.rating));

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progress.dismiss();
                        if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {
                                try {

                                    String responseContent = response.getString("content");

                                    //1.Show Generic Success Message
                                    App.showGenericInfoActivity(getActivity(),App.typeSuccess,getResources().getString(R.string.reviewsentsuccessmessage));

                                    //2.Go Previous Activity
                                    getActivity().getSupportFragmentManager().popBackStack();

                                    //3.Find correct screen using GoBack function
                                    ((PlaceDetailActivity) getActivity()).goBack();

                                } catch (JSONException e) {

                                    App.showGenericInfoActivity(getActivity(), App.typeInfo, getResources().getString(R.string.reviewsentunsuccessmessage));
                                    e.printStackTrace();
                                    return;
                                }

                        }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                            //We should get new apikey and token
                            RegisterAPI.callRegister(activity);

                            //Error Info
                            Log.e("531-FAILURE_PERMISSION" , "CreateComment->saveComment-> api key missing error");
                            Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();

                        }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_COMMENT_MULTIPLE")){

                            App.showGenericInfoActivity(getActivity(), App.typeInfo, getResources().getString(R.string.multipleCommentError));
                            return;

                        }else {

                            App.showGenericInfoActivity(getActivity(), App.typeInfo, getResources().getString(R.string.loadingdataerrormessage));
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
