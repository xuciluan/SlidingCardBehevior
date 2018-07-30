package com.example.refreshlistviewlayout.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.refreshlistviewlayout.R;
import com.example.refreshlistviewlayout.utils.ViewUtils;

import java.lang.reflect.TypeVariable;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */
public class LizhiFmRefreshView extends LinearLayout {

    /**
     * 要进行移动的view
     */
    private ImageView mLeavesImg;
    private ImageView mLeavesLeftImg;
    private ImageView mLeavesMidImg;
    private ImageView mLeavesRightImg;
    private ImageView mLizhiImg;

    private int MAX_MOVE_LENGHT;
    private TextView mStatusTextView;


    /**
     * 缩放比例
     */
    private static final float LEAVES_RATE = 0.9f;
    private static final float LEAVES_ONE_RATE = 0.85f;
    private static final float LIZHI_RATE = 0.9f;

    /**
     * 插值器
     */
    private AccelerateDecelerateInterpolator mAccelerateDecelerateInterpolator;
    private DecelerateInterpolator mDecelerateInterpolator;
    private AccelerateInterpolator mAccelerateInterpolator;

    private RefreshListener mRefreshListener;

    public LizhiFmRefreshView(Context context) {
        this(context,null);
    }

    public LizhiFmRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        MAX_MOVE_LENGHT = ViewUtils.dipToPx(context, 80);
        setOrientation(HORIZONTAL);
        inflate(context, R.layout.view_lizhi_refresh_header,this);
        setGravity(Gravity.CENTER_HORIZONTAL);

        mLeavesImg = findViewById(R.id.lizhi_refresh_leaves_img);
        mLeavesLeftImg = findViewById(R.id.lizhi_refresh_leaves_left_img);
        mLeavesMidImg = findViewById(R.id.lizhi_refresh_leaves_mid_img);
        mLeavesRightImg = findViewById(R.id.lizhi_refresh_leaves_right_img);
        mLizhiImg = findViewById(R.id.lizhi_refresh_lizhi_img);


        mStatusTextView = new TextView(context);
        mStatusTextView.setText("下拉刷新");
        mStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,10);
        LayoutParams params = new LayoutParams(ViewUtils.dipToPx(context,60), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ViewUtils.dipToPx(context, 20);
        addView(mStatusTextView,params);

        setScale(mLeavesImg,LEAVES_RATE);
        setScale(mLeavesLeftImg, LEAVES_ONE_RATE);
        setScale(mLeavesMidImg, LEAVES_ONE_RATE);
        setScale(mLeavesRightImg, LEAVES_ONE_RATE);
        setScale(mLizhiImg, LIZHI_RATE);

        mAccelerateInterpolator = new AccelerateInterpolator();
        mDecelerateInterpolator = new DecelerateInterpolator();
        mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    }

    /**
     * TODO 这里跟原来的代码不同
     * @param view
     * @param factor
     */
    private void setScale(View view,float factor){
        view.setScaleX(factor);
        view.setScaleY(factor);
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }
}
