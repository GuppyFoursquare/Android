/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place.favorites;

import com.youbaku.apps.placesnear.place.Place;

import java.util.ArrayList;

public interface FavoritesCallback {

    void favoritesReady(ArrayList<Place> places);
}
