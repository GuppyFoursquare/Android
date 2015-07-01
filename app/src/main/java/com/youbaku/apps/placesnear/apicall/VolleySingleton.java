
/******************** PAGE DETAILS ********************/
/* @Programmer  : Guppy Org.
 * @Maintainer  : Guppy Org.
 * @Created     : 13/06/15
 * @Modified    :
 * @Description : This is the api call handler class
********************************************************/

package com.youbaku.apps.placesnear.apicall;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.youbaku.apps.placesnear.MyApplication;

public class VolleySingleton {

    private static VolleySingleton instance;
    private static RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = null;

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);


            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }

    public static VolleySingleton getInstance() {

        if (instance == null) {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue != null) {
            return requestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
