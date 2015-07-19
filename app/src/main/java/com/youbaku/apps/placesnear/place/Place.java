

package com.youbaku.apps.placesnear.place;

import android.graphics.Bitmap;
import android.location.Location;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.photo.Photo;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.Deal;

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
    public static final String PLC_ID="plc_id";
    public static final String PLC_NAME="plc_name";
    public static final String PLC_EMAIL="plc_email";
    public static final String PLC_WEBSITE="plc_website";
    public static final String PLC_HEADER_IMAGE="plc_header_image";
    public static final String PLC_ADDRESS="plc_address";
    public static final String PLC_CONTACT="plc_contact";
    public static final String PLC_LATITUDE="plc_latitude";
    public static final String PLC_LONGITUDE="plc_longitude";
    public static final String PLC_INFO="plc_info";
    public static final String PLC_IS_ACTIVE="plc_is_active";
    public static final String RATING="rating";




    public static String ID="plc_id";
    public static String EMAIL="plc_email";
    public static final String NAME="plc_name";
    public static final String PHOTO="plc_header_image";
    public static final String WEBPAGE="plc_website";
    public static final String POSITION="position";
    public static final String ADDRESS="plc_address";
    public static final String PHONE="plc_contact";
    public static final String OPENHOUR="plc_intime";
    public static final String CLOSEHOUR="plc_outtime";
    public static final String LIKES="likescount";
    public static final String TWITTER="twitter";
    public static final String CATEGORY="category";
    public static final String ISACTIVE="isactive";



    public static final String DESCRIPTION="description";
    public static final String FACEBOOK="facebook";
    public static final String PLACE="place";
    public static final String DISTANCE="distance";
    public static Place FOR_DETAIL;

    public String imgUrl;
    public String id="";
    public String name="";
    private String phone="";
    public String category="";
    private String address="";
    private String web="";
    public String email="";
    private String description="";
    private boolean isActive=true;
    private double longitude=0;
    private double latitude=0;
    private boolean locationSet=false;
    private float[] distance=new float[1];

    public boolean isFavourite=false;
    public String color="";
    public int likes=0;
    public double rating=10.0;
    public String facebook="";
    public String twitter="";
    public Bitmap photo;
    public File file;
    public ArrayList<Comment> comments=new ArrayList<Comment>();
    public ArrayList<Deal> deals=new ArrayList<Deal>();
    public ArrayList<Photo> photos=new ArrayList<Photo>();
    public String open="";
    public String close="";
    public boolean liked=false;

    public static ArrayList<String> placesIDArrayListNearMe = new ArrayList<>();
    public static ArrayList<Place> placesArrayListNearMe = new ArrayList<>();

    public static Map<String,Place> placesListNearMe = new HashMap<>();



    public void setLocation(double latitude, double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
        locationSet=true;
    }

    public boolean isOpen(){
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date now = format.parse(format.format(new Date()));
            Date open = format.parse(this.open);
            Date close = format.parse(this.close);
            if(!now.after(open) || !now.before(close))
                return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public boolean isLocationSet(){
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








    /**
     *
     * @param jsonObj
     * @param key
     * @return
     */
    private static String getJsonValueIfExist(JSONObject jsonObj, String key){

            try {
                if (jsonObj.has(key)) {
                    return jsonObj.getString(key);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return "";
    }

    private static JSONArray getJsonArayIfExist(JSONObject jsonObj, String key){

        try {
            if (jsonObj.has(key)) {
                return jsonObj.getJSONArray(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param METHOD            Request.Method.GET || Request.Method.POST
     * @param URL
     * @param parameters
     * @param resultPlaceList
     *
     * Burada generic place verileri çekilmektedir. Rating(comment), ek fotolar gibi
     * datalar dönmemektedir.
     */
    public static <T> void fetchGenericPlaceList( int METHOD, String URL , Map parameters , final ArrayList<Place> resultPlaceList , final T adapter){

        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (METHOD, URL, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getString("status").equalsIgnoreCase("SUCCESS")){

                                JSONArray responsePlaceList = response.getJSONArray("content");
                                if (responsePlaceList.length() > 0) {

                                    for (int i = 0; i < responsePlaceList.length(); i++) {

                                        JSONObject jsonPlace = responsePlaceList.getJSONObject(i);

                                        //Inflate places
                                        Place place = new Place();
                                        place.setId(getJsonValueIfExist(jsonPlace,Place.PLC_ID));
                                        place.setName(getJsonValueIfExist(jsonPlace,Place.PLC_NAME));
                                        place.setImgUrl(getJsonValueIfExist(jsonPlace, Place.PLC_HEADER_IMAGE));
                                        place.setAddress(getJsonValueIfExist(jsonPlace,Place.PLC_ADDRESS));
                                        place.setEmail(getJsonValueIfExist(jsonPlace, Place.PLC_EMAIL));
                                        place.setWeb(getJsonValueIfExist(jsonPlace,Place.PLC_WEBSITE));
                                        place.setPhone(getJsonValueIfExist(jsonPlace,Place.PLC_CONTACT));

                                        double latitude = Double.parseDouble(getJsonValueIfExist(jsonPlace, Place.PLC_LATITUDE));
                                        double longitude = Double.parseDouble(getJsonValueIfExist(jsonPlace, Place.PLC_LONGITUDE));
                                        place.setLocation(latitude, longitude);

                                        String htmlToString = String.valueOf(Html.fromHtml(Html.fromHtml(getJsonValueIfExist(jsonPlace, Place.PLC_INFO)).toString()));
                                        place.setDescription(htmlToString);

                                        String isActive = getJsonValueIfExist(jsonPlace, Place.PLC_IS_ACTIVE);
                                        place.setIsActive(isActive.equalsIgnoreCase("1"));

                                        // GET ratings
                                        double averageRating=0;
                                        JSONArray ratings = getJsonArayIfExist(jsonPlace,Place.RATING);
                                        for (int j = 0; ratings!=null && j<ratings.length(); j++) {
                                            Comment comment = new Comment();
                                            JSONObject obj = ratings.getJSONObject(j);
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            try {
                                                Date date = format.parse(obj.getString("places_rating_created_date"));
                                                comment.created = date;
                                                comment.text = obj.getString("place_rating_comment");
                                                comment.comment_id = obj.getString("place_rating_id");
                                                comment.rating = Double.parseDouble(obj.getString("place_rating_rating"));
                                                comment.name = obj.getString("usr_username");

                                                averageRating += comment.rating;

                                                //Getting User Image
                                                if(obj.isNull("usr_profile_picture")){
                                                    comment.user_img="";
                                                    Log.i("---GUPPY USER IMAGE---","No Available Image");
                                                }
                                                else{
                                                    comment.user_img = obj.getString("usr_profile_picture").toString();
                                                }

                                                place.comments.add(comment);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        place.setRating(averageRating/(double)ratings.length());

                                        // GET distance
                                        // --GUPPY COMMENT IMPORTANT--
                                        Location.distanceBetween(40.372877, 49.842825, place.getLatitude(), place.getLongitude(), place.getDistance());


                                        //Put place to list
                                        try{
                                            resultPlaceList.add(place);
                                        }catch (NullPointerException e){
                                            Log.e("---GUPPY---" , "getGenericPlaceList > resultPlaceList parameter is null");
                                        }

                                    }

                                    if(adapter!=null){
                                        ((PlaceAdapter)adapter).setList(resultPlaceList);
                                        ((PlaceAdapter)adapter).notifyDataSetChanged();
                                    }

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

        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
    }

    public static void fetchPopularPlaces(){
        fetchGenericPlaceList(
                Request.Method.GET,
                App.SitePath + "api/places.php?op=search&popular=1",
                null,
                placesArrayListNearMe,
                null);
    }

    public static void fetchPopularPlaces(PlaceAdapter adapter){
        fetchGenericPlaceList(
                Request.Method.GET,
                App.SitePath + "api/places.php?op=search&popular=1",
                null,
                placesArrayListNearMe,
                adapter);
    }

    public float[] getDistance() {
        return distance;
    }

    public void setDistance(float[] distance) {
        this.distance = distance;
    }
}
