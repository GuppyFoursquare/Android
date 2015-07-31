/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place.deal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.place.Place;
import com.youbaku.apps.placesnear.web.WebActivity;

import java.util.ArrayList;

public class DealListFragment extends ListFragment {
    private ArrayList<Deal> deals;

    public DealListFragment() {}

    public void setDeals(ArrayList<Deal> deals) {
        this.deals = deals;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App)getActivity().getApplication()).track(App.ANALYSIS_DEALS);

        if (deals == null){
            System.err.println("deals have to be declerad for DealtListFragment");
            return;
        }

        DealListAdapter adap=new DealListAdapter(getActivity(),deals);
        getListView().setDivider(new ColorDrawable(Color.parseColor(App.BackgroundGrayColor)));
        getListView().setDividerHeight(App.dpTopx(getActivity(), 20));
        getListView().setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        getListView().setSelector(new ColorDrawable(Color.parseColor("#00000000")));
        setListAdapter(adap);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(deals.get(position).url.length()<1)
            return;
        Intent in=new Intent(getActivity(), WebActivity.class);
        in.putExtra(WebActivity.COLOR, Color.parseColor(Place.FOR_DETAIL.color));
        in.putExtra(WebActivity.TITLE, deals.get(position).title);
        in.putExtra(WebActivity.URL,deals.get(position).url);
        in.putExtra(WebActivity.SUBTITLE,deals.get(position).url);
        startActivity(in);
    }
}
