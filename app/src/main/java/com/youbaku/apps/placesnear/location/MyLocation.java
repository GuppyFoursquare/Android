/**
 *
 * Created by Guppy Org.
 * Copyright (c) 2015 CasbianSoft. All rights reserved.
 *
 * @Developer   Kemal Sami KARACA
 * @Modified    24/07/2015
 *
 */

package com.youbaku.apps.placesnear.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation implements LocationListener{

    private static MyLocation instance;
    private static final long MIN_TIME = 60000;
    private static final float MIN_DISTANCE = 200;

    public double longitude=0;
    public double latitude=0;
    private boolean isSet=false;
    private LocationManager locationManager;
    public MyLocationSet subscriber;
    private boolean called=false;

    private MyLocation(Context context) {
        locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }


    public static MyLocation getMyLocation(Context context){
        if(instance==null){
            instance=new MyLocation(context);
        }
        return instance;
    }

    public void callHard(){
        isSet=false;
        called=false;
        callLocation();
    }

    public void callLocation(){
        if(called)
            return;
        called=true;
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            longitude=location.getLongitude();
            latitude=location.getLatitude();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude=location.getLongitude();
            latitude=location.getLatitude();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            longitude=location.getLongitude();
            latitude=location.getLatitude();
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }

    }

    public static boolean checkLocationServices(Context context){
        MyLocation locationObj=MyLocation.getMyLocation(context);
        LocationManager localManager=locationObj.locationManager;

        return localManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                localManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                localManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

    }

    public boolean isSet(){
        return isSet;
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
        called=false;
        locationManager.removeUpdates(this);
        if(!isSet) {
            isSet = true;
            if (subscriber != null) {
                subscriber.locationSet();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
