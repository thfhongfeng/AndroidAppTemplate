package com.pine.base.architecture.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;

import com.pine.base.architecture.mvvm.data.BaseMvvmLiveData;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseViewModel extends ViewModel {
    private UiState mUiState = UiState.UI_STATE_UNDEFINE;

    MutableLiveData<Boolean> finishData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getFinishData() {
        return finishData;
    }

    public void setFinishData(boolean finish) {
        finishData.setValue(finish);
    }

    MutableLiveData<Boolean> uiLoadingData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUiLoadingData() {
        return uiLoadingData;
    }

    public void setUiLoadingData(boolean isLoading) {
        uiLoadingData.setValue(isLoading);
    }

    private int toastCount = 0;

    BaseMvvmLiveData<Integer, String> toastStrData = new BaseMvvmLiveData<>();

    public BaseMvvmLiveData<Integer, String> getToastStrData() {
        return toastStrData;
    }

    public void setToastData(String msg) {
        toastStrData.setValue(++toastCount, msg);
    }

    BaseMvvmLiveData<Integer, Integer> toastResIdData = new BaseMvvmLiveData<>();

    public BaseMvvmLiveData<Integer, Integer> getToastResIdData() {
        return toastResIdData;
    }

    public void setToastData(@IdRes Integer id) {
        toastResIdData.setValue(++toastCount, id);
    }

    /**
     * UI状态回调
     *
     * @param state UI_STATE_ON_CREATE,UI_STATE_ON_START,UI_STATE_ON_RESUME,UI_STATE_ON_PAUSE,
     *              UI_STATE_ON_STOP,UI_STATE_ON_DETACH
     */
    @CallSuper
    public void onUiState(UiState state) {
        mUiState = state;
    }

    public enum UiState {
        UI_STATE_UNDEFINE,
        UI_STATE_ON_CREATE,
        UI_STATE_ON_START,
        UI_STATE_ON_RESUME,
        UI_STATE_ON_PAUSE,
        UI_STATE_ON_STOP,
        UI_STATE_ON_DETACH
    }

    /**
     * 用于分析传入参数是否非法
     *
     * @return true表示非法， false表示合法
     */
    public abstract boolean parseInitData(Bundle bundle);
}
