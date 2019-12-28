package com.pine.tool.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pine.tool.widget.IGestureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by tanghongfeng on 2019/10/10.
 */

public class FitChildScrollViewPager extends ViewPager {
    private boolean mCanScroll = true;//标记ViewPager是否允许滑动

    public FitChildScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public FitChildScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    public boolean isCanScroll() {
        return mCanScroll;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this) {
            if (v instanceof IGestureView) {
                IGestureView gestureView = (IGestureView) v;
                return gestureView.canScroll(checkV, dx, x, y);
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!mCanScroll) {
            return false;
        }
        return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mCanScroll) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }
}
