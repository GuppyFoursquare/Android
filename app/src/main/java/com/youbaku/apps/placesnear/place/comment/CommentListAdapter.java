//
//  CommentListAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.R;

import java.util.ArrayList;

public class CommentListAdapter extends ArrayAdapter {
    private ArrayList<Comment> list;

    public CommentListAdapter(Context context, ArrayList<Comment> list) {
        super(context, R.layout.comment_list_item);
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
            convertView=inflater.inflate(R.layout.comment_list_item,null);
        }

        ImageView image=(ImageView)convertView.findViewById(R.id.image_comment_list_item);
        TextView comment=(TextView)convertView.findViewById(R.id.comment_comment_list_item);
        TextView info=(TextView)convertView.findViewById(R.id.info_comment_list_item);

        comment.setText(list.get(position).text);
        info.setText(list.get(position).getCreatedDate() + " , " + list.get(position).name);
        if(list.get(position).getUser_img()==""){
            image.setImageResource(R.drawable.placeholder_user);
        }
        else{
            Picasso.with(getContext())
                    .load(list.get(position).getUser_img())
                    .placeholder(R.drawable.placeholder_user)
                    .fit()
                    .into(image);
        }

        return convertView;
    }
}
