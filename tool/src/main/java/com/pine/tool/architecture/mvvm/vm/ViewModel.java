package com.pine.tool.architecture.mvvm.vm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.MutableLiveData;

import com.pine.tool.architecture.state.UiState;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class ViewModel extends androidx.lifecycle.ViewModel {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());
    private UiState mUiState = UiState.UI_STATE_UNDEFINE;

    /**
     * UI状态回调
     *
     * @param state UI_STATE_ON_INIT,UI_STATE_ON_RESUME,UI_STATE_ON_PAUSE,
     *              UI_STATE_ON_STOP,UI_STATE_ON_DETACH
     */
    @CallSuper
    public void onUiState(Context activity, UiState state) {
        mUiState = state;
    }

    public UiState getUiState() {
        return mUiState;
    }

    /**
     * 用于分析传入参数是否非法，在View init之前调用
     *
     * @return true表示非法， false表示合法
     */
    public boolean parseIntentData(Context activity, @NonNull Bundle bundle) {
        return false;
    }

    /**
     * 在View init之后调用
     *
     * @return
     */
    public void afterViewInit(Context activity) {

    }

    public void onCleared() {

    }

    MutableLiveData<Integer> observeSyncLiveData = new MutableLiveData<>();

    public MutableLiveData<Integer> getObserveSyncLiveDataData() {
        return observeSyncLiveData;
    }

    /**
     * 用于LiveData是其它功能操作返回（不是在VM中初始化赋值）的情况，
     * 在LiveData返回时通过调用setSyncLiveDataTag来告诉UI开始绑定Observer，
     * UI中的必须实现observeSyncLiveData，同时所有其它功能操作返回的LiveData只能在此方法中进行绑定Observer
     *
     * @param liveDataObjTag 用来标识对应的LiveData(由调用者自己确定)
     */
    public void setSyncLiveDataTag(int liveDataObjTag) {
        observeSyncLiveData.setValue(liveDataObjTag);
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
    ParametricLiveData<Boolean, Integer> uiLoadingData = new ParametricLiveData<>();

    public ParametricLiveData<Boolean, Integer> getUiLoadingData() {
        return uiLoadingData;
    }

    public boolean isUiLoading() {
        return uiLoadingData.getValue() == null ? false : uiLoadingData.getValue();
    }

    public void setUiLoading(boolean isLoading) {
        uiLoadingData.setValue(isLoading, -1);
    }

    public void setUiLoading(boolean isLoading, boolean enableClickGone) {
        uiLoadingData.setValue(isLoading, enableClickGone ? 1 : 0);
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

    ParametricLiveData<Integer, Integer[]> toastResFormatData = new ParametricLiveData<>();

    public ParametricLiveData<Integer, Integer[]> getToastResFormatData() {
        return toastResFormatData;
    }

    public void setToastResFormat(@StringRes Integer id, Integer... formatArgs) {
        toastResFormatData.setValue(id, formatArgs);
    }

    MutableLiveData<String> longToastMsgData = new MutableLiveData<>();

    public MutableLiveData<String> getLongToastMsgData() {
        return longToastMsgData;
    }

    public void setLongToastMsg(String msg) {
        longToastMsgData.setValue(msg);
    }

    MutableLiveData<Integer> longToastResIdData = new MutableLiveData<>();

    public MutableLiveData<Integer> getLongToastResIdData() {
        return longToastResIdData;
    }

    public void setLongToastResId(@StringRes Integer id) {
        longToastResIdData.setValue(id);
    }

    ParametricLiveData<Integer, Integer[]> longToastResFormatData = new ParametricLiveData<>();

    public ParametricLiveData<Integer, Integer[]> getLongToastResFormatData() {
        return longToastResFormatData;
    }

    public void setLongToastResFormat(@StringRes Integer id, Integer... formatArgs) {
        longToastResFormatData.setValue(id, formatArgs);
    }

    public final String getString(@StringRes int resId) {
        return AppUtils.getApplication().getString(resId);
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return AppUtils.getApplication().getString(resId, formatArgs);
    }
}
