package com.pine.template.base.browser;

import static com.pine.template.base.browser.ScreenSaverConfig.TYPE_IMAGE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.tool.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScreenSaverView extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();

    private int mType = -1;

    private Context mContext;

    private CarouselImgView mCarouselImgView;

    private String mScreenSaverPath;

    public ScreenSaverView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ScreenSaverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenSaverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mContext = context;
    }

    private static long TIMEOUT_MS = 300000; // 5 minutes
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private long mLastInteractionTime;

    public void init(View listenView, int type, String sourceFilePath, long idleTime) {
        LogUtils.d(TAG, "init type:" + type + ",idleTime:" + idleTime + ",sourceFilePath:" + sourceFilePath);
        mScreenSaverPath = sourceFilePath;
        TIMEOUT_MS = idleTime;
        mType = type;
        release(listenView);
        switch (mType) {
            case TYPE_IMAGE:
                mCarouselImgView = new CarouselImgView(getContext());
                addView(mCarouselImgView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mCarouselImgView.init(getImgListPath());
                break;
        }

        // 启动定时器
        mMainHandler.postDelayed(mIdleRunnable, TIMEOUT_MS);

        View rootView = getChildAt(0);
        if (rootView == null) {
            rootView = this;
        }
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScreenSaver();
            }
        });
        listenView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateLastInteractionTime();
                return false;
            }
        });
        listenView.setOnGenericMotionListener(new OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                updateLastInteractionTime();
                return false;
            }
        });
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

    private List<String> getImgListPath() {
        File imgDir = new File(mScreenSaverPath);
        List<String> list = new ArrayList<>();
        if (imgDir.exists() && imgDir.isDirectory()) {
            File[] files = imgDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        list.add(file.getAbsolutePath());
                    }
                }
            }
        }
        LogUtils.d(TAG, "find images for screensaver:" + list.size());
        return list;
    }

    public void release(View view) {
        LogUtils.d(TAG, "release");
        if (mCarouselImgView != null) {
            mCarouselImgView.release();
            mCarouselImgView = null;
        }
        mMainHandler.removeCallbacksAndMessages(null);
        removeAllViews();
    }

    private Runnable mIdleRunnable = new Runnable() {
        @Override
        public void run() {
            long lastInteractionTime = getLastInteractionTime(); // 获取用户最后一次与 WebView 交互的时间
            long idleTime = System.currentTimeMillis() - lastInteractionTime;
            if (idleTime >= TIMEOUT_MS) {
                LogUtils.d(TAG, "time for screensaver, startScreenSaver idleTime:" + idleTime);
                // 用户长时间未进行任何操作，执行相应的操作
                startScreenSaver();
            } else {
                // 继续检查用户是否长时间未进行任何操作
                mMainHandler.postDelayed(mIdleRunnable, TIMEOUT_MS - idleTime);
            }
        }
    };

    private void updateLastInteractionTime() {
        mLastInteractionTime = System.currentTimeMillis();
    }

    private long getLastInteractionTime() {
        return mLastInteractionTime;
    }

    public void startScreenSaver() {
        switch (mType) {
            case TYPE_IMAGE:
                if (mCarouselImgView != null) {
                    mCarouselImgView.start();
                }
                break;
        }
        setVisibility(VISIBLE);
    }

    public void stopScreenSaver() {
        setVisibility(GONE);
        switch (mType) {
            case TYPE_IMAGE:
                if (mCarouselImgView != null) {
                    mCarouselImgView.stop();
                }
                break;
        }
        // 继续检查用户是否长时间未进行任何操作
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.postDelayed(mIdleRunnable, TIMEOUT_MS);
    }

    public interface IOnScreenSaverCallback {
        void onScreenSaver(boolean onScreen);
    }
}
