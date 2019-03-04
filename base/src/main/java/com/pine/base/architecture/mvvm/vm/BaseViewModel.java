package com.pine.base.architecture.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseViewModel extends ViewModel {
    MutableLiveData<Boolean> uiLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUiLoading() {
        return uiLoading;
    }

    public void setUiLoading(boolean isLoading) {
        uiLoading.setValue(isLoading);
    }

    /**
     * UI状态回调
     *
     * @param state UI_STATE_ON_CREATE,UI_STATE_ON_START,UI_STATE_ON_RESUME,UI_STATE_ON_PAUSE,
     *              UI_STATE_ON_STOP,UI_STATE_ON_DETACH
     */
    public void onUiState(UiState state) {

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
}
