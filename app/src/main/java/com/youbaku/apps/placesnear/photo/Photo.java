/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.photo;

public class Photo {
    public static final String PHOTO="photo";
    public static final String PLACE="place";
    public static final String ISACTIVE="isactive";
    private static int LIMIT=0;
    private static int DOWNLOADED=0;
    private static AllPhotosDownloaded SUBSCRIBER;

    private String url="";
    public String place="";
    public boolean isActive=false;

    public Photo() {}

    public static void setSubscriber(AllPhotosDownloaded subscriber) {
        Photo.SUBSCRIBER = subscriber;
    }

    public static void setLIMIT(int LIMIT) {
        Photo.LIMIT = LIMIT;
    }

    public static void increaseDownloaded(){
        Photo.DOWNLOADED++;
        if(Photo.DOWNLOADED==Photo.LIMIT){
            Photo.DOWNLOADED=0;
            if(Photo.SUBSCRIBER!=null)
                Photo.SUBSCRIBER.photosDownloaded();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
