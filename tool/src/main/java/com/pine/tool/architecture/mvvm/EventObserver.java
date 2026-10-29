package com.pine.tool.architecture.mvvm;

import androidx.lifecycle.Observer;

public abstract class EventObserver<T> implements Observer<EventData<T>> {
    public abstract void accept(T t);

    @Override
    public void onChanged(EventData<T> event) {
        if (event == null) return;
        T data = event.getLiveDataIfNotHandled();
        if (data != null) {
            accept(data);
        }
    }
}
