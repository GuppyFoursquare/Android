//
//  FilterFragment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.filter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;

public class FilterFragment extends Fragment {
    private TextView limit1;
    private TextView limit2;
    private TextView limit3;
    private TextView limit4;
    private TextView limit5;
    private EditText keyword;
    private ToggleButton popular;
    private ToggleButton open;
    private TextView km;
    private TextView ml;
    private ImageView tick;
    private SeekBar distance;
    private TextView distanceMin;
    private TextView distanceMax;
    private TextView sortDistance;
    private TextView sortRating;
    private TextView sortLikes;
    private ImageView sortTick;

    private TextView limitInfo;
    private TextView distanceInfo;
    private TextView lengthInfo;
    private TextView sortInfo;


    private RelativeLayout.LayoutParams distanceTickParams;
    private RelativeLayout.LayoutParams sortTickParams;

    private TextView limitSelected;
    private PlaceFilter filter;
    private int color=0;


    public FilterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App)getActivity().getApplication()).track(App.ANALYSIS_FILTERS);
        ((ScrollView)getView().findViewById(R.id.main_filter_fragment)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));

        keyword=(EditText)getView().findViewById(R.id.keyword_filter_fragment);
        popular=(ToggleButton)getView().findViewById(R.id.popular_toggle_filter_fragment);
        open=(ToggleButton)getView().findViewById(R.id.open_toggle_filter_fragment);
        limit1=(TextView)getView().findViewById(R.id.limit_ten_filter_fragment);
        limit2=(TextView)getView().findViewById(R.id.limit_twenty_filter_fragment);
        limit3=(TextView)getView().findViewById(R.id.limit_thirty_filter_fragment);
        limit4=(TextView)getView().findViewById(R.id.limit_fourty_filter_fragment);
        limit5=(TextView)getView().findViewById(R.id.limit_fifty_filter_fragment);
        km=(TextView)getView().findViewById(R.id.kilometer_filter_fragment);
        ml=(TextView)getView().findViewById(R.id.miles_filter_fragment);
        tick=(ImageView)getView().findViewById(R.id.distance_type_filter_fragment);
        distance=(SeekBar)getView().findViewById(R.id.seek_bar_filter_fragment);
        distanceMin=(TextView)getView().findViewById(R.id.seek_bar_start_text_filter_fragment);
        distanceMax=(TextView)getView().findViewById(R.id.seek_bar_end_text_filter_fragment);
        sortDistance=(TextView)getView().findViewById(R.id.sort_distance_filter_fragment);
        sortRating=(TextView)getView().findViewById(R.id.sort_rating_filter_fragment);
        sortLikes=(TextView)getView().findViewById(R.id.sort_likes_filter_fragment);
        sortTick=(ImageView)getView().findViewById(R.id.sort_image_filter_fragment);
        limitInfo=(TextView)getView().findViewById(R.id.limit_info_text_filter_fragment);
        distanceInfo=(TextView)getView().findViewById(R.id.distance_info_text_filter_fragment);
        lengthInfo=(TextView)getView().findViewById(R.id.distance_length_info_text_filter_fragment);
        sortInfo=(TextView)getView().findViewById(R.id.sort_info_text_filter_fragment);
        filter=PlaceFilter.getInstance();

        SVG tik= SVGParser.getSVGFromResource(getResources(),R.raw.icon_check,App.SVGOldColor,color);
        ViewCompat.setLayerType(tick, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(sortTick,ViewCompat.LAYER_TYPE_SOFTWARE,null);

        tick.setImageDrawable(tik.createPictureDrawable());
        sortTick.setImageDrawable(tik.createPictureDrawable());

        distanceTickParams=(RelativeLayout.LayoutParams)tick.getLayoutParams();
        sortTickParams=(RelativeLayout.LayoutParams)sortTick.getLayoutParams();
        distanceTickParams.width=App.dpTopx(getActivity(),App.FilterTickWidthHeight);
        sortTickParams.width=App.dpTopx(getActivity(),App.FilterTickWidthHeight);
        distanceTickParams.height=App.dpTopx(getActivity(),App.FilterTickWidthHeight);
        sortTickParams.height=App.dpTopx(getActivity(),App.FilterTickWidthHeight);

        tick.setLayoutParams(distanceTickParams);
        sortTick.setLayoutParams(sortTickParams);

        if(!filter.keyword.equals(""))
            keyword.setText(filter.keyword);

        if(Build.VERSION.SDK_INT>=11) {
            open.setActivated(filter.open);
            popular.setActivated(filter.popular);
        }

        switch (filter.limit){
            case 10:
                limitSelected=limit1;
                limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),10));
                break;
            case 20:
                limitSelected=limit2;
                limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),20));
                break;
            case 30:
                limitSelected=limit3;
                limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),30));
                break;
            case 40:
                limitSelected=limit4;
                limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),40));
                break;
            case 50:
                limitSelected=limit5;
                limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),50));
                break;

        }
        limitChanged(limitSelected);

        if(filter.metrics== PlaceFilter.DistanceSystem.km){
            distanceInfo.setText(getResources().getString(R.string.distanceunitsectionlabel)+" : "+getResources().getString(R.string.kilometerlabel));
            distanceTickParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.kilometer_filter_fragment);
            tick.setLayoutParams(distanceTickParams);
        }else{
            distanceInfo.setText(getResources().getString(R.string.distanceunitsectionlabel)+" : "+getResources().getString(R.string.mileslabel));
            distanceTickParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.miles_filter_fragment);
            tick.setLayoutParams(distanceTickParams);
        }

        //x=((progress * ((double) (App.maximumDistance - App.minimumDistance) / (100))) + App.minimumDistance)
        int x=App.distanceRadius;
        int value=(int)((x-App.minimumDistance)/((double) (App.maximumDistance - App.minimumDistance) / (100)));
        Drawable dr=getActivity().getResources().getDrawable(R.drawable.thumb_selector);
        dr.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
        System.out.println(Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT!=Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable ld = (LayerDrawable) distance.getProgressDrawable();
            ld.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }else{
            StateListDrawable sld=(StateListDrawable) distance.getProgressDrawable();
            sld.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        lengthInfo.setText(getResources().getString(R.string.distancelabel) + " : " + App.getDistanceString(x));
        distance.setProgress(value);
        distance.setThumb(dr);

        if(filter.sorting== PlaceFilter.SortBy.like){
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.distancelabel));
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_likes_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }else if(filter.sorting== PlaceFilter.SortBy.rating){
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.ratingslabel));
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_rating_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }else{
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.likeslabel));
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_distance_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }

        int ten=App.dpTopx(getActivity(),10);
        int twenty=App.dpTopx(getActivity(),20);
        int white=Color.parseColor("#ffffff");

        keyword.addTextChangedListener(keywordWatcher);

        popular.setOnCheckedChangeListener(popularChanged);
        open.setOnCheckedChangeListener(openChanged);
        limit1.setOnClickListener(limitOne);
        limit2.setOnClickListener(limitTwo);
        limit3.setOnClickListener(limitThree);
        limit4.setOnClickListener(limitFour);
        limit5.setOnClickListener(limitFive);
        km.setOnClickListener(kmClick);
        ml.setOnClickListener(mlClick);
        distance.setOnSeekBarChangeListener(distanceChanged);
        distanceMax.setText(App.getDistanceString(App.System, App.maximumDistance));
        distanceMin.setText(App.getDistanceString(App.System,App.minimumDistance));
        sortDistance.setOnClickListener(sortDistanceClick);
        sortRating.setOnClickListener(sortRatingClick);
        sortLikes.setOnClickListener(sortLikesClick);
    }

    public IBinder getWindowToken(){
        return keyword.getWindowToken();
    }

    public void setColor(int color) {
        this.color = color;
    }

    private void limitChanged(TextView newbie){
        Drawable dr=getActivity().getResources().getDrawable(R.drawable.rating_background);
        dr.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        limitSelected.setBackgroundColor(Color.parseColor("#ffffff"));
        limitSelected.setTextColor(Color.parseColor("#757575"));
        limitSelected=newbie;
        if(Build.VERSION.SDK_INT>=16)
            limitSelected.setBackground(dr);
        else
            limitSelected.setBackgroundDrawable(dr);
        limitSelected.setTextColor(Color.parseColor("#ffffff"));
    }


    //******************************listeners
    TextWatcher keywordWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            filter.keyword=s.toString();
            System.out.println(filter.keyword);
        }
    };

    CompoundButton.OnCheckedChangeListener popularChanged=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            filter.popular=isChecked;
        }
    };

    CompoundButton.OnCheckedChangeListener openChanged=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            filter.open=isChecked;
        }
    };

    View.OnClickListener limitOne=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.limit=10;
            limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),10));
            limitChanged(limit1);
        }
    };

    View.OnClickListener limitTwo=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.limit=20;
            limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),20));
            limitChanged(limit2);
        }
    };

    View.OnClickListener limitThree=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.limit=30;
            limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),30));
            limitChanged(limit3);
        }
    };

    View.OnClickListener limitFour=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.limit=40;
            limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),40));
            limitChanged(limit4);
        }
    };

    View.OnClickListener limitFive=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.limit=50;
            limitInfo.setText(String.format(getResources().getString(R.string.resultcountsectionlabel),50));
            limitChanged(limit5);
        }
    };

    View.OnClickListener kmClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.setDistanceType(PlaceFilter.DistanceSystem.km);
            distanceMin.setText(App.getDistanceString(PlaceFilter.DistanceSystem.km, App.minimumDistance));
            distanceMax.setText(App.getDistanceString(PlaceFilter.DistanceSystem.km, App.maximumDistance));
            distanceTickParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.kilometer_filter_fragment);
            distanceInfo.setText(getResources().getString(R.string.distanceunitsectionlabel) + " : " + getResources().getString(R.string.kilometerlabel));
            tick.setLayoutParams(distanceTickParams);
            float x=(distance.getProgress() * ((float) (App.maximumDistance - App.minimumDistance) / (100))) + App.minimumDistance;
            if(filter.metrics== PlaceFilter.DistanceSystem.ml)
                x=(float)(x*1.0936);
            lengthInfo.setText(getResources().getString(R.string.distancelabel)+" : "+App.getDistanceString(filter.metrics,x));
        }
    };

    View.OnClickListener mlClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filter.setDistanceType(PlaceFilter.DistanceSystem.ml);
            int min=(int)(App.minimumDistance * 1.09);
            distanceMin.setText(App.getDistanceString(PlaceFilter.DistanceSystem.ml, min));
            double max=App.maximumDistance*1.0936;
            distanceMax.setText(App.getDistanceString(PlaceFilter.DistanceSystem.ml, max));
            distanceTickParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.miles_filter_fragment);
            distanceInfo.setText(getResources().getString(R.string.distanceunitsectionlabel) + " : " + getResources().getString(R.string.mileslabel));
            tick.setLayoutParams(distanceTickParams);
            float x=(distance.getProgress() * ((float) (App.maximumDistance - App.minimumDistance) / (100))) + App.minimumDistance;
            if(filter.metrics== PlaceFilter.DistanceSystem.ml)
                x=(float)(x*1.0936);
            lengthInfo.setText(getResources().getString(R.string.distancelabel)+" : "+App.getDistanceString(filter.metrics,x));
        }
    };

    SeekBar.OnSeekBarChangeListener distanceChanged=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float x=(progress * ((float) (App.maximumDistance - App.minimumDistance) / (100))) + App.minimumDistance;
            if(filter.metrics== PlaceFilter.DistanceSystem.ml)
                x=(float)(x*1.0936);
            lengthInfo.setText(getResources().getString(R.string.distancelabel)+" : "+App.getDistanceString(filter.metrics,x));
            filter.setDistance(x);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    View.OnClickListener sortDistanceClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.distancelabel));
            filter.sorting= PlaceFilter.SortBy.distance;
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_distance_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }
    };

    View.OnClickListener sortRatingClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.ratingslabel));
            filter.sorting= PlaceFilter.SortBy.rating;
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_rating_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }
    };

    View.OnClickListener sortLikesClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sortInfo.setText(getResources().getString(R.string.sortsectionlabel)+" : "+getResources().getString(R.string.likeslabel));
            filter.sorting= PlaceFilter.SortBy.like;
            sortTickParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.sort_likes_filter_fragment);
            sortTick.setLayoutParams(sortTickParams);
        }
    };
}
