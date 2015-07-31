/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

/******************** PAGE DETAILS ********************/
/* @Programmer  : Kemal Sami KARACA
 * @Maintainer  : Guppy Org.
 * @Created     : 10/07/2015
 * @Modified    :
 * @Description :   This is the generic class for adapter.
 *                  It should be implemented by other adapters which are used
 *                  in Place.fetchGenericPlaceList() methods
 *
********************************************************/


package com.youbaku.apps.placesnear.adapter;

import java.util.List;

public interface Adapter {

    public void setAdapterList(List other);
    public List getAdapterList();
    public void notifyDataSetChanged();

}
