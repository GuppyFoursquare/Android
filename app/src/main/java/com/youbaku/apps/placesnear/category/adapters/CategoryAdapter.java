//
//  CategoryAdapter
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.category.SubCategoryListActivty;
import com.youbaku.apps.placesnear.utils.Category;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.category_list_item,null);
        }

        final Category c = list.get(position);
       /* TextView t=((TextView)convertView.findViewById(R.id.text_category_list_item));
        t.setText(c.getName());*/


        //Image Location
        final String url = "http://youbaku.com/uploads/category_images/"+c.iconURL; // URL of the image

        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        NetworkImageView image = (NetworkImageView)convertView.findViewById(R.id.image_category_list_item);

        image.setImageUrl(url,mImageLoader);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "başarılı" + position + "-" + list.get(position).objectId,
                        Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getContext(), SubCategoryListActivty.class);



              /* *************************************************
               *********************IMPORTANT *********************
              ******************************************************* */

                in.putExtra("CatId", list.get(position).objectId);
                in.putExtra("title", list.get(position).getTitle());
                in.putExtra("imageUrl", list.get(position).iconURL);
                Category.SELECTED_CATEGORY_ID = list.get(position).objectId;
                Category.SELECTED_IMAGE_URL=list.get(position).iconURL;


                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(in);


            }
        });



        return convertView;
    }
}
