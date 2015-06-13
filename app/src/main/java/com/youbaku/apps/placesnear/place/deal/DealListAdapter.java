//
//  DealListAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.deal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youbaku.apps.placesnear.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealListAdapter extends ArrayAdapter {
    ArrayList<Deal> deals;
    public DealListAdapter(Context context, ArrayList<Deal> deals) {
        super(context, R.layout.deal_list_item);
        this.deals=deals;
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.deal_list_item,null);
        }
        ImageView image=(ImageView)convertView.findViewById(R.id.image_deal_list_item);
        TextView title=(TextView)convertView.findViewById(R.id.title_deal_list_item);
        TextView info=(TextView)convertView.findViewById(R.id.info_deal_list_item);
        TextView text=(TextView)convertView.findViewById(R.id.text_deal_list_item);

        title.setText(deals.get(position).title);
        info.setText(deals.get(position).getDates());
        text.setText(deals.get(position).description);
        Picasso.with(getContext())
                .load(deals.get(position).photo)
                .placeholder(R.drawable.placeholder_photo_thumbnail)
                .fit()
                .into(image);

        return convertView;
    }
}
