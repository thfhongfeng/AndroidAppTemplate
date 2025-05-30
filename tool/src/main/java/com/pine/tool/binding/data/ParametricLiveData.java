package com.pine.tool.binding.data;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by tanghongfeng on 2019/3/1
 */

/**
 * 让MutableLiveData携带额外参数数据
 *
 * @param <T>
 * @param <P>
 */
public class ParametricLiveData<T, P> extends MutableLiveData<T> {
    private P customData;

    public P getCustomData() {
        return customData;
    }

    @Deprecated
    @Override
    public final void postValue(T value) {
        throw new IllegalStateException("this method was abandoned");
    }

    @Deprecated
    @Override
    public final void setValue(T value) {
        throw new IllegalStateException("this method was abandoned");
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
