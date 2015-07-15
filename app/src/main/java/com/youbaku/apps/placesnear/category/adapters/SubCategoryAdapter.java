package com.youbaku.apps.placesnear.category.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.volley.toolbox.ImageLoader;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.sub_category_list_item,null);
            viewHolder = new ViewHolder();

            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkedText);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.checkedText, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        viewHolder.checkbox.setText(list.get(position).getTitle());
        viewHolder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }
    static class ViewHolder {
        protected CheckBox checkbox;
    }

}