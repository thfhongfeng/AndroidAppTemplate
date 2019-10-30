package com.pine.tool.binding.data;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class ParametricLiveData<T, P> extends MutableLiveData<T> {
    private P customData;

    public P getCustomData() {
        return customData;
    }

    @Override
    public final void postValue(T value) {
    }

    @Override
    public final void setValue(T value) {

    }

    public void setValue(T value, P customData) {
        this.customData = customData;
        super.setValue(value);
    }

    public void postValue(T value, P customData) {
        this.customData = customData;
        super.postValue(value);
    }
}
