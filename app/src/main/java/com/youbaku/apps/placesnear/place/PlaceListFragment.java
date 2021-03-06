/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

/******************** PAGE DETAILS ********************/
/* @Programmer  : Kemal Sami KARACA
 * @Maintainer  : Guppy Org.
 * @Created     : 10/07/2015
 * @Modified    :
 * @Description :
********************************************************/

package com.youbaku.apps.placesnear.place;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import com.youbaku.apps.placesnear.App;
import java.util.ArrayList;


/**
 * This is the generic class for place list fragment which is used by
 *  - PopularPlaceFragment
 *  - ...
 *
 */
public class PlaceListFragment extends ListFragment {
    private ArrayList<Place> list;
    private PlaceAdapter adap;
    private String themeColor="";
    private AdapterView.OnItemClickListener listen;
    private boolean empty=false;
    private String emptyText="";

    public PlaceListFragment() {}

    public void setList(ArrayList<Place> list){
        this.list=list;
    }

    public void setColor(String color){
        themeColor=color;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(list==null){
            System.err.println("CategoryListFragment.setList should be used before fragment used on screen");
            return;
        }
        setAdapter(new PlaceAdapter(getActivity(),list, Color.parseColor(themeColor)));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //((App)getActivity().getApplicationContext()).track(App.ANALYSIS_PLACES);

        getListView().setDivider(new ColorDrawable(Color.parseColor(App.BackgroundGrayColor)));
        getListView().setDividerHeight(App.dpTopx(getActivity(), 20));
        getListView().setSelector(new ColorDrawable(Color.parseColor("#00000000")));
        setListAdapter(getAdapter());
        if(listen!=null)
            getListView().setOnItemClickListener(listen);
        if(empty)
            super.setEmptyText(emptyText);

    }

    @Override
    public void setEmptyText(CharSequence text) {
        if (isVisible())
            super.setEmptyText(text);
        else{
            empty=true;
            emptyText=text.toString();
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listen){
        this.listen=listen;
    }


    public PlaceAdapter getAdapter() {
        return adap;
    }

    public void setAdapter(PlaceAdapter adap) {
        this.adap = adap;
    }
}
