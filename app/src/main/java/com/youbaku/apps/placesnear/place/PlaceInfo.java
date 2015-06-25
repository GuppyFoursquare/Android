package com.youbaku.apps.placesnear.place;

import java.util.ArrayList;

/**
 * Created by orxan on 24.06.2015.
 */
public class PlaceInfo {
    public static PlaceInfo FOR_DETAIL;
    String description;
    String plc_website;
    private ArrayList<PlaceInfo> list;


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
