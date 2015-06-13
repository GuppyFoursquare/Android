package com.youbaku.apps.placesnear.category;

import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.R;

/**
 * Created by hsyn on 3/13/2015.
 */
public class FavoriteCategory extends Category {

    public FavoriteCategory() {}

    @Override
    public String getName() {
        return MainActivity.tt.getResources().getString(R.string.favoritedCategoryName);
    }
}
