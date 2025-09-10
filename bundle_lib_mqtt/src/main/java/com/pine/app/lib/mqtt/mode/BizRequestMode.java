package com.pine.app.lib.mqtt.mode;

public class BizRequestMode<T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BizRequestMode{" +
                "data=" + data +
                '}';
    }
}
