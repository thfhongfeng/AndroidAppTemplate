package com.pine.template.base.architecture.mvvm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;

import com.pine.template.base.R;
import com.pine.template.base.manager.MultiClickHelper;
import com.pine.template.base.manager.tts.TtsManager;
import com.pine.tool.architecture.mvvm.ui.MvvmActivity;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class BaseMvvmActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends MvvmActivity<T, VM> {

    private MultiClickHelper mMultiClickHelper;
    private boolean mEnableLoadingUiClickGoneDefault;
    private Handler mLoadingUiTimeoutHandler = new Handler(Looper.getMainLooper());
    private int mLoadingUiTimeoutGone;

    private int mEnterAnim = R.anim.base_anim_fade_in;
    private int mExitAnim = R.anim.base_anim_fade_out;

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        if (mEnterAnim > 0 && mExitAnim > 0) {
            // 设置进入和退出动画
            overridePendingTransition(mEnterAnim, mExitAnim);
            LogUtils.d("ActivityLifecycle", this + " overridePendingTransition");
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (mEnterAnim > 0 && mExitAnim > 0) {
            // 设置进入和退出动画
            overridePendingTransition(mEnterAnim, mExitAnim);
            LogUtils.d("ActivityLifecycle", this + " overridePendingTransition");
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (mEnterAnim > 0 && mExitAnim > 0) {
            // 设置进入和退出动画
            overridePendingTransition(mEnterAnim, mExitAnim);
            LogUtils.d("ActivityLifecycle", this + " overridePendingTransition");
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (mEnterAnim > 0 && mExitAnim > 0) {
            // 设置进入和退出动画
            overridePendingTransition(mEnterAnim, mExitAnim);
            LogUtils.d("ActivityLifecycle", this + " overridePendingTransition");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setPendingTransition(int enterAnim, int exitAnim) {
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
    }

    protected int getLoadingUiResId() {
        return R.layout.base_loading;
    }

    public void setLoadingUiVisibility(boolean visibility, boolean enableClickGone) {
        if (mMultiClickHelper == null) {
            mMultiClickHelper = new MultiClickHelper();
        }
        if (enableClickGone) {
            mMultiClickHelper.regMultiClick(findViewById(R.id.base_loading_layout), 400, 9,
                    new MultiClickHelper.IMultiClickListener() {
                        @Override
                        public void onMultiClick(View view) {
                            findViewById(R.id.base_loading_layout).setVisibility(View.GONE);
                        }
                    });
        } else {
            mMultiClickHelper.unRegMultiClick(findViewById(R.id.base_loading_layout));
        }
        hideSoftInputFromWindow();
        findViewById(R.id.base_loading_layout).setVisibility(visibility ? View.VISIBLE : View.GONE);
        if (visibility && mLoadingUiTimeoutGone > 0) {
            mLoadingUiTimeoutHandler.removeCallbacksAndMessages(null);
            mLoadingUiTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.base_loading_layout).setVisibility(View.GONE);
                }
            }, mLoadingUiTimeoutGone);
        }
    }

    public void configLoadingUi(boolean enableClickGoneDefault) {
        configLoadingUi(enableClickGoneDefault, mLoadingUiTimeoutGone);
    }

    public void configLoadingUi(int timeoutGone) {
        configLoadingUi(mEnableLoadingUiClickGoneDefault, timeoutGone);
    }

    public void configLoadingUi(boolean enableClickGoneDefault, int timeoutGone) {
        mEnableLoadingUiClickGoneDefault = enableClickGoneDefault;
        mLoadingUiTimeoutGone = timeoutGone;
    }

    public void setLoadingUiVisibility(boolean visibility) {
        setLoadingUiVisibility(visibility, mEnableLoadingUiClickGoneDefault);
    }

    public void showShortToast(String message, boolean playTts, boolean immediately) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        playTtsIfNeed(message, playTts, immediately);
    }

    public void showShortToast(@StringRes int resId, boolean playTts, boolean immediately) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        playTtsIfNeed(getString(resId), playTts, immediately);
    }

    public void showShortToast(@StringRes int resId, boolean playTts, boolean immediately, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        Toast.makeText(this, getString(resId, args), Toast.LENGTH_SHORT).show();
        playTtsIfNeed(playTts, immediately, resId, args);
    }

    public void showLongToast(String message, boolean playTts, boolean immediately) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        playTtsIfNeed(message, playTts, immediately);
    }

    public void showLongToast(@StringRes int resId, boolean playTts, boolean immediately) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
        playTtsIfNeed(resId, playTts, immediately);
    }

    public void showLongToast(@StringRes int resId, boolean playTts, boolean immediately, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        Toast.makeText(this, getString(resId, args), Toast.LENGTH_LONG).show();
        playTtsIfNeed(playTts, immediately, resId, args);
    }

    private void playTtsIfNeed(String msg, boolean playTts, boolean immediately) {
        if (playTts) {
            TtsManager.getInstance().play(msg, immediately);
        }
    }

    private void playTtsIfNeed(@StringRes int resId, boolean playTts, boolean immediately) {
        if (playTts) {
            TtsManager.getInstance().play(resId, immediately);
        }
    }

    private void playTtsIfNeed(boolean playTts, boolean immediately,
                               @StringRes int resId, Object... formatArgs) {
        if (playTts) {
            TtsManager.getInstance().play(immediately, resId, formatArgs);
        }
    }
}
