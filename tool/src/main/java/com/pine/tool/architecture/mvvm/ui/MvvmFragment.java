package com.pine.tool.architecture.mvvm.ui;

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

import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.architecture.state.UiState;
import com.pine.tool.ui.Fragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class MvvmFragment<T extends ViewDataBinding, VM extends ViewModel> extends Fragment {
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
        mViewModel.getSyncLiveDataInitData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer tag) {
                onSyncLiveDataInit(tag);
            }
        });
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
        mViewModel.getToastMsgData().observe(this, new Observer<String>() {
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

    @CallSuper
    @Override
    protected void afterInit() {
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_INIT);
        }
        if (mViewModel != null) {
            mViewModel.afterViewInit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_RESUME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_PAUSE);
        }
    }

    @Override
    public void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_DETACH);
        }
        super.onDestroyView();
    }

    public void setLoadingUiVisibility(boolean visibility) {

    }

    /**
     * 此方法的调用需要在VM中主动调用callOnSyncLiveDataInit方法，否则不会执行
     *
     * @param liveDataObjTag 用来标识对应的异步LiveData(由调用者自己标识)
     */
    public abstract void onSyncLiveDataInit(int liveDataObjTag);
}
