//
//  Photo
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.photo;

public class Photo {
    public static final String PHOTO="photo";
    public static final String PLACE="place";
    public static final String ISACTIVE="isactive";
    private static int LIMIT=0;
    private static int DOWNLOADED=0;
    private static AllPhotosDownloaded SUBSCRIBER;

    public String url="";
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
}
