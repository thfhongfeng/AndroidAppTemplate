package com.pine.base.architecture.mvvm.data;

import android.arch.lifecycle.MutableLiveData;

public class BaseMvvmLiveData<T, O> extends MutableLiveData<T> {
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