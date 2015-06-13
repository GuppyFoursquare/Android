//
//  MapsActivity
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.category.Category;
import com.youbaku.apps.placesnear.category.CategoryList;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.place.PlaceActivity;
import com.youbaku.apps.placesnear.place.filter.PlaceFilter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MapsActivity extends ActionBarActivity implements LocationListener{
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    public static final String LATITUDE="latitude";
    public static final String LONGTİTUDE="longtitude";
    public static final String HAVE_PLACE="havePlace";//is there a place to get close to show

    private double latitude=0;
    private double longtitude=0;
    private boolean havePlace=false;//is there a place to get close to show
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager loma;
    private Marker m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent in=getIntent();
        havePlace=in.getBooleanExtra(HAVE_PLACE,false);
        if(havePlace){
            longtitude=in.getDoubleExtra(LONGTİTUDE,0);
            latitude=in.getDoubleExtra(LATITUDE,0);
        }

        ActionBar ab=getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        if(!havePlace) {
            ((App)getApplication()).track(App.ANALYSIS_SELECT_LOCATION);
            ab.setTitle(getResources().getString(R.string.selectlocationtitlelabel));
            ab.setSubtitle(getResources().getString(R.string.selectlocationsubtitlelabel));
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0093D4")));
        }else{
            ((App)getApplication()).track(App.ANALYSIS_MAP);
            ab.setTitle(in.getStringExtra(Place.NAME));
            ab.setBackgroundDrawable(new ColorDrawable(in.getIntExtra(PlaceActivity.COLOR,0x0093d4)));
            String dist= App.getDistanceString(PlaceFilter.getInstance().metrics,Place.FOR_DETAIL.distance[0]);
            ab.setSubtitle(dist+" "+getResources().getString(R.string.titledistancelabel));
        }
            setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps,menu);
        if(havePlace){
            ((MenuItem)menu.getItem(0)).setVisible(false);
            ((MenuItem)menu.getItem(1)).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.location_selected){
            finish();
            return  true;
        }else if(item.getItemId()==R.id.get_directions_maps_menu){
            String mesaj=String.format(getResources().getString(R.string.openmapsappmessage),Place.FOR_DETAIL.name);
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(mesaj);
            builder.setNegativeButton(getResources().getString(R.string.alertcancelbuttonlabel),null);
            builder.setPositiveButton(getResources().getString(R.string.openbuttonlabel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyLocation my = MyLocation.getMyLocation(getApplicationContext());
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + my.latitude + "," + my.longitude + "&daddr=" + latitude + "," + longtitude;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(Intent.createChooser(intent, "Select an application"));
                }
            });
            builder.show();
        }else if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        if(!havePlace) {
            mMap.setOnMapClickListener(mapClickListener);
            loma = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loma.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            loma.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            loma.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }else{
            LatLng ll=new LatLng(latitude,longtitude);
            CameraUpdate ca=CameraUpdateFactory.newLatLngZoom(ll,15);
            mMap.animateCamera(ca);
            m=mMap.addMarker(new MarkerOptions().position(ll));
            Category cat= CategoryList.getCategory(Category.SELECTED_CATEGORY_ID);
            Picasso.with(this)
                    .load(cat.markerURL)
                    .placeholder(R.drawable.placeholderpin)
                    .into(markerTarget);
        }
    }

    GoogleMap.OnMapClickListener mapClickListener=new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(m==null)
                m=mMap.addMarker(new MarkerOptions().position(latLng));
            else
                m.setPosition(latLng);
        }
    };

    @Override
    public void finish() {
        if(m==null){
            setResult(RESULT_CANCELED);
        }else {
            Intent in = new Intent();
            in.putExtra(LATITUDE, m.getPosition().latitude);
            in.putExtra(LONGTİTUDE, m.getPosition().longitude);
            setResult(RESULT_OK, in);
        }
        super.finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap==null)
            return;
        LatLng ll=new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate ca=CameraUpdateFactory.newLatLngZoom(ll,15);
        mMap.animateCamera(ca);
        loma.removeUpdates(this);
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

    Target markerTarget=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap b=Bitmap.createScaledBitmap(bitmap,54,64,false);
            m.setIcon(BitmapDescriptorFactory.fromBitmap(b));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
}
