//
//  CategoryAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private ArrayList<Category> list;
    private ImageLoader mImageLoader;

    public CategoryAdapter(Context context, ArrayList<Category> list) {
        super(context, R.layout.category_list_item);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.category_list_item,null);
        }

        Category c = list.get(position);
        TextView t=((TextView)convertView.findViewById(R.id.text_category_list_item));
        t.setText(c.getName());


        //Image Location
        String url = "http://youbaku.com/uploads/category_images/"+c.iconURL; // URL of the image

        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        NetworkImageView image = (NetworkImageView)convertView.findViewById(R.id.image_category_list_item);

        image.setImageUrl(url,mImageLoader);




        /*t.setTextColor(Color.parseColor(App.CategoryListLabelColor));
        ImageView i=((ImageView)convertView.findViewById(R.id.image_category_list_item));
        int px=App.dpTopx(getContext(),100);
        Drawable dr=getContext().getResources().getDrawable(R.drawable.placeholder_category);
        Bitmap b=Bitmap.createScaledBitmap(((BitmapDrawable)dr).getBitmap(),px,px,false);
        if(position!=0)
            Picasso.with(getContext())
                    .load(list.get(position).iconURL)
                    .placeholder(new BitmapDrawable(getContext().getResources(),b))
                    .resize(px,px)
                    .into(i);
        else
            Picasso.with(getContext())
                    .load(R.drawable.favoritescategoryicon)
                    .placeholder(new BitmapDrawable(getContext().getResources(),b))
                    .resize(px,px)
                    .into(i);*/

        return convertView;
    }
}
