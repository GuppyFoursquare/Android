//
//  CommentListFragment
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place.comment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.youbaku.apps.placesnear.App;

import java.util.ArrayList;

public class CommentListFragment extends ListFragment {
    private ArrayList<Comment> comments;

    public CommentListFragment() {}

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((App)getActivity().getApplication()).track(App.ANALYSIS_REVIEWS);

        if (comments == null){
            System.err.println("commentlist have to be declerad for CommentListFragment");
            return;
        }
        CommentListAdapter adap=new CommentListAdapter(getActivity(), comments);

        getListView().setDivider(new ColorDrawable(Color.parseColor(App.BackgroundGrayColor)));
        getListView().setDividerHeight(App.dpTopx(getActivity(), 20));
        getListView().setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        getListView().setSelector(new ColorDrawable(Color.parseColor("#00000000")));
        setListAdapter(adap);
    }
}
