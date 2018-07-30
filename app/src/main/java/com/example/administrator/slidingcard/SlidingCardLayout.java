package com.example.administrator.slidingcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */

@CoordinatorLayout.DefaultBehavior(SlidingCardBehavior.class)
public class SlidingCardLayout extends LinearLayout {


    private TextView mHeader;
    private int mHeaderHeight;
    private RecyclerView rvList;


    public SlidingCardLayout(Context context) {
        this(context, null);
    }

    public SlidingCardLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingCardLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_slidingcardlayout, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingCardLayout, defStyleAttr, 0);
        String title = typedArray.getString(R.styleable.SlidingCardLayout_title);
        mHeader = findViewById(R.id.header);
        rvList = findViewById(R.id.rv_list);
        mHeader.setText(title);
        CharSequence[] textArray = typedArray.getTextArray(R.styleable.SlidingCardLayout_array);
        SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(textArray);
        rvList.setAdapter(simpleTextAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeaderHeight = mHeader.getMeasuredHeight();
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }
}
