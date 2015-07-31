/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kemal on 17/07/15.
 */
public class PlaceListNearMe<T> {


    private Map<String,T> placesListNearMe;

    public PlaceListNearMe(){
        placesListNearMe = new HashMap<String,T>();
    }

    public void add(String key , T t){
        placesListNearMe.put(key,t);
        //do other things you want to do when items are added
    }

}
