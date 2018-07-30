package com.example.refreshlistviewlayout.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */
public class PullToRefreshView extends FrameLayout {

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DRAG_MAX_DISTANCE = 100;
    private static final int INVALID_POINTER = -1;

    private DecelerateInterpolator mDecelerateInterpolator;

    private int mTouchSlop;
    private int mTotalDragDistance;


    private LizhiFmRefreshView mRefreshView;

    private View mTarget;
    private LizhiRefreshResultView mResultView;


    /**
     * mCurrentOffsetTop1 表示距离刷新的头的高度
     */
    private int mCurrentOffsetTop1;
    private int mRefreshOffsetTop;
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private boolean isTopAnimation;
    private boolean isSecondMove;
    private boolean isRefreshComplete;
    private boolean mIsBeingDragged;
    private int mFrom;
    private int mActivePointerId;
    private float mInitialMotionY;


    public PullToRefreshView(@NonNull Context context) {
        this(context, null);
    }

    public PullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        float density = context.getResources().getDisplayMetrics().density;
        mTotalDragDistance = Math.round((float) DRAG_MAX_DISTANCE * density);

        mRefreshView = new LizhiFmRefreshView(context);
        mRefreshView.setRefreshListener(mRefreshListener);
        addView(mRefreshView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        mResultView = new LizhiRefreshResultView(context);
        addView(mResultView);

        //TODO 验证这两句话的效果
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }


    private RefreshListener mRefreshListener = new RefreshListener() {
        @Override
        public void onRefresh() {

        }

        @Override
        public void showResult() {

        }

        @Override
        public void onDone() {

        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ensureTarget();
        //将结果的view放置到最前方 TODO 这里可以试一下如果去掉那一句setChildrenDrawingOrderEnabled这一句是否会生效？
        mResultView.bringToFront();
    }

    /**
     * 给target进行赋值
     */
    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mRefreshView && child != mResultView) {
                    mTarget = child;
                }
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp()) {
            return false;
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentOffsetTop1 = 0;
                mRefreshOffsetTop = mCurrentOffsetTop;

                if (!mRefreshing) {
                    //不是刷新
                    isTopAnimation = false;
                    isSecondMove = false;
                } else {
                    //正在刷新
                    isSecondMove = true;
                }

                //刷新完成
                if (isRefreshComplete) {
                    mIsBeingDragged = false;
                    return false;
                }

                isRefreshComplete = false;

                setTargetOffsetTop(0, true);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }

                mInitialMotionY = initialMotionY;
                if (mCurrentOffsetTop > 0) {
                    mIsBeingDragged = true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }

                float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            //当有一根手指离开屏幕时调用
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            default:
                break;
        }

        return mIsBeingDragged;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(event);
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (isRefreshComplete){
                    return false;
                }

                if (isTopAnimation){
                    return false;
                }

                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    //TODO 这一段看不懂
    private void onSecondaryPointerUp(MotionEvent ev) {
        /***
         *  mAction时的低8位（也就是0-7位）是动作类型信息; mAction的8-15位呢，是触控点的索引信息
         *  如果mAction的值是0x0000，则表示是第一个触控点的ACTION_DOWN操作;
         *  如果mAction的值是0x0100呢，则表示是第二个触控点的ACTION_DOWN操作;
         *  如果mAction的值是0x0200呢，则表示是第三个触控点的ACTION_DOWN操作。
         *
         *  getActionIndex 返回 第几个触摸点；
         *  getPointerId(int pointerIndex)：pointerIndex从0到getPointerCount-1,返回一个触摸点的标
         */
        int pointerIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(pointerIndex);
        //todo 这里为什么是相等呢？？？？
        if (pointerId == mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = ev.findPointerIndex(activePointerId);
        if (index < 0) {
            return -1;
        }
        return ev.getY();
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        if (offset + mCurrentOffsetTop < 0) {
            offset = -mCurrentOffsetTop;
        }
        mTarget.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (mRefreshing) {
            mFrom = mCurrentOffsetTop;
            mCurrentOffsetTop1 = mRefreshOffsetTop - mCurrentOffsetTop;
        }

    }

    private boolean canChildScrollUp() {
        //正数表示向上滑动，负数表示向下滑动
        return mTarget.canScrollVertically(-1);
    }
}
