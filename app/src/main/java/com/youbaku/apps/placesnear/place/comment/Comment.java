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

    public String name="";
    public String text="";
    public String email="";
    public String place="";
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
        SimpleDateFormat ft=new SimpleDateFormat("dd, MMM ,yyyy");
        return ft.format(created);
    }

}
