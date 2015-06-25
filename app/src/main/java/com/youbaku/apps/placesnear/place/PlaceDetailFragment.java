//
//  PlaceDetailFragment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.Gravatar;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MapsActivity;
import com.youbaku.apps.placesnear.photo.MyViewPager;
import com.youbaku.apps.placesnear.photo.PhotoActivity;
import com.youbaku.apps.placesnear.photo.PhotoAdapter;
import com.youbaku.apps.placesnear.place.deal.Deal;
import com.youbaku.apps.placesnear.utils.FavoriteCategory;
import com.youbaku.apps.placesnear.web.WebActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceDetailFragment extends Fragment{
    private TextView topInfo;
    private TextView description;
    private TextView address;
    private TextView comment;
    private TextView commentInfo;
    private ImageView commentView;
    private ImageView[] photos;
    private ImageView dealImage;
    private TextView dealTitle;
    private TextView dealDate;
    private TextView dealText;
    private ImageView iconFace;
    private ImageView iconMap;
    private ImageView iconPhone;
    private ImageView iconTwit;
    private ImageView iconWeb;
    private int color=0;
    private Place p;
    private PlaceInfo pi;
    private View.OnClickListener OnCommentClick;
    private View.OnClickListener OnDealClick;

    ArrayList<Place> listData = new ArrayList<Place>();

    public PlaceDetailFragment() {}

    @SuppressLint("ValidFragment")
    public PlaceDetailFragment(int color){
        this.color=color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_detail,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // ((App)getActivity().getApplication()).track(App.ANALYSIS_PLACE_DETAILS);
        ((RelativeLayout)getView().findViewById(R.id.main_place_detail)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        p=Place.FOR_DETAIL;
        pi=new PlaceInfo();


        topInfo=(TextView)getView().findViewById(R.id.top_info_place_detail);
        description=(TextView)getView().findViewById(R.id.description_place_detail);
        address=(TextView)getView().findViewById(R.id.address_place_detail);
        comment=(TextView)getView().findViewById(R.id.comment_comment_place_detail);
        commentInfo=(TextView)getView().findViewById(R.id.comment_info_place_detail);
        commentView=(ImageView)getView().findViewById(R.id.comment_image_place_detail);
        dealImage=(ImageView)getView().findViewById(R.id.deal_image_place_detail);
        dealTitle=(TextView)getView().findViewById(R.id.deal_title_place_detail);
        dealDate=(TextView)getView().findViewById(R.id.deal_date_place_detail);
        dealText=(TextView)getView().findViewById(R.id.deal_text_place_detail);


        // 2- We will call api
        String url2 = App.SitePath+"api/places.php?op=info&plc_id=1";
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            JSONArray jArray = response.getJSONArray("content");
                            FavoriteCategory f = new FavoriteCategory();


                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final PlaceInfo pi = new PlaceInfo();

                                pi.setDescription(obj.getString("plc_meta_description"));
                                pi.setPlc_website(obj.getString("plc_website"));


                                if(pi.getDescription().length()>0) {
                                    description.setVisibility(View.VISIBLE);
                                    description.setText(pi.getDescription() + "");
                                }else {
                                    description.setVisibility(View.GONE);
                                }



                            }

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






        if(p.photos.size()>0){
            ((TextView)getView().findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.VISIBLE);
            /*SlideShowView slide=(SlideShowView)getView().findViewById(R.id.slide_show_place_detail);
            String[] arr=new String[p.photos.size()];
            for(int i=0;i<p.photos.size();i++){
                arr[i]=p.photos.get(i).url;
            }
            slide.start(arr);*/
            MyViewPager pager=(MyViewPager)getView().findViewById(R.id.pager_place_detail);
            PhotoAdapter adapter=new PhotoAdapter(getActivity(),p.photos);
            pager.setAdapter(adapter);
        }else{
            ((TextView)getView().findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.GONE);
            ((LinearLayout)getView().findViewById(R.id.photos_container_place_detail)).setVisibility(View.GONE);
            ((ImageView)getView().findViewById(R.id.no_image_palce_detail)).setImageResource(R.drawable.place_detail_image_placeholder);
        }

        if(p.isOpen()) {
            topInfo.setText(p.name + " : " + getResources().getString(R.string.openbuttonlabel));
        }else{
            topInfo.setText(p.name + " : " + getResources().getString(R.string.closedbuttonlabel));
        }



        if(p.address!=null && p.address.length()>0){
            ((TextView)getView().findViewById(R.id.address_title_text_place_detail)).setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            address.setText(p.address);
        }else {
            ((TextView)getView().findViewById(R.id.address_title_text_place_detail)).setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }

        if(p.comments.size()>0) {
            ((TextView)getView().findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.VISIBLE);
            String t = p.comments.get(0).text;
            if (t.length() > 100)
                comment.setText(t.substring(0, 197) + "...");
            else
                comment.setText(t);
            commentInfo.setText(p.comments.get(0).getCreatedDate() + " , " + p.comments.get(0).name);
            Picasso.with(getActivity())
                    .load(Gravatar.getURL(p.comments.get(0).email))
                    .placeholder(R.drawable.placeholder_user)
                    .fit()
                    .into(commentView);
            if(OnCommentClick!=null){
                ((RelativeLayout)getView().findViewById(R.id.comment_container_place_detail))
                        .setOnClickListener(OnCommentClick);
            }
        }else{
            ((TextView)getView().findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.GONE);
            ((RelativeLayout)getView().findViewById(R.id.comment_container_place_detail)).setVisibility(View.GONE);
        }

        photos=new ImageView[4];
        photos[0]=(ImageView)getView().findViewById(R.id.photo1_place_detail);
        photos[1]=(ImageView)getView().findViewById(R.id.photo2_place_detail);
        photos[2]=(ImageView)getView().findViewById(R.id.photo3_place_detail);
        photos[3]=(ImageView)getView().findViewById(R.id.photo4_place_detail);

        for(int i=0;i<p.photos.size();i++){
            if(i==4)
                break;

            photos[i].setOnClickListener(openPhotoActivity);
            Picasso.with(getActivity())
                   .load(p.photos.get(i).url)
                   .placeholder(getResources().getDrawable(R.drawable.placeholder_photo_thumbnail))
                   .fit()
                   .into(photos[i]);
        }

        if(p.deals.size()>0){
            ((TextView)getView().findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.VISIBLE);
            Deal d=p.deals.get(0);
            dealTitle.setText(d.title);
            dealDate.setText(d.getDates());
            dealText.setText(d.description);
            Picasso.with(getActivity())
                    .load(d.photo)
                    .placeholder(getResources().getDrawable(R.drawable.placeholder_photo_thumbnail))
                    .fit()
                    .into(dealImage);
            if(OnDealClick!=null){
                ((RelativeLayout)getView().findViewById(R.id.deal_container_place_detail))
                        .setOnClickListener(OnDealClick);
            }
        }else{
            ((TextView)getView().findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.GONE);
            ((RelativeLayout)getView().findViewById(R.id.deal_container_place_detail)).setVisibility(View.GONE);
        }

        int faceColor=p.facebook.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int mapColor=color;
        int phoneColor=p.phone.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int twitColor=p.twitter.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int webColor=p.web.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;

        SVG face= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_facebook,App.SVGOldColor,faceColor);
        SVG map= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_map,App.SVGOldColor,mapColor);
        SVG phone= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_phone,App.SVGOldColor,phoneColor);
        SVG twit= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_twitter,App.SVGOldColor,twitColor);
        SVG web= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_webpage,App.SVGOldColor,webColor);

        iconFace=(ImageView)getView().findViewById(R.id.footer2_place_filter);
        iconWeb=(ImageView)getView().findViewById(R.id.footer1_place_filter);
        iconPhone=(ImageView)getView().findViewById(R.id.footer4_place_filter);
        iconTwit=(ImageView)getView().findViewById(R.id.footer3_place_filter);
        iconMap=(ImageView)getView().findViewById(R.id.footer5_place_filter);

        ViewCompat.setLayerType(iconFace, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(iconWeb, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(iconPhone, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(iconTwit, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(iconMap, ViewCompat.LAYER_TYPE_SOFTWARE, null);

        iconFace.setImageDrawable(face.createPictureDrawable());
        iconWeb.setImageDrawable(web.createPictureDrawable());
        iconPhone.setImageDrawable(phone.createPictureDrawable());
        iconTwit.setImageDrawable(twit.createPictureDrawable());
        iconMap.setImageDrawable(map.createPictureDrawable());

        if(p.web.length()>0)
            iconWeb.setOnClickListener(toWeb);
        if(p.facebook.length()>0)
            iconFace.setOnClickListener(toFace);
        if(p.twitter.length()>0)
            iconTwit.setOnClickListener(toTwit);
        if(p.phone.length()>0)
            iconPhone.setOnClickListener(toPhone);
        iconMap.setOnClickListener(toMapView);
    }

    public void setOnCommentClick(View.OnClickListener onCommentClick) {
        this.OnCommentClick = onCommentClick;
        if(getView()!=null){
            ((RelativeLayout)getView().findViewById(R.id.comment_container_place_detail))
                    .setOnClickListener(OnCommentClick);
        }
    }

    public void setOnDealClick(View.OnClickListener onDealClick) {
        this.OnDealClick = onDealClick;
        if(getView()!=null){
            ((RelativeLayout)getView().findViewById(R.id.deal_container_place_detail))
                    .setOnClickListener(onDealClick);
        }
    }




    View.OnClickListener openPhotoActivity=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), PhotoActivity.class);
            for(int i=0;i<photos.length;i++){
                if(photos[i].getId()==v.getId()){
                    in.putExtra(PhotoActivity.START_POSTİON,i);
                }
            }
            startActivity(in);
        }
    };

    View.OnClickListener toMapView=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), MapsActivity.class);
            in.putExtra(MapsActivity.HAVE_PLACE,true);
            in.putExtra(MapsActivity.LATITUDE,p.getLatitude());
            in.putExtra(MapsActivity.LONGTİTUDE,p.getLongitude());
            in.putExtra(Place.NAME,p.name);
            in.putExtra(PlaceActivity.COLOR, color);
            startActivity(in);
        }
    };

    View.OnClickListener toWeb=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, p.web);
            in.putExtra(WebActivity.TITLE, p.name);
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE,p.web);
            startActivity(in);
        }
    };

    View.OnClickListener toFace=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, p.facebook);
            in.putExtra(WebActivity.TITLE, p.name);
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE,"Facebook");
            startActivity(in);
        }
    };

    View.OnClickListener toTwit=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, p.twitter);
            in.putExtra(WebActivity.TITLE, p.name);
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE,"Twitter");
            startActivity(in);
        }
    };

    View.OnClickListener toPhone=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(Intent.ACTION_VIEW);
            in.setData(Uri.parse("tel:"+p.phone));
            startActivity(in);
        }
    };
}
