package com.pine.base.architecture.mvvm.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.ui.BaseActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseMvvmActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity {
    protected T mBinding;
    protected VM mViewModel;

    @CallSuper
    @Override
    protected void beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        // 创建ViewModel
        if (mViewModel == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Class presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                mViewModel = (VM) ViewModelProviders.of(this).get(presenterClazz);
                mViewModel.getUiLoading().setValue(false);
            }
        }
    }

    protected void setContentView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, getActivityLayoutResId());
    }

    @Override
    protected final void findViewOnCreate() {

    }

    @Override
    protected boolean parseIntentData() {
        return false;
    }

    @Override
    protected void init() {

    }

    @CallSuper
    @Override
    protected void afterInit() {
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_CREATE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_RESUME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_PAUSE);
        }
    }

    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_DETACH);
        }
    }
}
