package com.youbaku.apps.placesnear.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.youbaku.apps.placesnear.AnimatedExpandableListView;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.category.adapters.SubCategoryAdapter;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orxan on 09.07.2015.
 */

public class ExpandableListviewAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;
    private List<Category> items;//Main Categories
    public static ArrayList<SubCategory> subitems;//Sub Categories
    private Context context;
    private SubCategoryAdapter adap;
    private GridView gv;
    private ImageLoader mImageLoader;

    public ExpandableListviewAdapter(Context context, List<Category> items) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.setItems(items);
        this.subitems = subitems;
    }

    @Override
    public SubCategory getChild(int groupPosition, int childPosition) {
        //return items.get(groupPosition).items.get(childPosition);
        // return subitems.get(childPosition);

        // GET PRODUCT LIST FROM SERVER
        // HTTP CALL
        /*SubCategory myProduct = new SubCategory();
        myProduct.setTitle("Orxan");
        myProduct.setId("13");
        return myProduct;*/

        return null;//subitems.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final ChildHolder holder;

        //SubCategory item = getChild(groupPosition, childPosition);
        final SubCategory item = getChild(groupPosition, childPosition);

        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.search_child_subcategory, parent, false);
            //holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            // holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            convertView.setTag(holder);


        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        final View finalConvertView1 = convertView;

        subitems =  Category.categoryList.get(groupPosition).subCatList;
//         holder.hint.setText(s.getId());

        adap = new SubCategoryAdapter(parent.getContext(),subitems);
        gv = (GridView) finalConvertView1.findViewById(R.id.subGV);
        gv.setAdapter(adap);



        // initialize the following variables (i've done it based on your layout
        // note: rowHeightDp is based on my grid_cell.xml, that is the height i've
        //    assigned to the items in the grid.
        final int spacingDp = 1;
        final int colWidthDp = 50;
        final int rowHeightDp = 20;

        // convert the dp values to pixels
        final float COL_WIDTH = parent.getContext().getResources().getDisplayMetrics().density * colWidthDp;
        final float ROW_HEIGHT = parent.getContext().getResources().getDisplayMetrics().density * rowHeightDp;
        final float SPACING = parent.getContext().getResources().getDisplayMetrics().density * spacingDp;

        // calculate the column and row counts based on your display
        //final int colCount = (int)Math.floor((parent.getWidth() - (2 * SPACING)) / (COL_WIDTH + SPACING));
        final int rowCount = (int) Math.ceil((subitems.size()));

        // calculate the height for the current grid
        final int GRID_HEIGHT = Math.round(rowCount * (ROW_HEIGHT + SPACING));

        // set the height of the current grid
        // gv.getLayoutParams().height = GRID_HEIGHT;
        gv.getLayoutParams().height = GRID_HEIGHT;


        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        //return items.get(groupPosition).items.size();
        return 1;
    }

    @Override
    public Category getGroup(int groupPosition) {
        return getItems().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return getItems()!=null ? getItems().size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        Category item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.search_group_main_category, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        //Setting Image
        final String url = "http://youbaku.com/uploads/category_images/" + item.iconURL; // URL of the image
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.catImg);
        image.setImageUrl(url, mImageLoader);

        holder.title.setText(item.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public List<Category> getItems() {
        return items;
    }

    public void setItems(List<Category> items) {
        this.items = items;
    }


    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

}