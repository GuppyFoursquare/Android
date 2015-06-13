//
//  PhotoAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.photo;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.youbaku.apps.placesnear.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoAdapter extends PagerAdapter {
    private ArrayList<Photo> arr;
    private Context context;

    public PhotoAdapter(Context context,ArrayList<Photo> arr) {
        this.arr=arr;
        this.context=context;
    }

    @Override
    public int getCount() {
        if(arr==null || arr.size()<1)
            return 1;
        return arr.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((RelativeLayout)view)==((RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=inflater.inflate(R.layout.view_pager_item,container,false);
        final ImageView im=(ImageView)item.findViewById(R.id.image_view_pager_item);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display= windowManager.getDefaultDisplay();
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)im.getLayoutParams();
        int width=0;

        if(Build.VERSION.SDK_INT>12){
            Point s=new Point();
            display.getSize(s);
            width=s.x;
        }else{
            width=display.getWidth();
        }

        int height=(width/16)*9;
        params.height=height;
        im.setLayoutParams(params);
        if(arr==null || arr.size()<1){
            im.setImageResource(R.drawable.place_detail_image_placeholder);
        }else {
            Picasso.with(context)
                    .load(arr.get(position).url)
                    .placeholder(R.drawable.placeholder_placelist)
                    .into(im);
        }

        ((ViewPager)container).addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
