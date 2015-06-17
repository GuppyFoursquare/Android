//
//  CategoryDownloaded
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category;

import com.youbaku.apps.placesnear.utils.Category;

import java.util.ArrayList;

/**
 * Created by hsyn on 2/5/2015.
 */
public interface CategoryDownloaded {

    void categoryDownloaded(ArrayList<Category> list);
    void categoryDownloadError();
}
