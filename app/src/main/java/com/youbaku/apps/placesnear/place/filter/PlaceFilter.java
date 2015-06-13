//
//  PlaceFilter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.filter;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.place.Place;

public class PlaceFilter {
    private static PlaceFilter instance;
    public enum DistanceSystem{km,ml};
    public enum SortBy{distance,rating,like};

    public String keyword= App.keyword;
    public boolean open=App.isOpen;
    public boolean popular=App.isPopular;
    public int limit=App.resultCount;
    public DistanceSystem metrics;
    private double distance=App.distanceRadius;//always as km, conversion at getter and setter
    public SortBy sorting=SortBy.distance;
    public String category="";

    private PlaceFilter() {
        if(App.isMetric){
            metrics=DistanceSystem.km;
        }else{
            metrics=DistanceSystem.ml;
        }
        if(App.sortByRating)
            sorting=SortBy.rating;
        else if(App.sortByLikes)
            sorting=SortBy.like;
        else if(App.sortByDistance)
            sorting=SortBy.distance;

    }

    public static PlaceFilter getInstance() {
        if(instance==null){
            instance=new PlaceFilter();
        }
        return instance;
    }

    public double getDistance(DistanceSystem type) {
        if(type==DistanceSystem.km)
            return distance/1000;
        else{
            return (distance/1000)*0.6214;
        }
    }

    public void setDistance(double distance, DistanceSystem type) {
        setDistanceType(type);
        setDistance(distance);
    }

    public void setDistanceType(DistanceSystem type){
        metrics=type;
    }

    public void setDistance(double distance){
        if(metrics==DistanceSystem.km)
            this.distance = distance;
        else{
            this.distance=distance/0.6214;
        }
    }

    public String getOrder(){
        if(sorting==SortBy.rating) {
            return Place.RATING;
        }else if(sorting==SortBy.like) {
            return Place.LIKES;
        }else{
            return Place.DISTANCE;
        }
    }

    public static void resetFilter(){
        if(instance==null)
            return;
        instance.keyword= App.keyword;
        instance.open=App.isOpen;
        instance.popular=App.isPopular;
        instance.limit=App.resultCount;
        instance.distance=App.distanceRadius;
        instance.category="";
        if(App.isMetric){
            instance.metrics=DistanceSystem.km;
        }else{
            instance.metrics=DistanceSystem.ml;
        }
        if(App.sortByRating)
            instance.sorting=SortBy.rating;
        else if(App.sortByLikes)
            instance.sorting=SortBy.like;
        else
            instance.sorting=SortBy.distance;
    }
}
