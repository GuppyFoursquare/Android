/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.content.Context;
import android.widget.FrameLayout;
import android.view.MotionEvent;

/**
 * Created by kemal on 07/07/15.
 */
public class NearMeTouchableWrapper extends FrameLayout {

    public static boolean mMapIsTouched;

    public NearMeTouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapIsTouched=true;
                break;

            case MotionEvent.ACTION_UP:
                mMapIsTouched=false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

}
