package com.example.refreshlistviewlayout.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.example.refreshlistviewlayout.R;
import com.example.refreshlistviewlayout.utils.ViewUtils;

/**
 * Author : xuciluan
 * Time : 2018/7/30.
 * Email : xuciluan@lizhi.fm
 * Desc :
 * version : v1.0
 */
public class LizhiRefreshResultView extends AppCompatTextView {


    private final int RESULT_VIEW_HEIGHT = ViewUtils.dipToPx(getContext(), 28);

    public LizhiRefreshResultView(Context context) {
        this(context,null);
    }

    public LizhiRefreshResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setBackgroundResource(R.drawable.refresh_result_view_bg);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_PX,12);
        setTextColor(getResources().getColor(android.R.color.white));

        setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,RESULT_VIEW_HEIGHT));
        setTranslationY(- RESULT_VIEW_HEIGHT);
        setText("刷新完成");
    }
}
