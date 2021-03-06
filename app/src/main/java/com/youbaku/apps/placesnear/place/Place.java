/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.MyApplication;
import com.youbaku.apps.placesnear.adapter.Adapter;
import com.youbaku.apps.placesnear.apicall.RegisterAPI;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.photo.Photo;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.Deal;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Place {

    // ----- ----- ----- ----- ----- ----- ----- ----- -----
    // ----- ----- GENERIC FETCHING PLACES PARAMETERS ----- -----
    // ----- ----- ----- ----- ----- ----- ----- ----- -----
    public static final String PLC_ID = "plc_id";
    public static final String PLC_NAME = "plc_name";
    public static final String PLC_EMAIL = "plc_email";
    public static final String PLC_WEBSITE = "plc_website";
    public static final String PLC_HEADER_IMAGE = "plc_header_image";
    public static final String PLC_ADDRESS = "plc_address";
    public static final String PLC_CONTACT = "plc_contact";
    public static final String PLC_LATITUDE = "plc_latitude";
    public static final String PLC_LONGITUDE = "plc_longitude";
    public static final String PLC_INTIME = "plc_intime";
    public static final String PLC_OUTTIME = "plc_outtime";
    public static final String PLC_IS_OPEN = "plc_is_open";
    public static final String PLC_INFO = "plc_info";
    public static final String PLC_IS_ACTIVE = "plc_is_active";
    public static final String RATING = "rating";
    public static final String RATING_CREATED_DATE = "places_rating_created_date";
    public static final String RATING_COMMENT = "place_rating_comment";
    public static final String RATING_ID = "place_rating_id";
    public static final String RATING_RATING = "place_rating_rating";
    public static final String RATING_USR_USERNAME = "usr_username";
    public static final String RATING_USR_PROFILE_PICTURE = "usr_profile_picture";


    public static String ID = "plc_id";
    public static String EMAIL = "plc_email";
    public static final String NAME = "plc_name";
    public static final String PHOTO = "plc_header_image";
    public static final String WEBPAGE = "plc_website";
    public static final String POSITION = "position";
    public static final String ADDRESS = "plc_address";
    public static final String PHONE = "plc_contact";
    public static final String OPENHOUR = "plc_intime";
    public static final String CLOSEHOUR = "plc_outtime";
    public static final String LIKES = "likescount";
    public static final String TWITTER = "twitter";
    public static final String CATEGORY = "category";
    public static final String ISACTIVE = "isactive";



    public static final String DESCRIPTION = "description";
    public static final String FACEBOOK = "facebook";
    public static final String PLACE = "place";
    public static final String DISTANCE = "distance";
    public static Place FOR_DETAIL;

    public String imgUrl;
    public String id = "";
    public String name = "";
    private String plc_is_open="";
    private String phone = "";
    public String category = "";
    private String address = "";
    private String web = "";
    public String email = "";
    private String description = "";
    private boolean isActive = true;
    private double longitude = 0;
    private double latitude = 0;
    private boolean locationSet = false;
    private float[] distance = new float[1];

    public boolean isFavourite = false;
    public String color = "";
    public int likes = 0;
    public double rating = 10.0;
    public String facebook = "";
    public String twitter = "";
    public Bitmap photo;
    public File file;
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    public ArrayList<Deal> deals = new ArrayList<Deal>();
    private ArrayList<Photo> photos = new ArrayList<Photo>();
    private String open = "";
    private String close = "";
    public boolean liked = false;

    public static ArrayList<Place> placesArrayListSearch = new ArrayList<>();
    public static ArrayList<Place> placesArrayListPopular = new ArrayList<>();
    public static ArrayList<Place> placesArrayListNearMe = new ArrayList<>();
    public static ArrayList<Place> placesArrayListFilter = new ArrayList<>();

    public static Map<String, Place> placesListNearMe = new HashMap<>();


    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        locationSet = true;
    }

    public boolean isOpen() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date now = format.parse(format.format(new Date()));
            Date open = format.parse(this.getOpen());
            Date close = format.parse(this.getClose());
            if (!now.after(open) || !now.before(close))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }





    //---------------------------------- ENCAPSULATION -----------------------------------/
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isLocationSet() {
        return locationSet;
    }

    public static String getID() {
        return ID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public float[] getDistance() {
        return distance;
    }

    public void setDistance(float[] distance) {
        this.distance = distance;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    //---------------------------------- ENCAPSULATION -----------------------------------/










    //---------------------------------- REQUEST PART -----------------------------------/

    // --GUPPY COMMENT IMPORTANT--
    // PlaceActivity checkDownloads() methodu entegre edilecek

    /**
     * @param METHOD            Request.Method.GET || Request.Method.POST
     * @param URL
     * @param parameters
     * @param resultPlaceList   Burada generic place verileri çekilmektedir.
     * @param activity
     * @param adapter           This parameter can be any adapter but IT HAVE TO IMPLEMENT
     *                          com.youbaku.apps.placesnear.adapter.Adapter and ALSO NOT FORGET THE
     *                          OVERRIDE notifyDataSetChanged()
     *
     *                          Example :: PlaceAdapter
     */
    public static <T> void fetchGenericPlaceList(int METHOD, String URL, Map<String,String> parameters, final ArrayList<Place> resultPlaceList, final Activity activity, final T adapter) {

        if (activity!=null && !App.checkInternetConnection(activity) ) {
            App.showInternetError(activity);
            return;
        }


        // Request a json response
        JSONObject params = (parameters!=null) ? (new JSONObject(parameters)) : (null);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (METHOD, URL, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            resultPlaceList.clear();
                            // ----- ----- ----- ----- - ----- ----- ----- -----
                            // Check response is SUCCESS
                            // ----- ----- ----- ----- - ----- ----- ----- -----
                            if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {

                                try{

                                    JSONArray responsePlaceList = App.getJsonArayIfExist(response, App.RESULT_CONTENT);
                                    if(responsePlaceList!=null && responsePlaceList.length()>0) {

                                        for (int i = 0; i < responsePlaceList.length(); i++) {

                                            JSONObject jsonPlace = App.getJsonArrayValueIfExist(responsePlaceList,i);

                                            //Inflate places
                                            Place place = new Place();
                                            place.setId(App.getJsonValueIfExist(jsonPlace, Place.PLC_ID));
                                            place.setName(App.getJsonValueIfExist(jsonPlace, Place.PLC_NAME));
                                            place.setImgUrl(App.getJsonValueIfExist(jsonPlace, Place.PLC_HEADER_IMAGE));
                                            place.setAddress(App.getJsonValueIfExist(jsonPlace, Place.PLC_ADDRESS));
                                            place.setEmail(App.getJsonValueIfExist(jsonPlace, Place.PLC_EMAIL));
                                            place.setWeb(App.getJsonValueIfExist(jsonPlace, Place.PLC_WEBSITE));
                                            place.setPhone(App.getJsonValueIfExist(jsonPlace, Place.PLC_CONTACT));
                                            place.setOpen(App.getJsonValueIfExist(jsonPlace, Place.PLC_INTIME));
                                            place.setClose(App.getJsonValueIfExist(jsonPlace, Place.PLC_OUTTIME));

                                            double latitude = Double.parseDouble(App.getJsonValueIfExist(jsonPlace, Place.PLC_LATITUDE));
                                            double longitude = Double.parseDouble(App.getJsonValueIfExist(jsonPlace, Place.PLC_LONGITUDE));
                                            place.setLocation(latitude, longitude);

                                            String htmlToString = String.valueOf(Html.fromHtml(Html.fromHtml(App.getJsonValueIfExist(jsonPlace, Place.PLC_INFO)).toString()));
                                            place.setDescription(htmlToString);

                                            String isActive = App.getJsonValueIfExist(jsonPlace, Place.PLC_IS_ACTIVE);
                                            place.setIsActive(isActive.equalsIgnoreCase("1"));

                                            // GET ratings
                                            double averageRating = 0;
                                            JSONArray ratings = App.getJsonArayIfExist(jsonPlace, Place.RATING);
                                            for (int j = 0; ratings != null && j < ratings.length(); j++) {
                                                Comment comment = new Comment();
                                                JSONObject jsonComment = ratings.getJSONObject(j);
                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                                try {
                                                    // --GUPPY COMMENT IMPORTANT--
                                                    // -- Comments variable change to encapsulation --
                                                    Date date = format.parse(App.getJsonValueIfExist(jsonComment, Place.RATING_CREATED_DATE));
                                                    comment.created = date;
                                                    comment.text = App.getJsonValueIfExist(jsonComment, Place.RATING_COMMENT);
                                                    comment.comment_id = App.getJsonValueIfExist(jsonComment, Place.RATING_ID);
                                                    comment.rating = Double.parseDouble(App.getJsonValueIfExist(jsonComment, Place.RATING_RATING));
                                                    comment.name = App.getJsonValueIfExist(jsonComment, Place.RATING_USR_USERNAME);
                                                    comment.user_img = App.getJsonValueIfExist(jsonComment, Place.RATING_USR_PROFILE_PICTURE);

                                                    averageRating += comment.rating;

                                                    place.getComments().add(comment);

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            try {
                                                place.setRating(Double.parseDouble(App.getJsonValueIfExist(jsonPlace, "rating_avg").equalsIgnoreCase("-1") ? "0" : App.getJsonValueIfExist(jsonPlace, "rating_avg")));
                                            } catch (NullPointerException e) {
                                                place.setRating(0);
                                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Rating Return Null---" + e.getMessage());

                                                Log.e("---GUPPY---", "ratings return NULL");
                                            }


                                            // GET distance
                                            MyLocation my = MyLocation.getMyLocation(MyApplication.getAppContext());
                                            my.callHard();
                                            Location.distanceBetween(my.latitude, my.longitude, place.getLatitude(), place.getLongitude(), place.getDistance());


                                            //Put place to list
                                            try {
                                                resultPlaceList.add(place);
                                            } catch (NullPointerException e) {
                                                App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "getGenericPlaceList > resultPlaceList parameter is null---" + e.getMessage());
                                                Log.e("---GUPPY---", "getGenericPlaceList > resultPlaceList parameter is null");
                                            }

                                        }

                                    }else {
                                        Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Place Count 0 ");
                                        App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Place Count 0");

                                    }

                                }catch (JSONException e){
                                    Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Response Content JSONException");
                                    App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Response Content JSONException---" + e.getMessage());

                                    e.printStackTrace();
                                }

                            }else if(response.getString("status").equalsIgnoreCase("FAILURE_PERMISSION")){

                                //We should get new apikey and token
                                RegisterAPI.callRegister(activity);

                                //Error Info
                                Log.e("531-FAILURE_PERMISSION" , "Place->fetchGenericPlaceList-> api key missing error");
                                Toast.makeText(activity, "We are try to register again...", Toast.LENGTH_SHORT).show();

                            }else{
                                try{
                                    String resultStatus = response.getString("status");
                                    Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + resultStatus);
                                }catch (NullPointerException e){
                                    Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Status " + "NULL");
                                    App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Status RETURN NULL----" + e.getMessage());

                                }
                            }



                            // *********************************************************************
                            // *********************************************************************
                            // --GUPPY COMMENT UPDATE--
                            // -- Finally update Place List --
                            if (adapter != null) {
                                try {
                                    ((Adapter) adapter).setAdapterList(resultPlaceList);
                                    ((Adapter) adapter).notifyDataSetChanged();
                                }catch (ClassCastException e){
                                    Log.e("---GUPPY---", "Place -> fetchGenericPlaceList -> Interface adapter casting error");
                                    App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "Interface adapter casting error----" + e.getMessage());

                                }
                            }

                            // *********************************************************************
                            // *********************************************************************

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        App.sendErrorToServer(activity, getClass().getName(), "fetchGenericPlaceList", "onErrorResponse----" + error.getMessage());
                    }
                });

        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }
    //---------------------------------- REQUEST PART -----------------------------------/










    public static void fetchPopularPlaces(Activity activity) {
        fetchGenericPlaceList(
                Request.Method.GET,
                App.SitePath + "api/places.php?token="+ App.getYoubakuToken() +"&apikey="+ App.getYoubakuAPIKey() + "&op=search&popular=1",
                null,
                placesArrayListPopular,
                activity,
                null);
    }

    public static void fetchPopularPlaces(Activity activity, PlaceAdapter adapter) {
        fetchGenericPlaceList(
                Request.Method.GET,
                App.SitePath + "api/places.php?token="+ App.getYoubakuToken() +"&apikey="+ App.getYoubakuAPIKey() + "&op=search&popular=1",
                null,
                placesArrayListPopular,
                activity,
                adapter);
    }

    public static void fetchSearchPlaces(Activity activity, PlaceAdapter adapter) {

        try{
            // Preparing for parameters
            ArrayList selectedSubCategory = new ArrayList();
            for(Category c : Category.categoryList){
                for(SubCategory s : c.getSubCatList()){
                    if(s.isSelected()){
                        selectedSubCategory.add(s.getId());
                    }
                }
            }

            // Change selected subcategory to json string
            JSONArray subCategoryList = new JSONArray(selectedSubCategory);
            String subCategory = subCategoryList.toString().replaceAll("\"","");

            // Parameters
            Map<String, String> map = new HashMap<String,String>();
            map.put("subcat_list", subCategory);
            map.put("op", "search");


            fetchGenericPlaceList(
                    Request.Method.POST,
                    App.SitePath + "api/places.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey(),
                    map,
                    placesArrayListSearch,
                    activity,
                    adapter);


        }catch (NullPointerException e){

            Log.e("G-RAM" ,"Clean up RAM error on Category.categoryList");

            Intent i = new Intent(activity.getApplication(),MainActivity.class);
            activity.finish();
            activity.startActivity(i);

        }


    }


    public static void fetchNearMePlaces(Activity activity,PlaceAdapter adapter) {

        MyLocation my = MyLocation.getMyLocation(MyApplication.getAppContext());
        my.callHard();

        fetchGenericPlaceList(
                Request.Method.GET,
                App.SitePath + "api/places.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey() + "&op=nearme&lat=" + my.latitude + "&lon=" + my.longitude,
                null,
                placesArrayListNearMe,
                activity,
                adapter);
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getPlaceIsOpen() {
        return plc_is_open;
    }

    public void setPlaceIsOpen(String plc_is_open) {
        this.plc_is_open = plc_is_open;
    }
}
