//
//  Place
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place;

import android.graphics.Bitmap;

import com.youbaku.apps.placesnear.place.comment.Comment;
import com.youbaku.apps.placesnear.place.deal.Deal;
import com.youbaku.apps.placesnear.photo.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Place {
    public static  String ID="plc_id";
    public static  String EMAIL="plc_email";



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
    public static final String RATING="rating";
    public static final String PLACE="place";
    public static final String DISTANCE="distance";
    public static Place FOR_DETAIL;

    public String imgUrl;



    public String id="";
    public String name="";
    public String phone="";
    public String category="";
    public String address="";

    public String description="";
    public boolean isActive=true;
    public boolean isFavourite=false;
    public String color="";
    public int likes=0;
    public double rating=10.0;
    public String web="";
    public String email;
    public String facebook="";
    public String twitter="";
    public Bitmap photo;
    public File file;
    public float[] distance=new float[1];
    public ArrayList<Comment> comments=new ArrayList<Comment>();
    public ArrayList<Deal> deals=new ArrayList<Deal>();
    public ArrayList<Photo> photos=new ArrayList<Photo>();
    private double longitude=0;
    private double latitude=0;
    private boolean locationSet=false;
    public String open="";
    public String close="";
    public boolean liked=false;




    public Place() {
    }



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
}
