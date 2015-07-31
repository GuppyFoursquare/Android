/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

public abstract class AbSpinDrawable extends Drawable implements Drawable.Callback {

    protected Paint mPaint;
    @Override
    public void invalidateDrawable(Drawable who) {
        if(Build.VERSION.SDK_INT>10) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(who);
            }
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if(Build.VERSION.SDK_INT>10) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.scheduleDrawable(who, what, when);
            }
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if(Build.VERSION.SDK_INT>10) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.unscheduleDrawable(who, what);
            }
        }
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

}