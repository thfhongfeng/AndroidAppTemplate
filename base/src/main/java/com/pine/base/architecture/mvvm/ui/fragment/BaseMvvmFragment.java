package com.pine.base.architecture.mvvm.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.ui.BaseFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseMvvmFragment<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragment {
    protected T mBinding;
    protected VM mViewModel;

    @CallSuper
    @Override
    protected void beforeInitOnCreateView(@Nullable Bundle savedInstanceState) {
        // 创建ViewModel
        if (mViewModel == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Class presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                mViewModel = (VM) ViewModelProviders.of(getActivity()).get(presenterClazz);
                mViewModel.getUiLoadingData().setValue(false);
                mViewModel.setUi(getActivity());
            }
        }
    }

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getFragmentLayoutResId(), container, false);
        return mBinding.getRoot();
    }

    @Override
    protected final void findViewOnCreateView(View layout) {
        mViewModel.getResetUiData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean && getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }
            }
        });
        mViewModel.getFinishData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean && getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().finish();
                }
            }
        });
        mViewModel.getUiLoadingData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                setLoadingUiVisibility(aBoolean);
            }
        });
        mViewModel.getToastStrData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String msg) {
                showShortToast(msg);
            }
        });
        mViewModel.getToastResIdData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer resId) {
                showShortToast(resId);
            }
        });
    }

    @CallSuper
    @Override
    protected boolean parseArguments() {
        if (mViewModel != null) {
            return mViewModel.parseIntentData(getArguments() == null ? new Bundle() : getArguments());
        }
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
        if (mViewModel != null) {
            mViewModel.afterViewInit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_START);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_RESUME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_PAUSE);
        }
    }

    @Override
    public void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mViewModel != null) {
            mViewModel.onUiState(BaseViewModel.UiState.UI_STATE_ON_DETACH);
        }
        super.onDestroyView();
    }

    public void setLoadingUiVisibility(boolean visibility) {

    }
}
