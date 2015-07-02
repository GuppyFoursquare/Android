//
//  Category
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.utils;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Category {
    public static String SELECTED_CATEGORY_ID="";
    public static String SELECTED_IMAGE_URL="";
    public static String SELECTED_MARKER_URL="";

    public String title="";
    public String subtitle="";
    public Bitmap icon;
    public String iconURL="";
    public String markerURL="";
    public String color="";
    public String objectId="";
    public boolean favourite=false;
    private JSONObject name;
    private static List<SubCategory> subCatList;


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public static List<SubCategory> getSubCatList() {
        return subCatList;
    }

    public static void setSubCatList(List<SubCategory> subCatList) {
        Category.subCatList = subCatList;
    }

}
