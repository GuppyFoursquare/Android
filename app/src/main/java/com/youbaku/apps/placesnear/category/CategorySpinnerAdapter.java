//
//  CategorySpinnerAdapter
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
import android.widget.ImageView;
import android.widget.TextView;

import com.youbaku.apps.placesnear.R;
import com.squareup.picasso.Picasso;

public class CategorySpinnerAdapter extends ArrayAdapter {
    private CategoryList list;

    public CategorySpinnerAdapter(Context context, CategoryList list) {
        super(context, R.layout.category_spinner_item,R.id.textview_category_spinner_item,list.getList());
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size()-1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.category_spinner_item,null);
        }

        ImageView i=((ImageView)convertView.findViewById(R.id.imageview_category_spinner_item));
        Picasso.with(getContext())
                .load(list.get(position+1).iconURL)
                .placeholder(getContext().getResources().getDrawable(R.drawable.placeholder_category))
                .into(i);
        ((TextView) convertView.findViewById(R.id.textview_category_spinner_item)).setText(list.get(position+1).getName());

        return convertView;
    }
}
