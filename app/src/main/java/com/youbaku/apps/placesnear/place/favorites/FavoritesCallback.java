package com.youbaku.apps.placesnear.place.favorites;

import com.youbaku.apps.placesnear.place.Place;

import java.util.ArrayList;

/**
 * Created by hsyn on 3/14/2015.
 */
public interface FavoritesCallback {

    void favoritesReady(ArrayList<Place> places);
}
