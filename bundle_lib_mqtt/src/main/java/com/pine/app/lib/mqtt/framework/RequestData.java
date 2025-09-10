package com.pine.app.lib.mqtt.framework;

public class RequestData<T> {
    private BaseParams baseParams;

    private long requestTime;

    private T mode;

    private RequestData() {
    }

    public static <T> RequestData build(T mode, BaseParams baseParams) {
        RequestData<T> entity = new RequestData<>();
        entity.baseParams = baseParams;
        entity.mode = mode;
        return entity;
    }

    public static RequestData build(BaseParams baseParams) {
        RequestData entity = new RequestData<>();
        entity.baseParams = baseParams;
        return entity;
    }

    public BaseParams getBaseParams() {
        return baseParams;
    }

    public void setBaseParams(BaseParams baseParams) {
        this.baseParams = baseParams;
    }

    public T getMode() {
        return mode;
    }

    public void setMode(T mode) {
        this.mode = mode;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "baseParams=" + baseParams +
                ", requestTime=" + requestTime +
                ", mode=" + mode +
                '}';
    }
}
