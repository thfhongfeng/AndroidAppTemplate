package com.pine.tool.architecture.mvvm;

public class EventData<T> {
    private final T liveData;
    private boolean hasBeenHandled = false;

    public EventData(T liveData) {
        this.liveData = liveData;
    }

    /**
     * 获取内容，只会返回一次；已经消费过返回 null
     */
    public T getLiveDataIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return liveData;
    }

    /**
     * 只是拿内容，不修改消费标记（用于日志/调试）
     */
    public T peekContent() {
        return liveData;
    }
}
