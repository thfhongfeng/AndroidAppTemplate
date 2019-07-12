package com.pine.tool.architecture.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.SupportActivity;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class ViewModel extends android.arch.lifecycle.ViewModel {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());
    private UiState mUiState = UiState.UI_STATE_UNDEFINE;
    private SupportActivity mUi;

    public SupportActivity getUi() {
        return mUi;
    }

    public void setUi(SupportActivity ui) {
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

    public enum DialogState {
        DIALOG_STATE_SHOW,
        DIALOG_STATE_HIDE,
        DIALOG_STATE_DISMISS
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

    // 用于LiveData是异步操作返回（不是在VM中初始化）的情况，
    // 在异步LiveData返回时调用callOnSyncLiveDataInit来告诉UI开始绑定Observer，
    // 参数liveDataObjTag用来标识对应的异步LiveData(由调用者自己标识)
    // UI中的必须实现onSyncLiveDataInit，同时所有的异步返回的LiveData只能在此方法中进行绑定
    MutableLiveData<Integer> syncLiveDataInitData = new MutableLiveData<>();

    public MutableLiveData<Integer> getSyncLiveDataInitData() {
        return syncLiveDataInitData;
    }

    public void callOnSyncLiveDataInit(int liveDataObjTag) {
        syncLiveDataInitData.setValue(liveDataObjTag);
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

    public void setToastMsg(String msg) {
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
