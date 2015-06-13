//
//  Category
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.Locale;

public class Category {
    public static String SELECTED_CATEGORY_ID="";

    public String title="";
    public Bitmap icon;
    public String iconURL="";
    public String markerURL="";
    public String color="";
    public String objectId="";
    public boolean favourite=false;
    private JSONObject name;

    @Override
    public String toString() {
        return title;
    }

    public void setName(JSONObject name) {
        this.name = name;
    }

    public String getName(){
        String name="";
        try{
            if(Locale.getDefault().getLanguage().equals("he"))
                name=this.name.getString("hr");
            else if(Locale.getDefault().getLanguage().equals("zh-CHS"))
                name=this.name.getString("zn_Hans");
            else if(Locale.getDefault().getLanguage().equals("zh-CHT"))
                name=this.name.getString("zn_Hant");
            else
                name=this.name.getString(Locale.getDefault().getLanguage());
        }catch (Exception e){
            try {
                name=this.name.getString("en");
            }catch (Exception ee){
                name=this.title;
            }
        }
        return name;
    }
}
