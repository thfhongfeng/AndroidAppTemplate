package com.pine.tool.architecture.mvvm.ui;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
            int clickGone = mViewModel.getUiLoadingData().getCustomData();
            if (clickGone == -1) {
                setLoadingUiVisibility(aBoolean);
            } else {
                setLoadingUiVisibility(aBoolean, clickGone != 0);
            }
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

    private Observer<Integer> mToastResFormatDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer resId) {
            showShortToast(resId, mViewModel.getToastResFormatData().getCustomData());
        }
    };

    private Observer<String> mLongToastMsgDataObserver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String msg) {
            showLongToast(msg);
        }
    };

    private Observer<Integer> mLongToastResIdDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer resId) {
            showLongToast(resId);
        }
    };

    private Observer<Integer> mLongToastResFormatDataObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer resId) {
            showLongToast(resId, mViewModel.getToastResFormatData().getCustomData());
        }
    };

    @CallSuper
    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        // 创建ViewModel{
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            mViewModel = createViewModel((ParameterizedType) type);
        }
        mViewModel.getObserveSyncLiveDataData().observe(this, mSyncLiveDataObserver);
        mViewModel.getResetUiData().observe(this, mResetUiDataObserver);
        mViewModel.getFinishData().observe(this, mFinishDataObserver);
        mViewModel.getUiLoadingData().observe(this, mUiLoadingDataObserver);
        mViewModel.getToastMsgData().observe(this, mToastMsgDataObserver);
        mViewModel.getToastResIdData().observe(this, mToastResIdDataObserver);
        mViewModel.getToastResFormatData().observe(this, mToastResFormatDataObserver);
        mViewModel.getLongToastMsgData().observe(this, mLongToastMsgDataObserver);
        mViewModel.getLongToastResIdData().observe(this, mLongToastResIdDataObserver);
        mViewModel.getLongToastResFormatData().observe(this, mLongToastResFormatDataObserver);
        observeInitLiveData(savedInstanceState);
        return false;
    }

    private volatile boolean mEnableStoreViewModel = true;

    public void enableStoreViewModel(boolean enable) {
        mEnableStoreViewModel = enable;
    }

    public VM createViewModel(ParameterizedType type) {
        Class presenterClazz = (Class) type.getActualTypeArguments()[1];
        if (mEnableStoreViewModel) {
            VM vm = (VM) ViewModelProviders.of(this).get(presenterClazz);
            return vm;
        } else {
            ViewModelProvider.Factory factory = new ViewModelProvider.NewInstanceFactory();
            VM vm = (VM) new ViewModelProvider(this, factory).get(presenterClazz);
            return vm;
        }
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

    }

    @CallSuper
    @Override
    protected final boolean parseIntentData() {
        if (mViewModel != null) {
            return mViewModel.parseIntentData(this, getIntent().getExtras() == null ?
                    new Bundle() : getIntent().getExtras());
        }
        return false;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        if (mViewModel != null) {
            mViewModel.onUiState(this, UiState.UI_STATE_ON_INIT);
        }
        if (mViewModel != null) {
            mViewModel.afterViewInit(this);
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(this, UiState.UI_STATE_ON_RESUME);
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(this, UiState.UI_STATE_ON_PAUSE);
        }
    }

    @CallSuper
    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(this, UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (mViewModel != null) {
            mViewModel.onUiState(this, UiState.UI_STATE_ON_DETACH);
        }
    }

    public void setLoadingUiVisibility(boolean visibility) {

    }

    public void setLoadingUiVisibility(boolean visibility, boolean enableClickGone) {

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
