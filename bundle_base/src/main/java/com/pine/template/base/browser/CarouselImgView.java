package com.pine.template.base.browser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;

import java.util.List;

public class CarouselImgView extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView mCurImgView, mLastImgView;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private List<String> mImageList;
    private volatile int mCurIndex;
    private int mCarouseInterval = 10000;

    private boolean mActive = false;
    private Animation mShowAction, mHiddenAction;

    public CarouselImgView(@NonNull Context context) {
        super(context);
    }

    public CarouselImgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CarouselImgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean checkPrepared() {
        return mImageList != null && mImageList.size() > 0;
    }

    public void init(List<String> imgFilePaths) {
        mImageList = imgFilePaths;
        if (!checkPrepared()) {
            return;
        }
        removeAllViews();
        mCurIndex = 0;
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.screen_saver_carouse_img_view, this, true);
        imageView1 = rootView.findViewById(R.id.image_iv_1);
        imageView2 = rootView.findViewById(R.id.image_iv_2);
        mCurImgView = imageView1;
        mLastImgView = imageView2;
        loadImage(mCurImgView);
        mShowAction = AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_in);
        mHiddenAction = AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_out);
    }

    public void start() {
        if (!checkPrepared()) {
            stop();
            return;
        }
        LogUtils.d(TAG, "start carousel image screen saver");
        mMainHandler.removeCallbacksAndMessages(null);
        if (mImageList.size() == 1) {
            showImgView(mCurImgView);
        } else {
            mMainHandler.post(mCarouseRunnable);
        }
        mActive = true;
    }

    public void stop() {
        LogUtils.d(TAG, "stop carousel image screen saver");
        mMainHandler.removeCallbacksAndMessages(null);
        mActive = false;
    }

    public void release() {
        LogUtils.d(TAG, "release carousel image screen saver");
        mMainHandler.removeCallbacksAndMessages(null);
        if (mCurImgView != null) {
            mCurImgView.clearAnimation();
        }
        if (mLastImgView != null) {
            mLastImgView.clearAnimation();
        }
        mActive = false;
    }

    private Runnable mCarouseRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActive) {
                showImgView(mCurImgView);
                hideImgView(mLastImgView);
                mMainHandler.removeCallbacks(mBackLoadRunnable);
                mMainHandler.postDelayed(mBackLoadRunnable, mCarouseInterval / 2);
            }
            mMainHandler.postDelayed(mCarouseRunnable, mCarouseInterval);
        }
    };

    private Runnable mBackLoadRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActive) {
                loadImage(mLastImgView);
                ImageView temp = mLastImgView;
                mLastImgView = mCurImgView;
                mCurImgView = temp;
            }
        }
    };

    private void showImgView(View view) {
        view.setVisibility(VISIBLE);
        view.startAnimation(mShowAction);
    }

    private void hideImgView(View view) {
        view.setVisibility(GONE);
        view.startAnimation(mHiddenAction);
    }

    private void loadImage(ImageView imageView) {
        ImageLoaderManager.getInstance().loadImage(getContext(), mImageList.get(mCurIndex++ % mImageList.size()), imageView);
    }
}
