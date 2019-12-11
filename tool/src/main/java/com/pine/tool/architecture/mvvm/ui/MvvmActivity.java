package com.pine.tool.architecture.mvvm.ui;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.architecture.state.UiState;
import com.pine.tool.ui.Activity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class MvvmActivity<T extends ViewDataBinding, VM extends ViewModel> extends Activity {
    protected T mBinding;
    protected VM mViewModel;

    private Observer<Integer> mSyncLiveDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer tag) {
            observeSyncLiveData(tag);
        }
    };

    private Observer<Boolean> mResetUiDataObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@Nullable Boolean aBoolean) {
            if (aBoolean) {
                finish();
                startActivity(getIntent());
            }
        }
    };

    private Observer<Boolean> mFinishDataObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@Nullable Boolean aBoolean) {
            if (aBoolean) {
                finish();
            }
        }
    };

    private Observer<Boolean> mUiLoadingDataObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@Nullable Boolean aBoolean) {
            setLoadingUiVisibility(aBoolean);
        }
    };

    private Observer<String> mToastMsgDataObserver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String msg) {
            showShortToast(msg);
        }
    };

    private Observer<Integer> mToastResIdDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer resId) {
            showShortToast(resId);
        }
    };

    private Observer<Integer> mToastResDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer resId) {
            showShortToast(resId, mViewModel.getToastResData().getCustomData());
        }
    };

    @CallSuper
    @Override
    protected void beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        // 创建ViewModel{
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            mViewModel = (VM) ViewModelProviders.of(this).get(presenterClazz);
            mViewModel.getUiLoadingData().setValue(false);
        }
        mViewModel.getObserveSyncLiveDataData().observe(this, mSyncLiveDataObserver);
        mViewModel.getResetUiData().observe(this, mResetUiDataObserver);
        mViewModel.getFinishData().observe(this, mFinishDataObserver);
        mViewModel.getUiLoadingData().observe(this, mUiLoadingDataObserver);
        mViewModel.getToastMsgData().observe(this, mToastMsgDataObserver);
        mViewModel.getToastResIdData().observe(this, mToastResIdDataObserver);
        mViewModel.getToastResData().observe(this, mToastResDataObserver);
        observeInitLiveData(savedInstanceState);
    }

    /**
     * 用于在VM中初始化赋值的LiveData的进行监听观察
     * 此方法在Activity onCreate的时候自动调用
     * （注意区别于observeSyncLiveData）
     * observeInitLiveData：用于在VM中初始化的LiveData的进行监听观察。
     * observeSyncLiveData ：用于对不是在VM中初始化赋值的LiveData的进行监听观察，需要在VM中主动调用setSyncLiveDataTag。
     */
    public abstract void observeInitLiveData(Bundle savedInstanceState);

    protected void setContentView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, getActivityLayoutResId());
    }

    @CallSuper
    @Override
    protected final void findViewOnCreate(Bundle savedInstanceState) {
        mViewModel.setContext(this);
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
            mViewModel.onUiState(UiState.UI_STATE_ON_INIT);
        }
        if (mViewModel != null) {
            mViewModel.afterViewInit();
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_RESUME);
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_PAUSE);
        }
    }

    @CallSuper
    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_DETACH);
        }
    }

    public void setLoadingUiVisibility(boolean visibility) {

    }

    /**
     * 对不是在VM中初始化赋值的LiveData的进行监听观察（通过其它功能返回的LiveData）。
     * 此方法的调用需要在VM获取到LiveData后中主动调用setSyncLiveDataTag方法。
     * （注意区别于observeInitLiveData）
     * observeInitLiveData：用于在VM中初始化的LiveData的进行监听观察。
     * observeSyncLiveData ：用于对不是在VM中初始化赋值的LiveData的进行监听观察，需要在VM中主动调用setSyncLiveDataTag。
     *
     * @param liveDataObjTag 用来标识对应的LiveData(由调用者自己标识)
     */
    public abstract void observeSyncLiveData(int liveDataObjTag);
}
