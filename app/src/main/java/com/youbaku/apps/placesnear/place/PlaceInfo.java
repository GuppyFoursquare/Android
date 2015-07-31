/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place;

import java.util.ArrayList;

/**
 * Created by orxan on 24.06.2015.
 */
public class PlaceInfo {
    public static PlaceInfo FOR_DETAIL;
    String description;
    public static String str="";

    private String mytext;
    private static String plc_website;
    private ArrayList<PlaceInfo> list;
    private static PlaceInfo instance;

    public PlaceInfo() {
    }

    public static PlaceInfo getInstance() {

        if (instance == null) {
            instance = new PlaceInfo();
        }
        return instance;
    }


    public String getMytext() {
        return mytext;
    }

    public void setMytext(String mytext) {
        this.mytext = mytext;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlc_website() {
        return plc_website;
    }

    public void setPlc_website(String plc_website) {
        this.plc_website = plc_website;
    }



}
