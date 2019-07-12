package com.pine.tool.architecture.mvvm.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.ui.Activity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class MvvmActivity<T extends ViewDataBinding, VM extends ViewModel> extends Activity {
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
                mViewModel.getUiLoadingData().setValue(false);
                mViewModel.setUi(this);
            }
        }
    }

    protected void setContentView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, getActivityLayoutResId());
    }

    @Override
    protected final void findViewOnCreate() {
        mViewModel.getSyncLiveDataInitData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer tag) {
                onSyncLiveDataInit(tag);
            }
        });
        mViewModel.getResetUiData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        mViewModel.getFinishData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    finish();
                }
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
    protected final boolean parseIntentData() {
        if (mViewModel != null) {
            return mViewModel.parseIntentData(getIntent().getExtras() == null ?
                    new Bundle() : getIntent().getExtras());
        }
        return false;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_CREATE);
        }
        if (mViewModel != null) {
            mViewModel.afterViewInit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_RESUME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_PAUSE);
        }
    }

    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (mViewModel != null) {
            mViewModel.onUiState(ViewModel.UiState.UI_STATE_ON_DETACH);
        }
    }

    /**
     * 此方法的调用需要在VM中主动调用callOnSyncLiveDataInit方法，否则不会执行
     *
     * @param liveDataObjTag 用来标识对应的异步LiveData(由调用者自己标识)
     */
    public abstract void onSyncLiveDataInit(int liveDataObjTag);
}
