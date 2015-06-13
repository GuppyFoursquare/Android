package com.youbaku.apps.placesnear.photo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.youbaku.apps.placesnear.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hsyn on 3/7/2015.
 */
public class SlideAdapter extends PagerAdapter {
    private ArrayList<Photo> arr;
    private Context context;

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
        final ImageView im=(ImageView)item.findViewById(R.id.image_view_pager_item);

        Picasso.with(context)
                .load(arr.get(position).url)
                .placeholder(R.drawable.placeholder_placelist)
                .into(im);

        ((ViewPager)container).addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}