package com.youbaku.apps.placesnear.category.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.toolbox.ImageLoader;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.utils.SubCategory;

import java.util.ArrayList;

/**
 * Created by orxan on 17.06.2015.
 */
public class SubCategoryAdapter extends ArrayAdapter<SubCategory> {
    private ArrayList<SubCategory> list;

    private ImageLoader mImageLoader;

    public SubCategoryAdapter(Context context, ArrayList<SubCategory> list) {
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
            convertView=inflater.inflate(R.layout.sub_category_list_item,null);
        }

        final SubCategory c = list.get(position);
        Button t=((Button)convertView.findViewById(R.id.subCatTitle));
        t.setBackgroundColor(Color.parseColor(App.ButtonColor));
        t.setText(c.getTitle());

        return convertView;
    }
}
