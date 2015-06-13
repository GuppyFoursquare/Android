//
//  Deal
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.deal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Deal {
    public static final String TITLE="title";
    public static final String DESCRIPTION="description";
    public static final String URL="url";
    public static final String START_DATE="startdate";
    public static final String END_DATE="enddate";
    public static final String PLACE="place";
    public static final String PHOTO="photo";
    private static int LIMIT=0;
    private static int DOWNLOADED=0;
    private static AllDealsDownloaded subscriber;

    public String title="";
    public String description="";
    public String url="";
    public Date startDate;
    public Date endDate;
    public String photo="";
    public String place="";

    public Deal() {}

    public static void setSubscriber(AllDealsDownloaded sub){
        Deal.subscriber=sub;
    }

    public static void setLIMIT(int limit){
        Deal.LIMIT=limit;
    }

    public static void increaseDownloaded(){
        Deal.DOWNLOADED++;
        if(Deal.DOWNLOADED==Deal.LIMIT) {
            Deal.DOWNLOADED = 0;
            subscriber.dealsDownloaded();
        }
    }

    public String getDates(){
        SimpleDateFormat ft=new SimpleDateFormat("dd MMM yyyy HH:mm");
        return ft.format(startDate)+" - "+ft.format(endDate);
    }
}
