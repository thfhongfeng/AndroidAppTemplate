package com.pine.template.base.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pine.template.base.R;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class ViewPagerTabLayout extends TabLayout {

    private Drawable mDivider;
    private int mShowDividers;
    private int mLeftMargin;
    private int mTopMargin;
    private int mRightMargin;
    private int mBottomMargin;
    private int mStartMargin;
    private int mEndMargin;
    private Drawable mBottomDivider;
    private Paint mPaint;

    public ViewPagerTabLayout(Context context) {
        this(context, null);
    }

    public ViewPagerTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseViewPagerTabLayout, defStyleAttr, 0);
        mDivider = typedArray.getDrawable(R.styleable.BaseViewPagerTabLayout_base_divider);
        mShowDividers = typedArray.getInt(R.styleable.BaseViewPagerTabLayout_base_showDividers, 0);
        int margin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_margin, -1);
        if (margin >= 0) {
            mLeftMargin = margin;
            mTopMargin = margin;
            mRightMargin = margin;
            mBottomMargin = margin;
        } else {
            mLeftMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginLeft, 0);
            mRightMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginRight, 0);
            mTopMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginTop, 0);
            mBottomMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginBottom, 0);
            mStartMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginStart, 0);
            mEndMargin = typedArray.getDimensionPixelSize(R.styleable.BaseViewPagerTabLayout_base_marginEnd, 0);
        }
        mBottomDivider = typedArray.getDrawable(R.styleable.BaseViewPagerTabLayout_base_bottomDivider);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
        View view = getChildAt(0);
        if (view != null && view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;
            layout.setDividerDrawable(mDivider);
            layout.setShowDividers(mShowDividers);
            int count = layout.getChildCount();
            for (int i = 0; i < count; i++) {
                View tabChild = layout.getChildAt(i);
                LinearLayout.LayoutParams lps = (LinearLayout.LayoutParams) tabChild.getLayoutParams();
                lps.setMargins(mLeftMargin, mTopMargin, mRightMargin, mBottomMargin);
                lps.setMarginStart(mStartMargin);
                lps.setMarginEnd(mEndMargin);
                tabChild.requestLayout();
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBottomDivider != null) {
            mBottomDivider.setBounds(0, getHeight() - mBottomDivider.getIntrinsicHeight(), getWidth(), getHeight());
            mBottomDivider.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }
}

