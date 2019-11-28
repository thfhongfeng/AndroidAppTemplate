package com.pine.tool.architecture.mvvm.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        }
    };

    private Observer<Boolean> mFinishDataObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@Nullable Boolean aBoolean) {
            if (aBoolean) {
                getActivity().finish();
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

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建ViewModel
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            mViewModel = (VM) ViewModelProviders.of(getActivity()).get(presenterClazz);
            mViewModel.getUiLoadingData().setValue(false);
        }
        mViewModel.getObserveSyncLiveDataData().observe(this, mSyncLiveDataObserver);
        mViewModel.getResetUiData().observe(this, mResetUiDataObserver);
        mViewModel.getFinishData().observe(this, mFinishDataObserver);
        mViewModel.getUiLoadingData().observe(this, mUiLoadingDataObserver);
        mViewModel.getToastMsgData().observe(this, mToastMsgDataObserver);
        mViewModel.getToastResIdData().observe(this, mToastResIdDataObserver);
        observeInitLiveData();
    }

    /**
     * 用于在VM中初始化赋值的LiveData的进行监听观察
     * 此方法在Fragment onCreate的时候自动调用
     * （注意区别于observeSyncLiveData）
     * observeInitLiveData：用于在VM中初始化的LiveData的进行监听观察。
     * observeSyncLiveData ：用于对不是在VM中初始化赋值的LiveData的进行监听观察，需要在VM中主动调用setSyncLiveDataTag。
     */
    public abstract void observeInitLiveData();

    @CallSuper
    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getFragmentLayoutResId(), container, false);
        return mBinding.getRoot();
    }

    @CallSuper
    @Override
    protected final void findViewOnCreateView(View layout) {
        mViewModel.setContext(getActivity());
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

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_RESUME);
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_PAUSE);
        }
    }

    @CallSuper
    @Override
    public void onStop() {
        if (mViewModel != null) {
            mViewModel.onUiState(UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @CallSuper
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
