package com.youbaku.apps.placesnear.adapter;

import java.util.List;

/**
 * Created by kemal on 21/07/15.
 */

public interface Adapter {

    public void setAdapterList(List other);
    public List getAdapterList();
    public void notifyDataSetChanged();

}
