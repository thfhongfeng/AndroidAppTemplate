package com.pine.app.lib.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.tool.util.LogUtils;

public class SafeDragEditText extends androidx.appcompat.widget.AppCompatEditText {
    private final String TAG = this.getClass().getSimpleName();

    public SafeDragEditText(@NonNull Context context) {
        super(context);
    }

    public SafeDragEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeDragEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            // 避免在多行输入框内长按拖拽时，因为没有选中文本而出现报下面错误：
            // java.lang.IllegalStateException: Drag shadow dimensions must be positive
            //    at android.view.View.startDragAndDrop(View.java:28082)
            LogUtils.w(TAG, "onTouchEvent exception:" + e);
        }
        return false;
    }
}