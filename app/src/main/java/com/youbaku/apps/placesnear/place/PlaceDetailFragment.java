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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.Gravatar;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MapsActivity;
import com.youbaku.apps.placesnear.photo.MyViewPager;
import com.youbaku.apps.placesnear.photo.Photo;
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
    private NetworkImageView[] photos;
    private ArrayList<Photo> arr;
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
    ImageLoader mImageLoader;

    ArrayList<Place> listData = new ArrayList<Place>();

    public PlaceDetailFragment() {}
    private View view;

    @SuppressLint("ValidFragment")
    public PlaceDetailFragment(int color){
        this.color=color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.place_detail,container,false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // ((App)getActivity().getApplication()).track(App.ANALYSIS_PLACE_DETAILS);
        ((RelativeLayout)view.findViewById(R.id.main_place_detail)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        p=Place.FOR_DETAIL;
        pi=new PlaceInfo();


        topInfo=(TextView)view.findViewById(R.id.top_info_place_detail);
        description=(TextView)view.findViewById(R.id.description_place_detail);
        address=(TextView)view.findViewById(R.id.address_place_detail);
        comment=(TextView)view.findViewById(R.id.comment_comment_place_detail);
        commentInfo=(TextView)view.findViewById(R.id.comment_info_place_detail);
        commentView=(ImageView)view.findViewById(R.id.comment_image_place_detail);
        dealImage=(ImageView)view.findViewById(R.id.deal_image_place_detail);
        dealTitle=(TextView)view.findViewById(R.id.deal_title_place_detail);
        dealDate=(TextView)view.findViewById(R.id.deal_date_place_detail);
        dealText=(TextView)view.findViewById(R.id.deal_text_place_detail);


        // 2- We will call api
        String url2 = App.SitePath+"api/places.php?op=info&plc_id="+Place.ID;
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            JSONArray jArray = response.getJSONObject("content").getJSONArray("gallery");
                            FavoriteCategory f = new FavoriteCategory();
                            p.photos=new ArrayList<>();

                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final Photo photo = new Photo();
                                photo.url=obj.getString("plc_gallery_media");
                                p.photos.add(photo);

                                //Toast.makeText(getActivity(),"Gallery are: "+p.photos.get(i).url,Toast.LENGTH_LONG).show();

                            }


                            if(p.photos.size()>0){
                                ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.VISIBLE);

                                MyViewPager pager=(MyViewPager)view.findViewById(R.id.pager_place_detail);
                                PhotoAdapter adapter=new PhotoAdapter(getActivity(),p.photos);
                                pager.setAdapter(adapter);
                            }else{
                                ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.GONE);
                                ((LinearLayout)view.findViewById(R.id.photos_container_place_detail)).setVisibility(View.GONE);
                                ((ImageView)view.findViewById(R.id.no_image_palce_detail)).setImageResource(R.drawable.place_detail_image_placeholder);
                            }
                            photos=new NetworkImageView[4];
                            photos[0]=(NetworkImageView)view.findViewById(R.id.photo1_place_detail);
                            photos[1]=(NetworkImageView)view.findViewById(R.id.photo2_place_detail);
                            photos[2]=(NetworkImageView)view.findViewById(R.id.photo3_place_detail);
                            photos[3]=(NetworkImageView)view.findViewById(R.id.photo4_place_detail);

                            for(int i=0;i<p.photos.size();i++){
                                if(i==4)
                                    break;

                                photos[i].setOnClickListener(openPhotoActivity);


                                String url = App.SitePath+"uploads/places_images/large/"+p.photos.get(i).url; // URL of the image
                                mImageLoader = VolleySingleton.getInstance().getImageLoader();
                                photos[i].setImageUrl(url, mImageLoader);
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



        /*if(p.photos.size()>0){
            ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.VISIBLE);
            *//*SlideShowView slide=(SlideShowView)view.findViewById(R.id.slide_show_place_detail);
            String[] arr=new String[p.photos.size()];
            for(int i=0;i<p.photos.size();i++){
                arr[i]=p.photos.get(i).url;
            }
            slide.start(arr);*//*
            MyViewPager pager=(MyViewPager)view.findViewById(R.id.pager_place_detail);
            PhotoAdapter adapter=new PhotoAdapter(getActivity(),p.photos);
            pager.setAdapter(adapter);
        }else{
            ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.GONE);
            ((LinearLayout)view.findViewById(R.id.photos_container_place_detail)).setVisibility(View.GONE);
            ((ImageView)view.findViewById(R.id.no_image_palce_detail)).setImageResource(R.drawable.place_detail_image_placeholder);
        }*/

        if(p.isOpen()) {
            topInfo.setText(p.name + " : " + getResources().getString(R.string.openbuttonlabel));
        }else{
            topInfo.setText(p.name + " : " + getResources().getString(R.string.closedbuttonlabel));
        }

        if(p.getDescription().length()>0) {
            description.setVisibility(View.VISIBLE);
            description.setText(p.getDescription() + "");
        }else {
            description.setVisibility(View.GONE);
        }

        if(p.getAddress() !=null && p.getAddress().length()>0){
            ((TextView)view.findViewById(R.id.address_title_text_place_detail)).setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            address.setText(p.getAddress());
        }else {
            ((TextView)view.findViewById(R.id.address_title_text_place_detail)).setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }

        if(p.comments.size()>0) {
            ((TextView)view.findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.VISIBLE);
            String t = p.comments.get(0).text;
            if (t.length() > 100)
                comment.setText(t.substring(0, 197) + "...");
            else
                comment.setText(t);
            commentInfo.setText(p.comments.get(0).getCreatedDate() + " , by user " + p.comments.get(0).name);
            Picasso.with(getActivity())
                    .load(Gravatar.getURL(p.comments.get(0).email))
                    .placeholder(R.drawable.placeholder_user)
                    .fit()
                    .into(commentView);
            if(OnCommentClick!=null){
                ((RelativeLayout)view.findViewById(R.id.comment_container_place_detail))
                        .setOnClickListener(OnCommentClick);
            }
        }else{
            ((TextView)view.findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.GONE);
            ((RelativeLayout)view.findViewById(R.id.comment_container_place_detail)).setVisibility(View.GONE);
        }

        /*photos=new ImageView[4];
        photos[0]=(ImageView)view.findViewById(R.id.photo1_place_detail);
        photos[1]=(ImageView)view.findViewById(R.id.photo2_place_detail);
        photos[2]=(ImageView)view.findViewById(R.id.photo3_place_detail);
        photos[3]=(ImageView)view.findViewById(R.id.photo4_place_detail);

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
*/
        if(p.deals.size()>0){
            ((TextView)view.findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.VISIBLE);
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
                ((RelativeLayout)view.findViewById(R.id.deal_container_place_detail))
                        .setOnClickListener(OnDealClick);
            }
        }else{
            ((TextView)view.findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.GONE);
            ((RelativeLayout)view.findViewById(R.id.deal_container_place_detail)).setVisibility(View.GONE);
        }

        int faceColor=p.facebook.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int mapColor=color;
        int phoneColor= p.getPhone().length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int twitColor=p.twitter.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
        int webColor= p.getWeb().length()<1 ? Color.parseColor(App.SVGPassiveColor):color;

        SVG face= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_facebook,App.SVGOldColor,faceColor);
        SVG map= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_map,App.SVGOldColor,mapColor);
        SVG phone= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_phone,App.SVGOldColor,phoneColor);
        SVG twit= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_twitter,App.SVGOldColor,twitColor);
        SVG web= SVGParser.getSVGFromResource(getActivity().getResources(),R.raw.icon_webpage,App.SVGOldColor,webColor);

        iconFace=(ImageView)view.findViewById(R.id.footer2_place_filter);
        iconWeb=(ImageView)view.findViewById(R.id.footer1_place_filter);
        iconPhone=(ImageView)view.findViewById(R.id.footer4_place_filter);
        iconTwit=(ImageView)view.findViewById(R.id.footer3_place_filter);
        iconMap=(ImageView)view.findViewById(R.id.footer5_place_filter);

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

        if(p.getWeb().length()>0)
            iconWeb.setOnClickListener(toWeb);
        if(p.facebook.length()>0)
            iconFace.setOnClickListener(toFace);
        if(p.twitter.length()>0)
            iconTwit.setOnClickListener(toTwit);
        if(p.getPhone().length()>0)
            iconPhone.setOnClickListener(toPhone);
        iconMap.setOnClickListener(toMapView);
    }

    public void setOnCommentClick(View.OnClickListener onCommentClick) {
        this.OnCommentClick = onCommentClick;
        if(view!=null){
            ((RelativeLayout)view.findViewById(R.id.comment_container_place_detail))
                    .setOnClickListener(OnCommentClick);
        }
    }

    public void setOnDealClick(View.OnClickListener onDealClick) {
        this.OnDealClick = onDealClick;
        if(view!=null){
            ((RelativeLayout)view.findViewById(R.id.deal_container_place_detail))
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
                    Toast.makeText(getActivity(), "View Id :" +photos.length, Toast.LENGTH_LONG).show();
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
            in.putExtra(WebActivity.URL, p.getWeb());
            in.putExtra(WebActivity.TITLE, p.name);
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE, p.getWeb());
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
            in.setData(Uri.parse("tel:"+ p.getPhone()));
            startActivity(in);
        }
    };
}
