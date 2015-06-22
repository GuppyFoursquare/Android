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
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.parse.Parse;

public class App extends Application {


    //----------------------------------------- API KEYS -----------------------------------------/
    public static final String ParseApplicationId         = "6HK3iYSVSpyCO8COqtuVUGwwHmlFHOBqst5iJgdf";
    public static final String ParseClientKey             = "81QBTfBCE2GIZypCmqKkQKFLwKs36wC6eMUblUrS";
    private static final String GoogleAnalyticsCode       = "PASTE_YOUR_GOOGLE_ANALYTICS_CODE_HERE";
    //----------------------------------------- API KEYS -----------------------------------------/


    //----------------------------------------- HTTP LINKS -----------------------------------------/
    public static final String SitePath         = "http://192.168.1.38/youbaku/";

    //----------------------------------------- HTTP LINKS-----------------------------------------/



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
    //------------------------------------- DEFAULT COLORS ---------------------------------------/




    //------------------------------------ MODERATION PARAMS -------------------------------------/
    public static final boolean moderatePlaces            = false;
    public static final boolean moderateReviews           = true;
    public static final boolean moderatePhotos            = true;
    //------------------------------------ MODERATION PARAMS -------------------------------------/




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
        if(con.getActiveNetworkInfo()==null || !nf.isConnected()){
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
}
