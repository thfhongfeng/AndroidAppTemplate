package com.pine.base.architecture.mvvm.vm;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseViewModel extends ViewModel {
    private UiState mUiState = UiState.UI_STATE_UNDEFINE;
    private Activity mUi;

    public Activity getUi() {
        return mUi;
    }

    public void setUi(Activity ui) {
        mUi = ui;
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

    public UiState getUiState() {
        return mUiState;
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
     * 用于分析传入参数是否非法，在View init之前调用
     *
     * @return true表示非法， false表示合法
     */
    public boolean parseIntentData(@NonNull Bundle bundle) {
        return false;
    }

    /**
     * 用于分析传入参数是否非法，在View init之后调用
     *
     * @return
     */
    public void afterViewInit() {

    }

    protected void onCleared() {
        mUi = null;
    }

    // 重置UI
    MutableLiveData<Boolean> resetUiData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getResetUiData() {
        return resetUiData;
    }

    public void resetUi() {
        resetUiData.setValue(true);
    }

    // 结束UI
    MutableLiveData<Boolean> finishData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getFinishData() {
        return finishData;
    }

    public void finishUi() {
        finishData.setValue(true);
    }

    // 加载中ui显示状态
    MutableLiveData<Boolean> uiLoadingData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUiLoadingData() {
        return uiLoadingData;
    }

    public boolean isUiLoading() {
        return uiLoadingData.getValue();
    }

    public void setUiLoading(boolean isLoading) {
        uiLoadingData.setValue(isLoading);
    }

    // Toast ui显示
    MutableLiveData<String> toastMsgData = new MutableLiveData<>();

    public MutableLiveData<String> getToastMsgData() {
        return toastMsgData;
    }

    public void setToastMsgData(String msg) {
        toastMsgData.setValue(msg);
    }

    MutableLiveData<Integer> toastResIdData = new MutableLiveData<>();

    public MutableLiveData<Integer> getToastResIdData() {
        return toastResIdData;
    }

    public void setToastResId(@StringRes Integer id) {
        toastResIdData.setValue(id);
    }
}
