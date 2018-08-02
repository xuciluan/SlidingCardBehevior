package com.example.administrator.slidingcard;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
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
        int offset = getChildOffset(parent, child);
        int heightMeasureSpec = View.MeasureSpec.getSize(parentHeightMeasureSpec) - offset;
        child.measure(parentWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY));
        Log.e("xcl", "child.measure called");
        return true;
    }

    private int getChildOffset(CoordinatorLayout parent, SlidingCardLayout child) {

        int offset = 0;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingCardLayout) {
                offset += ((SlidingCardLayout) view).getHeaderHeight();
            }
        }
        return offset;
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, SlidingCardLayout child, int layoutDirection) {
        //先让父亲摆放好儿子们
        parent.onLayoutChild(child, layoutDirection);
        Log.e("xcl", "parent.onLayoutChild called");
        //此时它的高度 = 上一个控件的高度 + 上一个控件的Header的高度
        SlidingCardLayout previous = getPreviousChild(parent, child);
        if (previous != null) {
            int offset = previous.getTop() + previous.getHeaderHeight();
            child.offsetTopAndBottom(offset);
        }
        mInitialOffset = child.getTop();
        return true;
    }

    private SlidingCardLayout getPreviousChild(CoordinatorLayout parent, SlidingCardLayout child) {
        int index = parent.indexOfChild(child);
        for (int i = index - 1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
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
        return isVerticle && (child == directTargetChild);
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout parent, @NonNull SlidingCardLayout child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //1. 控制自己的移动
        // 注意 : offTopAndBottom 传递的参数不是相对滑动，而是距离屏幕顶部的高度
       consumed[1] = scroll(child, dy, mInitialOffset, mInitialOffset + child.getHeight() - child.getHeaderHeight());
        // 2. 控制自己上边和下边的滑动
        shiftSlidings(consumed[1],parent,child);
       // super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout parent, @NonNull SlidingCardLayout child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        //1. 控制自己的移动
        // 注意 : offTopAndBottom 传递的参数不是相对滑动，而是距离屏幕顶部的高度
        int shift = scroll(child, dyUnconsumed, mInitialOffset, mInitialOffset + child.getHeight() - child.getHeaderHeight());
        // 2. 控制自己上边和下边的滑动
        shiftSlidings(shift,parent,child);
        //super.onNestedScroll(parent, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    private void shiftSlidings(int shift, CoordinatorLayout parent, SlidingCardLayout child) {
        if (shift == 0) {
            return;
        }

        if (shift > 0) {
            //向上
            //向下
            //推动下面所有的卡片
            //找到下面所有的卡片
            SlidingCardLayout current = child;
            SlidingCardLayout card = getPreviousChild(parent, current);
            while (card != null) {
                int offset = getHeaderOveralap(card, current);
                card.offsetTopAndBottom(-offset);
                current = card;
                card = getPreviousChild(parent, current);
            }
        } else {
            //向下
            //推动下面所有的卡片
            //找到下面所有的卡片
            SlidingCardLayout current = child;
            SlidingCardLayout card = getNextChild(parent, current);
            while (card != null) {
                int offset = getHeaderOveralap(current, card);
                card.offsetTopAndBottom(offset);
                current = card;
                card = getNextChild(parent, current);
            }
        }
    }

    private int getHeaderOveralap(SlidingCardLayout above, SlidingCardLayout belw) {

        return above.getTop() + above.getHeaderHeight() - belw.getTop();
    }

    private SlidingCardLayout getNextChild(CoordinatorLayout parent, SlidingCardLayout current) {
        int index = parent.indexOfChild(current);
        for (int i = index + 1; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
                return (SlidingCardLayout) view;
            }
        }
        return null;

    }

    private int scroll(SlidingCardLayout child, int dyUnconsumed, int minOffset, int maxOffset) {
        int initialOffset = child.getTop();
        //dy是负数，此时要向下滑动，是正数，所以要进行相减
        int offset = clamp(initialOffset - dyUnconsumed, minOffset, maxOffset) - mInitialOffset;
        child.offsetTopAndBottom(offset);
        return -offset;//负数表示是往下滑动，正数表示向上滑动 ； 此时offset是负数，所以要向上滑动
    }


    private int clamp(int i, int minOffset, int maxOffset) {
        if (i > maxOffset) {
            return maxOffset;
        } else if (i < minOffset) {
            return minOffset;
        } else {
            return i;
        }
    }


}
