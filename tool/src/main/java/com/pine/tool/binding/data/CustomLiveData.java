package com.pine.tool.binding.data;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class CustomLiveData<T, O> extends MutableLiveData<T> {
    private O customData;

    public O getCustomData() {
        return customData;
    }

    @Override
    public final void postValue(T value) {
    }

    @Override
    public final void setValue(T value) {

    }

    public void setValue(T value, O customData) {
        this.customData = customData;
        super.setValue(value);
    }

    public void postValue(T value, O customData) {
        this.customData = customData;
        super.postValue(value);
    }
}
