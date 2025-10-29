package com.pine.template.main.ui;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;

import com.pine.tool.architecture.mvvm.ui.MvvmFragment;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class UniversalVisibleFragment<T extends ViewDataBinding, VM extends ViewModel>
        extends MvvmFragment<T, VM> {
    private static final String STATE_IS_FIRST_TIME = "is_first_time";
    protected boolean _isFirstTime = true;

    protected boolean _isLastVisible = false;

    // 生命周期校验（add/replace 场景）
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "checkAndTrigger onResume");
        checkAndTrigger(true);
    }

    // 隐藏状态校验（show/hide 场景）
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "checkAndTrigger onHiddenChanged");
        checkAndTrigger(false);
    }

    // 视图可见性校验（极端场景兜底）
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getView() != null && getView().getGlobalVisibleRect(new Rect())) {
                        LogUtils.d(TAG, "checkAndTrigger OnGlobalLayoutListener");
                        checkAndTrigger(false);
                        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    // 核心触发逻辑
    private void checkAndTrigger(boolean resume) {
        boolean isReallyVisible = isReallyVisible(resume);
        LogUtils.d(TAG, "checkAndTrigger resume: " + resume + ", isReallyVisible:" + isReallyVisible);
        if (isReallyVisible) {
            if (!_isLastVisible) {
                onFragmentVisible(_isFirstTime);
                _isFirstTime = false; // 关键：立即标记为非首次
            }
        } else {
            if (_isLastVisible) {
                onFragmentHide();
            }
        }
    }

    // 最终可见性校验
    protected boolean isReallyVisible(boolean resume) {
        boolean isAdded = isAdded();
        boolean isDetached = isDetached();
        boolean isHidden = isHidden();
        boolean getUserVisibleHint = getUserVisibleHint();
        boolean isAtLeastRESUMED = getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
        LogUtils.i(TAG, "isReallyVisible isAdded:" + isAdded + ", isDetached:" + isDetached
                + ", isHidden:" + isHidden + ", getUserVisibleHint:" + getUserVisibleHint + ", resume:" + resume
                + ", isAtLeastRESUMED:" + isAtLeastRESUMED + ", getLifecycle().getCurrentState():" + getLifecycle().getCurrentState());
        return isAdded && !isDetached && !isHidden && getUserVisibleHint && (resume || isAtLeastRESUMED);
    }

    // 触发事件（带首次标记）
    public void onFragmentVisible(boolean first) {
        _isLastVisible = true;
    }

    public void onFragmentHide() {
        _isLastVisible = false;
    }

    // 状态保存（可选：配置变更时保留首次标记）
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_FIRST_TIME, _isFirstTime);
    }

    // 状态恢复（可选）
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            _isFirstTime = savedInstanceState.getBoolean(STATE_IS_FIRST_TIME, true);
        }
    }

    // 配置变更时重置首次标记（可选）
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _isFirstTime = true; // 屏幕旋转等配置变更时视为新实例
    }
}