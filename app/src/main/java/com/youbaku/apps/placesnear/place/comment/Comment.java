//
//  Comment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.comment;

import com.youbaku.apps.placesnear.App;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {




    public static final String NAME="name";
    public static final String TEXT="comment";
    public static final String EMAIL="email";
    public static final String PLACE="place";
    public static final String ISACTIVE="isactive";
    public static final String RATING="rating";
    private static int LIMIT=0;
    private static int DOWNLOADED=0;
    private static AllCommentsDownloaded subscriber;

    public String comment_id="";

    public static String getNAME() {
        return NAME;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String name="";
    public String text="";
    public String email="";
    public String place="";
    public String user_img="";
    public double rating=0;
    public Date created;
    public boolean isActive=!App.moderateReviews;

    public Comment() {}

    public static void setSubscriber(AllCommentsDownloaded subscriber){
        Comment.subscriber=subscriber;
    }

    public static void setLIMIT(int l){
        Comment.LIMIT=l;
    }

    public static void increaseDownloaded(){
        Comment.DOWNLOADED++;
        if(Comment.DOWNLOADED==Comment.LIMIT && Comment.subscriber!=null) {
            Comment.DOWNLOADED=0;
            subscriber.CommentsDownloaded();
        }

    }

    public String getCreatedDate(){
        SimpleDateFormat ft=new SimpleDateFormat("dd MMM ,yyyy");
        return ft.format(created);
    }

}
