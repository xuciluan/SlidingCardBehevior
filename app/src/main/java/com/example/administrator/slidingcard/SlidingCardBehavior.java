package com.example.administrator.slidingcard;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */
public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingCardLayout> {


    private int mInitialOffset;

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, SlidingCardLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

        //高度 = 父亲的给的高度 - header的高度
        int offset = getChildOffset(parent,child);
        int heightMeasureSpec = View.MeasureSpec.getSize(parentHeightMeasureSpec) - offset;
        child.measure(parentWidthMeasureSpec,View.MeasureSpec.makeMeasureSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY));
        return true;
    }

    private int getChildOffset(CoordinatorLayout parent, SlidingCardLayout child) {

        int offset = 0;
        int childCount = parent.getChildCount();
        for (int  i= 0 ; i < childCount ; i ++ ){
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingCardLayout){
                offset += ((SlidingCardLayout) view) .getHeaderHeight();
            }
        }
        return offset;
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, SlidingCardLayout child, int layoutDirection) {
        //先让父亲摆放好儿子们
        parent.onLayoutChild(child,layoutDirection);
        //此时它的高度 = 上一个控件的高度 + 上一个控件的Header的高度
        SlidingCardLayout previous = getPreviousChild(parent,child);
        if (previous != null){
            int offset = previous.getTop() + previous.getHeaderHeight();
            child.offsetTopAndBottom(offset);
        }
        mInitialOffset = child.getTop();
        return true;
    }

    private SlidingCardLayout getPreviousChild(CoordinatorLayout parent, SlidingCardLayout child) {
        int index = parent.indexOfChild(child);
        for (int i = index - 1 ; i >= 0 ; i --){
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout){
                return (SlidingCardLayout) view;
            }
        }
        return null;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull SlidingCardLayout child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes, int type) {

        boolean isVerticle = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return isVerticle && (child == target);
    }
}
