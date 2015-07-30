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
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import java.util.ArrayList;

public class PhotoAdapter extends PagerAdapter {
    private ArrayList<Photo> arr;
    private Context context;
    ImageLoader mImageLoader;

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
        final NetworkImageView im= (NetworkImageView) item.findViewById(R.id.image_view_pager_item);
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

            /*Picasso.with(context)
                    .load("http://localhost/youbaku/uploads/places_images/large/"+arr.get(position).url)
                    .placeholder(R.drawable.placeholder_placelist)
                    .into(im);*/
            //Image Location
            String url = "http://youbaku.com/uploads/places_images/large/"+ arr.get(position).getUrl(); // URL of the image
            mImageLoader = VolleySingleton.getInstance().getImageLoader();
            im.setImageUrl(url,mImageLoader);


        ((ViewPager)container).addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
