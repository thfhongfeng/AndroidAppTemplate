package com.pine.template.base.browser;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;

public class WebControllerView extends FrameLayout {
    private static final String TAG = BrowserWebView.class.getSimpleName();

    private Context mContext;
    private Handler mHandler;

    private View rootView;
    private ImageView backIv, refreshIv, homeIv;

    private final int MIN_CLICK_INTERVAL = 500;
    private long mLastActionClickTime;

    private volatile WebBrowser mBrowser;
    private volatile BrowserWebView mWebView;
    private WebControllerListener mListener;

    public WebControllerView(Context context) {
        super(context);
        init(context);
    }

    public WebControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mContext = context;
        mHandler = new Handler();
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.web_controller_view, this, true);
        backIv = findViewById(R.id.iv_back);
        refreshIv = findViewById(R.id.iv_refresh);
        homeIv = findViewById(R.id.iv_home);

        backIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkViewPrepared()) {
                    return;
                }
                if (System.currentTimeMillis() - mLastActionClickTime < MIN_CLICK_INTERVAL) {
                    return;
                }
                LogUtils.d(TAG, "go back action click");
                if (mListener != null
                        && mListener.onControllerClick(backIv, WebControllerListener.CONTROLLER_TYPE_GO_BACK)) {
                    return;
                }
                mLastActionClickTime = System.currentTimeMillis();
                mWebView.goBackAction();
            }
        });
        refreshIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkViewPrepared()) {
                    return;
                }
                if (System.currentTimeMillis() - mLastActionClickTime < MIN_CLICK_INTERVAL) {
                    return;
                }
                LogUtils.d(TAG, "refresh action click");
                if (mListener != null
                        && mListener.onControllerClick(refreshIv, WebControllerListener.CONTROLLER_TYPE_REFRESH)) {
                    return;
                }
                mLastActionClickTime = System.currentTimeMillis();
                mBrowser.refresh();
            }
        });
        homeIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkViewPrepared()) {
                    return;
                }
                if (System.currentTimeMillis() - mLastActionClickTime < MIN_CLICK_INTERVAL) {
                    return;
                }
                LogUtils.d(TAG, "go home action click");
                if (mListener != null
                        && mListener.onControllerClick(homeIv, WebControllerListener.CONTROLLER_TYPE_GO_HOME)) {
                    return;
                }
                mLastActionClickTime = System.currentTimeMillis();
                mWebView.goHome();
            }
        });
        setVisibility(GONE);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 防止触摸穿透
                return true;
            }
        });
        setOnGenericMotionListener(new OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                // 防止鼠标点击穿透
                return true;
            }
        });
    }

    private boolean checkViewPrepared() {
        return mBrowser != null && mWebView != null && mWebView.isAttachedToWindow();
    }

    public void register(WebBrowser browser, WebControllerListener listener) {
        mBrowser = browser;
        mWebView = browser.getWebView();
        mWebView.addOnTouchListener(onTouchListener);
        mListener = listener;
    }

    public void unregister() {
        setVisibility(GONE);
        mListener = null;
        if (mWebView != null) {
            mWebView.removeOnTouchListener(onTouchListener);
            mWebView = null;
        }
        mBrowser = null;
    }

    public void enableGoBack(boolean enable) {
        backIv.setEnabled(enable);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!isAttachedToWindow()) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                setVisibility(VISIBLE);
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(hideRunnable, 5000);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(hideRunnable, 5000);
            }
            return false;
        }
    };

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        if (!isAttachedToWindow()) {
            return super.onHoverEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            setVisibility(VISIBLE);
            mHandler.removeCallbacksAndMessages(null);
        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(hideRunnable, 5000);
        }
        return super.onHoverEvent(event);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAttachedToWindow()) {
                return;
            }
            setVisibility(GONE);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        unregister();
        super.onDetachedFromWindow();
    }
}
