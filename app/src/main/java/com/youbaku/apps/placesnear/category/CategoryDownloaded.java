/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.category;

import com.youbaku.apps.placesnear.utils.Category;

import java.util.ArrayList;

public interface CategoryDownloaded {

    void categoryDownloaded(ArrayList<Category> list);
    void categoryDownloadError();
}
