/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MapsActivity;
import com.youbaku.apps.placesnear.photo.MyViewPager;
import com.youbaku.apps.placesnear.photo.Photo;
import com.youbaku.apps.placesnear.photo.PhotoActivity;
import com.youbaku.apps.placesnear.photo.PhotoAdapter;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.web.WebActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlaceDetailFragment extends Fragment{
    private TextView topInfo;
    private TextView description;
    private TextView address;
    private TextView comment;
    private TextView commentInfo;
    private ImageView commentView;
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
    private Button reservBtn;
    private int color=0;
    private Place p = new Place();;
    private PlaceInfo pi;
    private View.OnClickListener OnCommentClick;
    private View.OnClickListener OnDealClick;
    ImageLoader mImageLoader;
    private BookPlaceFragment bookPlaceFrg;


// TODO: Place object for this class & holds all response data
        private Place activePlace = new Place();
// TODO: Gallery small images
        private NetworkImageView[] photos;
// TODO: ProgressDialog for async task (json request)
        private ProgressDialog progress;
// TODO: Needs for general operations
        private View view;
        private Activity activity;


    public PlaceDetailFragment() {}

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

        ((RelativeLayout)view.findViewById(R.id.main_place_detail)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        activity = this.getActivity();

        activePlace = Place.FOR_DETAIL;
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
        reservBtn=(Button)view.findViewById(R.id.doReserveBtn);
        reservBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookPlaceFrg=new BookPlaceFragment();
                getFragmentManager().beginTransaction().addToBackStack("bookplace").replace(R.id.main_activity_place_detail, bookPlaceFrg).commit();

            }
        });


        // Start Progress Bar before api request
        progress = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        progress.setTitle("Getting Place");
        progress.setMessage("Loading place info");
        progress.show();


        /**
         * @developer   Kemal Sami KARACA
         * @modified    30/07/2015
         * @desc
         *              This request provides Place detail information. Therefore view parts SHOULD be
         *              updated on onResponse() method.
         *
         *
         */
        String url = App.SitePath+"api/places.php?token="+ App.getYoubakuToken() +"&apikey="+ App.getYoubakuAPIKey() +"&op=info&plc_id="+Place.ID;
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                            if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {

                                    JSONObject responseContent = App.getJsonObjectValueFromJsonObject(response, App.RESULT_CONTENT);
                                    if(responseContent!=null){

                                        //---------------------------------------------------------------------------
                                        //----- ----- ----- ----- ----- SET RESPONSE VALUE ----- ----- ----- ----- -----
                                        //---------------------------------------------------------------------------

                                        activePlace.setId(App.getJsonValueIfExist(responseContent, Place.PLC_ID));
                                        activePlace.setName(App.getJsonValueIfExist(responseContent, Place.PLC_NAME));
                                        activePlace.setImgUrl(App.getJsonValueIfExist(responseContent, Place.PLC_HEADER_IMAGE));
                                        activePlace.setAddress(App.getJsonValueIfExist(responseContent, Place.PLC_ADDRESS));
                                        activePlace.setEmail(App.getJsonValueIfExist(responseContent, Place.PLC_EMAIL));
                                        activePlace.setWeb(App.getJsonValueIfExist(responseContent, Place.PLC_WEBSITE));
                                        activePlace.setPhone(App.getJsonValueIfExist(responseContent, Place.PLC_CONTACT));
                                        activePlace.setOpen(App.getJsonValueIfExist(responseContent, Place.PLC_INTIME));
                                        activePlace.setClose(App.getJsonValueIfExist(responseContent, Place.PLC_OUTTIME));
                                        activePlace.setPlaceIsOpen(App.getJsonValueIfExist(responseContent, Place.PLC_IS_OPEN));

                                        double latitude = Double.parseDouble(App.getJsonValueIfExist(responseContent, Place.PLC_LATITUDE));
                                        double longitude = Double.parseDouble(App.getJsonValueIfExist(responseContent, Place.PLC_LONGITUDE));
                                        activePlace.setLocation(latitude, longitude);

                                        String htmlToString = String.valueOf(Html.fromHtml(Html.fromHtml(App.getJsonValueIfExist(responseContent, Place.PLC_INFO)).toString()));
                                        activePlace.setDescription(htmlToString);

                                        String isActive = App.getJsonValueIfExist(responseContent, Place.PLC_IS_ACTIVE);
                                        activePlace.setIsActive(isActive.equalsIgnoreCase("1"));


                                        // Get gallery and prepare to upload image url
                                        JSONArray placeGallery = App.getJsonArayIfExist(responseContent , "gallery");
                                        activePlace.setPhotos(new ArrayList<Photo>());

                                        // Set gallery array to "activePlace" variable
                                        for (int i = 0; placeGallery!=null && i<placeGallery.length(); i++) {

                                            // Get gallery object
                                            JSONObject gallery = App.getJsonArrayValueIfExist(placeGallery , i);

                                            // Set gallery object
                                            final Photo photo = new Photo();
                                            photo.setUrl(App.getJsonValueIfExist(gallery, "plc_gallery_media"));
                                            activePlace.getPhotos().add(photo);
                                        }


                                        // ----- ----- -----
                                        // GET ratings
                                        double averageRating = 0;
                                        JSONArray ratings = App.getJsonArayIfExist(responseContent, Place.RATING);
                                        activePlace.setComments(new ArrayList<Comment>());
                                        for (int j = 0; ratings != null && j < ratings.length(); j++) {
                                            Comment comment = new Comment();
                                            JSONObject jsonComment = App.getJsonArrayValueIfExist(ratings,j);
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            try {
                                                // --GUPPY COMMENT IMPORTANT--
                                                // -- Comments variable change to encapsulation --
                                                comment.created     = format.parse(App.getJsonValueIfExist(jsonComment, Place.RATING_CREATED_DATE));
                                                comment.text        = App.getJsonValueIfExist(jsonComment, Place.RATING_COMMENT);
                                                comment.comment_id  = App.getJsonValueIfExist(jsonComment, Place.RATING_ID);
                                                comment.rating      = Double.parseDouble(App.getJsonValueIfExist(jsonComment, Place.RATING_RATING));
                                                comment.name        = App.getJsonValueIfExist(jsonComment, Place.RATING_USR_USERNAME);
                                                comment.user_img    = App.getJsonValueIfExist(jsonComment, Place.RATING_USR_PROFILE_PICTURE);

                                                averageRating += comment.rating;

                                                activePlace.getComments().add(comment);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }



                                        //---------------------------------------------------------------------------
                                        //----- ----- ----- ----- ----- UPDATE VIEW PART ----- ----- ----- ----- -----
                                        //---------------------------------------------------------------------------

                                        try{

                                                // Set Title Info part
                                                topInfo.setText(activePlace.getName() + " : " + (activePlace.getPlaceIsOpen().equalsIgnoreCase("1") ? getResources().getString(R.string.openbuttonlabel) : getResources().getString(R.string.closedbuttonlabel)));


                                                // ----- ----- -----
                                                // Set main images part
                                                if(activePlace.getPhotos().size()>0){
                                                    ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.VISIBLE);

                                                    MyViewPager pager=(MyViewPager)view.findViewById(R.id.pager_place_detail);
                                                    PhotoAdapter adapter=new PhotoAdapter(getActivity(), activePlace.getPhotos());
                                                    pager.setAdapter(adapter);
                                                }else{
                                                    ((TextView)view.findViewById(R.id.photo_title_text_place_detail)).setVisibility(View.GONE);
                                                    ((LinearLayout)view.findViewById(R.id.photos_container_place_detail)).setVisibility(View.GONE);
                                                    ((ImageView)view.findViewById(R.id.no_image_palce_detail)).setImageResource(R.drawable.place_detail_image_placeholder);
                                                }


                                                // ----- ----- -----
                                                // Set Description part
                                                if(activePlace.getDescription().length()>0) {
                                                    description.setVisibility(View.VISIBLE);
                                                    description.setText(activePlace.getDescription() + "");
                                                }else {
                                                    description.setVisibility(View.GONE);
                                                }


                                                // ----- ----- -----
                                                // Set Address part
                                                if(activePlace.getAddress().length()>0){
                                                    ((TextView)view.findViewById(R.id.address_title_text_place_detail)).setVisibility(View.VISIBLE);
                                                    address.setVisibility(View.VISIBLE);
                                                    address.setText(activePlace.getAddress());
                                                }else {
                                                    ((TextView)view.findViewById(R.id.address_title_text_place_detail)).setVisibility(View.GONE);
                                                    address.setVisibility(View.GONE);
                                                }


                                                // ----- ----- -----
                                                // Set Comment part
                                                if(activePlace.getComments().size()>0) {

                                                    ((TextView)view.findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.VISIBLE);

                                                    // Only last comment seems
                                                    String lastComment = activePlace.getComments().get(0).text;
                                                    if (lastComment.length() > 100)
                                                        comment.setText(lastComment.substring(0, 99) + "...");
                                                    else
                                                        comment.setText(lastComment);

                                                    commentInfo.setText(activePlace.getComments().get(0).getCreatedDate() + " , by  " + activePlace.getComments().get(0).getName());
                                                    boolean isUserImageExist = activePlace.getComments().get(0).getUser_img()!=null && activePlace.getComments().get(0).getUser_img()!="" && (!activePlace.getComments().get(0).getUser_img().equalsIgnoreCase("-1"));
                                                    if(!isUserImageExist){
                                                        commentView.setImageResource(R.drawable.placeholder_user);
                                                    }
                                                    else{
                                                        Picasso.with(getActivity())
                                                                .load(activePlace.getComments().get(0).getUser_img())
                                                                .placeholder(R.drawable.placeholder_user)
                                                                .fit()
                                                                .into(commentView);

                                                    }

                                                    if(OnCommentClick!=null){
                                                        ((RelativeLayout)view.findViewById(R.id.comment_container_place_detail))
                                                                .setOnClickListener(OnCommentClick);
                                                    }
                                                }else{
                                                    ((TextView)view.findViewById(R.id.comment_title_text_place_detail)).setVisibility(View.GONE);
                                                    ((RelativeLayout)view.findViewById(R.id.comment_container_place_detail)).setVisibility(View.GONE);
                                                }


                                                // ----- ----- -----
                                                // Set small images part
                                                photos=new NetworkImageView[4];
                                                photos[0]=(NetworkImageView)view.findViewById(R.id.photo1_place_detail);
                                                photos[1]=(NetworkImageView)view.findViewById(R.id.photo2_place_detail);
                                                photos[2]=(NetworkImageView)view.findViewById(R.id.photo3_place_detail);
                                                photos[3]=(NetworkImageView)view.findViewById(R.id.photo4_place_detail);

                                                for(int i=0; i<activePlace.getPhotos().size() && i<photos.length ;i++){
                                                    photos[i].setOnClickListener(openPhotoActivity);
                                                    photos[i].setImageUrl(
                                                            App.SitePath+"uploads/places_images/large/"+ activePlace.getPhotos().get(i).getUrl(),
                                                            VolleySingleton.getInstance().getImageLoader());
                                                }


                                                // ----- ----- ----- --GUPPY COMMENT IMPORTANT--
                                                // Set deals part
//                                                if(activePlace.deals.size()>0){
//                                                    ((TextView)view.findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.VISIBLE);
//                                                    Deal d=activePlace.deals.get(0);
//                                                    dealTitle.setText(d.title);
//                                                    dealDate.setText(d.getDates());
//                                                    dealText.setText(d.description);
//                                                    Picasso.with(getActivity())
//                                                            .load(d.photo)
//                                                            .placeholder(getResources().getDrawable(R.drawable.placeholder_photo_thumbnail))
//                                                            .fit()
//                                                            .into(dealImage);
//                                                    if(OnDealClick!=null){
//                                                        ((RelativeLayout)view.findViewById(R.id.deal_container_place_detail))
//                                                                .setOnClickListener(OnDealClick);
//                                                    }
//                                                }else{
//                                                    ((TextView)view.findViewById(R.id.deal_title_text_place_detail)).setVisibility(View.GONE);
//                                                    ((RelativeLayout)view.findViewById(R.id.deal_container_place_detail)).setVisibility(View.GONE);
//                                                }


                                                // ----- ----- -----
                                                // Set Icons
                                                int faceColor=activePlace.facebook.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
                                                int mapColor=color;
                                                int phoneColor= activePlace.getPhone().length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
                                                int twitColor=activePlace.twitter.length()<1 ? Color.parseColor(App.SVGPassiveColor):color;
                                                int webColor= activePlace.getWeb().length()<1 ? Color.parseColor(App.SVGPassiveColor):color;

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

                                                if(activePlace.getWeb().length()>0)
                                                    iconWeb.setOnClickListener(toWeb);
                                                if(activePlace.facebook.length()>0)
                                                    iconFace.setOnClickListener(toFace);
                                                if(activePlace.twitter.length()>0)
                                                    iconTwit.setOnClickListener(toTwit);
                                                if(activePlace.getPhone().length()>0)
                                                    iconPhone.setOnClickListener(toPhone);
                                                iconMap.setOnClickListener(toMapView);

                                        }catch (NullPointerException e){
                                            Log.d(getClass().getName() , "View Destroyed error");
                                        }

                                    }


                            }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PERMISSION")){

                                //We should get new apikey and token
                                RegisterAPI.callRegister(activity);

                                //Error Info
                                Log.e("531-FAILURE_PERMISSION" , "PlaceDetailFragment->onViewCreated-> api key missing error");
                                Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();

                            }


                            progress.dismiss();

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
            for(int i=0;photos!=null && i<photos.length;i++){
               if(photos[i].getId()==v.getId()){
                    in.putExtra(PhotoActivity.START_POSTİON,i);
//                    Toast.makeText(getActivity(), "View Id :" +photos.length, Toast.LENGTH_LONG).show();
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
            in.putExtra(MapsActivity.LATITUDE, activePlace.getLatitude());
            in.putExtra(MapsActivity.LONGTİTUDE, activePlace.getLongitude());
            in.putExtra(Place.NAME,activePlace.getName());
            in.putExtra(PlaceActivity.COLOR, color);
            startActivity(in);
        }
    };

    View.OnClickListener toWeb=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, activePlace.getWeb());
            in.putExtra(WebActivity.TITLE, activePlace.getName());
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE, activePlace.getWeb());
            startActivity(in);
        }
    };

    View.OnClickListener toFace=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, activePlace.facebook);
            in.putExtra(WebActivity.TITLE, activePlace.getName());
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE,"Facebook");
            startActivity(in);
        }
    };

    View.OnClickListener toTwit=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(getActivity(), WebActivity.class);
            in.putExtra(WebActivity.URL, activePlace.twitter);
            in.putExtra(WebActivity.TITLE, activePlace.getName());
            in.putExtra(WebActivity.COLOR,color);
            in.putExtra(WebActivity.SUBTITLE,"Twitter");
            startActivity(in);
        }
    };

    View.OnClickListener toPhone=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in=new Intent(Intent.ACTION_VIEW);
            in.setData(Uri.parse("tel:" + activePlace.getPhone()));
            startActivity(in);
        }
    };
}
