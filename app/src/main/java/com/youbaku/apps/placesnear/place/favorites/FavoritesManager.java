package com.youbaku.apps.placesnear.place.favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.category.CategoryList;
import com.youbaku.apps.placesnear.location.MyLocation;
import com.youbaku.apps.placesnear.photo.Photo;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.Deal;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsyn on 3/14/2015.
 */
public class FavoritesManager {
    private static final String PREF="favorites";

    private static String[] arr;
    private static ArrayList<Place> list = new ArrayList<Place>();
    private static int commentCount=0;
    private static int dealCount=0;
    private static int photoCount=0;
    private static FavoritesCallback callback;
    private static boolean changed=true;

    private FavoritesManager() {}

    public static void saveFavorite(Context context, String id){
        changed=true;
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String s=preferences.getString(PREF,"");
        if(s.length()>0)
            s+=",";
        s+=id;
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(PREF,s);
        editor.commit();
    }

    public static void removeFavorite(Context context, String id){
        changed=true;
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String s=preferences.getString(PREF,"");
        String ss=s;
        int start=s.indexOf(id);
        if(start>0){
            ss=s.substring(0,start-1);
            if(start+id.length()!=s.length())
                ss+=s.substring(start+id.length(),s.length());
        }else if(start==0){
            if(s.length()>start+id.length()+1)
                ss=s.substring(start+id.length()+1,s.length());
            else
                ss="";
        }
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(PREF,ss);
        editor.commit();
    }

    public static boolean isFavorite(Context context, String id){
        if(arr==null){
            prepareFavorites(context);
            if(arr==null)
                return false;
        }
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(id))
                return true;
        }
        return false;
    }

    public static boolean isChanged() {
        return changed;
    }

    private static void prepareFavorites(Context context){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String s=preferences.getString(PREF,"");
        arr=null;
        if(s.length()>0)
            arr=s.split(",");
    }

    public static void refresh(final Context context, FavoritesCallback callback){
        FavoritesManager.callback=callback;
        final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        prepareFavorites(context);
        changed=false;
        if(arr==null){
            callback.favoritesReady(null);
            return;
        }
        list=new ArrayList();
        commentCount=0;
        dealCount=0;
        photoCount=0;
        for(int i=0;i<arr.length;i++){
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(App.PARSE_PLACES);
            query.whereEqualTo(Place.ID,arr[i]);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject o, ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
                        commentCount++;
                        dealCount++;
                        photoCount++;
                        return;
                    }

                    if(!o.getBoolean(Place.ISACTIVE)){
                        commentCount++;
                        dealCount++;
                        photoCount++;
                        return;
                    }

                    final Place p = new Place();
                    p.name = o.getString(Place.NAME);
                    p.setDescription(o.getString(Place.DESCRIPTION));
                    p.category = CategoryList.getCategory(o.getString(Place.CATEGORY)).title;
                    p.color = App.DefaultActionBarColor;
                    p.id = o.getObjectId();
                    p.likes = Integer.parseInt(o.getString(Place.LIKES));
                    p.setIsActive(o.getBoolean(Place.ISACTIVE));
                    p.rating = Double.parseDouble(o.getString(Place.RATING));
                    p.setOpen(o.getString(Place.OPENHOUR));
                    p.setClose(o.getString(Place.CLOSEHOUR));
                    p.setWeb(o.getString(Place.WEBPAGE));
                    p.facebook = o.getString(Place.FACEBOOK);
                    p.twitter = o.getString(Place.TWITTER);
                    p.setPhone(o.getString(Place.PHONE));
                    p.liked=preferences.getBoolean(p.id,false);
                    p.isFavourite=true;
                    p.setAddress(o.getString(Place.ADDRESS));

                    ParseGeoPoint geo = o.getParseGeoPoint(Place.POSITION);
                    p.setLocation(geo.getLatitude(), geo.getLongitude());
                    MyLocation my = MyLocation.getMyLocation(context);
                    my.callHard();
                    Location.distanceBetween(my.latitude, my.longitude, p.getLatitude(), p.getLongitude(), p.getDistance());

                    /*
                    try {
                        List<Address> go = new Geocoder(context).getFromLocation(p.getLatitude(), p.getLongitude(), 1);
                        if (!go.isEmpty()) {
                            p.address = go.get(0).getAddressLine(0) + " " + go.get(0).getAddressLine(1);
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }*/

                    list.add(p);

                    ParseQuery<ParseObject> q = ParseQuery.getQuery(App.PARSE_COMMENTS);
                    q.whereEqualTo(Comment.PLACE, o.getObjectId());
                    q.whereEqualTo(Comment.ISACTIVE, true);
                    q.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            for (int i = 0; i < parseObjects.size(); i++) {
                                Comment c = new Comment();
                                ParseObject o = parseObjects.get(i);
                                c.created = o.getCreatedAt();
                                c.name = o.getString(Comment.NAME);
                                c.text = o.getString(Comment.TEXT);
                                c.email = o.getString(Comment.EMAIL);
                                c.rating = o.getDouble(Comment.RATING);
                                c.isActive = true;
                                c.place = o.getString(Comment.PLACE);
                                p.comments.add(c);
                            }
                            commentCount++;
                            checkDownloads();
                        }
                    });


                    ParseQuery<ParseObject> qq = ParseQuery.getQuery(App.PARSE_DEALS);
                    qq.whereEqualTo(Deal.PLACE, o.getObjectId());
                    qq.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e != null)
                                e.printStackTrace();
                            for (int i = 0; i < parseObjects.size(); i++) {
                                Deal d = new Deal();
                                ParseObject o = parseObjects.get(i);
                                d.title = o.getString(Deal.TITLE);
                                d.description = o.getString(Deal.DESCRIPTION);
                                d.url = o.getString(Deal.URL);
                                d.place = o.getString(Deal.PLACE);
                                ParseFile file = o.getParseFile(Deal.PHOTO);
                                d.photo = file.getUrl();
                                d.startDate = o.getDate(Deal.START_DATE);
                                d.endDate = o.getDate(Deal.END_DATE);
                                p.deals.add(d);
                            }
                            dealCount++;
                            checkDownloads();
                        }
                    });

                    ParseQuery<ParseObject> qp = ParseQuery.getQuery(App.PARSE_PHOTOS);
                    qp.whereEqualTo(Photo.PLACE, o.getObjectId());
                    qp.whereEqualTo(Photo.ISACTIVE, true);
                    qp.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e != null)
                                e.printStackTrace();
                            for (int i = 0; i < parseObjects.size(); i++) {
                                Photo pp = new Photo();
                                ParseObject o = parseObjects.get(i);
                                pp.isActive = true;
                                pp.place = o.getString(Photo.PLACE);
                                ParseFile f = o.getParseFile(Photo.PHOTO);
                                pp.url = f.getUrl();
                                p.photos.add(pp);
                            }
                            photoCount++;
                            checkDownloads();
                        }
                    });
                }
            });
        }
    }

    private static void checkDownloads(){
        if(commentCount==arr.length && dealCount==arr.length && photoCount==arr.length){

            callback.favoritesReady(list);
        }
    }

    public static void stop(){
        changed=true;
    }
}
