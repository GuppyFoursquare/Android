package com.youbaku.apps.placesnear.photo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by hsyn on 3/7/2015.
 */
public class SlideAdapter extends PagerAdapter {
    private ArrayList<Photo> arr;
    private Context context;
    ImageLoader mImageLoader;

    public SlideAdapter(Context context, ArrayList<Photo> arr) {
        this.arr=arr;
        this.context=context;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((RelativeLayout)view)==((RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=inflater.inflate(R.layout.slide_view_pager_item,container,false);
        final NetworkImageView im=(NetworkImageView)item.findViewById(R.id.image_view_pager_item);

       /* Picasso.with(context)
                .load(arr.get(position).url)
                .placeholder(R.drawable.placeholder_placelist)
                .into(im);*/


        //Image Location
        String url = "http://youbaku.com/uploads/places_images/large/"+ arr.get(position).getUrl(); // URL of the image
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        im.setImageUrl(url, mImageLoader);

        ((ViewPager)container).addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}