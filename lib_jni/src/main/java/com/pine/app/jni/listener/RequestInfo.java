package com.pine.app.jni.listener;

import androidx.annotation.NonNull;

public class RequestInfo {
    private String action;
    private String callTag;
    private IRequestListener listener;
    private long callTime;

    public RequestInfo(@NonNull String action, @NonNull String callTag) {
        this.action = action;
        this.callTag = callTag;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCallTag() {
        return callTag;
    }

    public void setCallTag(String callTag) {
        this.callTag = callTag;
    }

    public IRequestListener getListener() {
        return listener;
    }

    public void setListener(IRequestListener listener) {
        this.listener = listener;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }
}
