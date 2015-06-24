package com.youbaku.apps.placesnear.utils;

/**
 * Created by orxan on 17.06.2015.
 */
public class SubCategory {
    public static String SELECTED_SUB_CATEGORY_ID="";
    public static String SELECTED_SUB_CATEGORY_NAME="";

    String title;
    String id;
    String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
