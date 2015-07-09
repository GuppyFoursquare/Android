package com.youbaku.apps.placesnear.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.AnimatedExpandableListView;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orxan on 09.07.2015.
 */

public class ExpandableListviewAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;
    private List<Category> items;//Main Categories
    public static List<SubCategory> subitems;//Sub Categories
    private Context context;

    public ExpandableListviewAdapter(Context context,List<Category> items) {
        inflater = LayoutInflater.from(context);
        this.context=context;
        this.items = items;
        this.subitems=subitems;
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
    public View getRealChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        //SubCategory item = getChild(groupPosition, childPosition);
        final SubCategory item = getChild(groupPosition,childPosition);

        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.search_child_subcategory, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        String url2 = App.SitePath+"api/category.php?cat_id="+items.get(groupPosition).getObjectId();
        Toast.makeText(context,url2,Toast.LENGTH_LONG).show();
        JSONObject apiResponse = null;
        // Request a json response
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url2, apiResponse, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            JSONArray jArray = response.getJSONArray("content");
                            subitems=new ArrayList<SubCategory>();

                            //Read JsonArray
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject obj = jArray.getJSONObject(i);
                                final SubCategory s = new SubCategory();


                                s.setTitle(obj.getString("cat_name"));
                                s.setId(obj.getString("cat_id"));


                                subitems.add(s);
                                holder.title.setText(subitems.get(i).getTitle());
                                holder.hint.setText(s.getId());


                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add the request to the queue
        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);




        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        //return items.get(groupPosition).items.size();
        return 1;//subitems.size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
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

    private static class ChildItem {
        String title;
        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

}