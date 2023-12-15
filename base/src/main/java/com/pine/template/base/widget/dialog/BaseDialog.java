package com.pine.template.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void showKeyboard(EditText editText) {
        if (editText != null) {
            //设置可获得焦点
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            //请求获得焦点
            editText.requestFocus();
            //调用系统输入法

            InputMethodManager imm = (InputMethodManager) editText
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    public void hideKeyboard(EditText editText) {
        if (editText != null) {
            //设置可获得焦点
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    public void show(boolean fullScreenMode) {
        if (fullScreenMode) {
            // show前先设置FLAG_NOT_FOCUSABLE，show后清除。避免全屏下软件盘弹出后收起造成状态栏和导航栏显示的问题
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        if (mAutoDismissIdleTime > 0) {
            mLastUserOperateTime = System.currentTimeMillis();
            mUserNoOperateHandler.sendEmptyMessageDelayed(0, mAutoDismissIdleTime);
        }
    }

    private volatile long mLastUserOperateTime;
    private volatile boolean mIgnoreUserOperate;
    private volatile long mAutoDismissIdleTime;
    private Handler mUserNoOperateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (!mIgnoreUserOperate) {
                long now = System.currentTimeMillis();
                long offset = now - mLastUserOperateTime;
                if (offset < mAutoDismissIdleTime) {
                    mUserNoOperateHandler.sendEmptyMessageDelayed(0, mAutoDismissIdleTime - offset);
                    return;
                }
            }
            if (isShowing()) {
                dismiss();
            }
        }
    };

    public void resetLastUserOperateTime(long lastUserOperateTime) {
        mLastUserOperateTime = lastUserOperateTime;
    }

    public void enableAutoDismiss(boolean ignoreUserOperate, long autoDismissIdleTime) {
        if (autoDismissIdleTime <= 0) {
            return;
        }
        mIgnoreUserOperate = ignoreUserOperate;
        mAutoDismissIdleTime = autoDismissIdleTime;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        mLastUserOperateTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchGenericMotionEvent(@NonNull MotionEvent ev) {
        mLastUserOperateTime = System.currentTimeMillis();
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        mLastUserOperateTime = System.currentTimeMillis();
        return super.dispatchKeyEvent(event);
    }
}
