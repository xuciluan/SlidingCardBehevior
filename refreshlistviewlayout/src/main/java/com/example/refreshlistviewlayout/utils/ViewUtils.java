package com.example.refreshlistviewlayout.utils;

import android.content.Context;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */
public class ViewUtils {


    public static int dipToPx(Context context, float dip) {
        return context != null?roundUp(dip * context.getResources().getDisplayMetrics().density):0;
    }

    public static int roundUp(float f) {
        return (int)(0.5F + f);
    }
}
