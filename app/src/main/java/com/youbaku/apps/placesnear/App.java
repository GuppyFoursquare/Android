//
//  App
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.parse.Parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {


    //----------------------------------------- API KEYS -----------------------------------------/
    public static final String ParseApplicationId         = "6HK3iYSVSpyCO8COqtuVUGwwHmlFHOBqst5iJgdf";
    public static final String ParseClientKey             = "81QBTfBCE2GIZypCmqKkQKFLwKs36wC6eMUblUrS";
    private static final String GoogleAnalyticsCode       = "PASTE_YOUR_GOOGLE_ANALYTICS_CODE_HERE";
    //----------------------------------------- API KEYS -----------------------------------------/


    //----------------------------------------- HTTP LINKS -----------------------------------------/
    public static final String SitePath         = "http://youbaku.com/";
//    public static final String SitePath         = "http://192.168.2.50/youbaku/";
    //----------------------------------------- HTTP LINKS-----------------------------------------/



    //----------------------------------------- TOKENS -----------------------------------------/
    public static String youbakuToken=null;
    public static String youbakuAPIKey=null;
    public static String username=null;
    public static String useremail=null;
    //----------------------------------------- TOKENS -----------------------------------------/


    //----------------------------------------- COMMON -----------------------------------------/
    public static final String RESULT_STATUS = "status";
    public static final String RESULT_CONTENT = "content";

    /**
     * @param jsonObj
     * @param key
     * @return
     */
    public static String getJsonValueIfExist(JSONObject jsonObj, String key) {

        try {
            if (jsonObj.has(key)) {
                return jsonObj.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return "";
    }

    public static JSONObject getJsonArrayValueIfExist(JSONArray jsonObj, int key) {
        try {
            if (jsonObj.getJSONObject(key)!=null) {
                return jsonObj.getJSONObject(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getJsonArayIfExist(JSONObject jsonObj, String key) {

        try {
            if (jsonObj.has(key)) {
                return jsonObj.getJSONArray(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }
    //----------------------------------------- COMMON -----------------------------------------/




    //---------------------------------- DEFAULT PLACE FILTERS -----------------------------------/
    public static final String  keyword                   = "";
    public static final boolean isOpen                    = false;
    public static final boolean isPopular                 = false;
    public static final int     resultCount               = 20;
    public static final int     distanceRadius            = 10000;
    public static final int     minimumDistance           = 100;
    public static final int     maximumDistance           = 60000;
    public static final boolean sortByDistance            = true;
    public static final boolean sortByRating              = false;
    public static final boolean sortByLikes               = false;
    public static final boolean isMetric                  = true;
    //---------------------------------- DEFAULT PLACE FILTERS -----------------------------------/




    //------------------------------------- DEFAULT COLORS ---------------------------------------/
    public static final String DefaultActionBarColor      = "#1f1f1f";
    public static final String BackgroundGrayColor        = "#EFEFF4";
    public static final String DefaultBackgroundColor     = "#414142";
    public static final String CategoryListLabelColor     = "#ffffff";
    public static final String FacebookColor              = "#3B5998";
    public static final String TwitterColor               = "#2CA6EF";
    public static final String SVGPassiveColor            = "#777777";
    public static final String LoaderColor                = "#FFDE3B";
    public static final String ButtonColor                = "#c5273f";
    public static final String GreenColor                 = "#04a968";
    public static final String YellowColor                = "#debe41";
    //------------------------------------- DEFAULT COLORS ---------------------------------------/




    //------------------------------------ MODERATION PARAMS -------------------------------------/
    public static final boolean moderatePlaces            = false;
    public static final boolean moderateReviews           = true;
    public static final boolean moderatePhotos            = true;
    //------------------------------------ MODERATION PARAMS -------------------------------------/



    //------------------------------------ MESSAGE TYPES -------------------------------------/
    public static final int typeConnection               = 1;
    public static final int typeNull                     = 2;
    public static final int typeInfo                     = 3;
    public static final int typeSuccess                  = 4;

    //------------------------------------ MESSAGE TYPES -------------------------------------/



    //------------------------------- DO NOT CHANGE THESE VALUES ---------------------------------/
    //PARSE CLASSES
    public static final String PARSE_CATEGORIES           = "Categories";
    public static final String PARSE_PLACES               = "Places";
    public static final String PARSE_COMMENTS             = "Reviews";
    public static final String PARSE_DEALS                = "Deals";
    public static final String PARSE_PHOTOS               = "Photos";


    //GOOGLE ANALYTICS CLASSES
    public static final String ANALYSIS_CATEGORIES        = "Categories Screen";
    public static final String ANALYSIS_PLACES            = "Places Screen";
    public static final String ANALYSIS_PLACE_DETAILS     = "Place Details Screen";
    public static final String ANALYSIS_FILTERS           = "Filters Screen";
    public static final String ANALYSIS_DEALS             = "Deals Screen";
    public static final String ANALYSIS_REVIEWS           = "Reviews Screen";
    public static final String ANALYSIS_ADD_PLACE         = "Add Place Screen";
    public static final String ANALYSIS_WRITE_REVIEW      = "Write Review Screen";
    public static final String ANALYSIS_SELECT_LOCATION   = "Select Location Screen";
    public static final String ANALYSIS_MAP               = "Map Screen";
    public static final String ANALYSIS_PHOTOS            = "Photos Screen";
    private Tracker TRACKER;


    //OTHER SETTINGS
    public static final int MaximumPhotoHeight            = 1500;//it will also work as maximum width
    public static final int FilterTickWidthHeight         = 60;
    public static final int SVGOldColor                   = 0xff3377BB;
    public static final PlaceFilter.DistanceSystem System = PlaceFilter.DistanceSystem.km;
    //------------------------------- DO NOT CHANGE THESE VALUES ---------------------------------/



    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, ParseApplicationId, ParseClientKey);
    }

    @SuppressLint("NewApi") public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static int dpTopx(Context con,int dp){
        float scale=con.getResources().getDisplayMetrics().density;
        return (int)(dp*scale*0.5f);
    }

    public static int getInSampleSize(BitmapFactory.Options opt,int maxWidth, int maxHeight){
        int h=opt.outHeight;
        int w=opt.outWidth;
        int inSampleSize=2;

        if(h>maxHeight || w>maxWidth){
            int newH=h/2;
            int newW=w/2;

            while((newH/inSampleSize)>maxHeight && (newW/inSampleSize)>maxWidth){
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }

    public static String getDistanceString(float meter){
        if(meter<100){
            return (((int)(meter*10))/10)+"m";
        }else if(meter<1000){
            return ((int)meter)+"m";
        }else{
            return ((float)((int)(meter*10/1000))/10)+"km";
        }
    }

    /**
     * convert to readable string
     * @param type tells the type of next parameter is meter or yard
     * @param meter length to convert string as a unit of meter or yard
     * @return
     */
    public static String getDistanceString(PlaceFilter.DistanceSystem type,double meter) {
        if (type == PlaceFilter.DistanceSystem.km){
            if (meter < 100) {
                return (((int) (meter * 10)) / 10) + "m";
            } else if (meter < 1000) {
                return ((int) meter) + "m";
            } else {
                return ((float) ((int) (meter * 10 / 1000)) / 10) + "km";
            }
        }else{
            if(meter <100){
                return (((int) (meter * 10)) / 10) + "yd";
            }else if(meter < 1760){
                return ((int) meter) + "yd";
            }else{
                return ((float) ((int) (meter * 10 / 1760)) / 10) + "mi";
            }
        }
    }


    synchronized Tracker getTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        TRACKER = analytics.newTracker(GoogleAnalyticsCode);
        //t.enableAdvertisingIdCollection(true);
        return TRACKER;
    }

    public void track(String screenName){
        Tracker tr=getTracker();
        tr.setScreenName(screenName);
        tr.send(new HitBuilders.AppViewBuilder().build());
    }

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager con=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=con.getActiveNetworkInfo();
        NetworkInfo mWifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(con.getActiveNetworkInfo()==null || !nf.isConnected() && !mWifi.isConnected()){
            return false;
        }
        return true;
    }

    public static void showInternetError(final Activity activity){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.networkconnectionerrormessage));
        builder.setPositiveButton(activity.getResources().getString(R.string.alertokbuttonlabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.show();
    }

    public static void showUserError(final Activity activity, final boolean wantToFinishActivity , String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(activity.getResources().getString(R.string.alertokbuttonlabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(wantToFinishActivity){
                    activity.finish();
                }
            }
        });
        builder.show();
    }

    //Generic Info Method
    public static void showGenericInfoActivity(Context context, int typeID,String message){

        String currMessage=message;
        int iconId;

        if(typeID==typeConnection){
            iconId=R.drawable.connection_icon;
        }
        else if(typeID==typeNull){
            iconId=R.drawable.sad_icon;
        }
        else if(typeID==typeSuccess){
            iconId=R.drawable.tick_icon;
        }
        else{
            iconId=R.drawable.warning_icon;
        }

        //Intent Operation
        Intent in =new Intent(context,InfoActivity.class);

        in.putExtra("message",message);
        in.putExtra("imgId",""+iconId);

        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }


    /**
     *
     * @param activity
     *
     * This method will be used for get Logs of Android Project
     */
    public static void sendErrorToServer(final Activity activity , String className, String functionName, String errorCause){

        boolean wantToSendReport=true;
        if(wantToSendReport){

            String mPhoneNumber="";
            if(activity!=null){
                TelephonyManager tMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
                mPhoneNumber = tMgr.getLine1Number();
                mPhoneNumber = tMgr.getDeviceId();
            }

            //Calling Api
            String url = "http://193.140.63.162:8080/youbaku_LOG/youbaku";

            Map<String, String> map = new HashMap<String, String>();
            map.put("class", className);
            map.put("function", functionName);
            map.put("error", errorCause);
            map.put("phone", mPhoneNumber + "/" + Build.VERSION.SDK_INT);

            // Request a json response
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if(activity!=null && false){
                                    Toast.makeText(activity , response.getString("resultText") , Toast.LENGTH_SHORT).show();
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
        }

    }
}
