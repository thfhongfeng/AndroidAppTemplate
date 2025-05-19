package com.pine.tool.binding.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 解决类似Fragment重新加载，observe在重新注册时会触发LiveData的「粘性事件」特性，从而导致onChanged会在注册后被触发的问题
 * 即去掉LiveData的「粘性事件」特性，以用于某些特殊情况
 */
public class SingleMutableLiveEvent<T> extends MutableLiveData<T> {
    private final AtomicBoolean pending = new AtomicBoolean(false);

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, o -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(o);
            }
        });
    }

    @Override
    public void setValue(T value) {
        pending.set(true);
        super.setValue(value);
    }
}