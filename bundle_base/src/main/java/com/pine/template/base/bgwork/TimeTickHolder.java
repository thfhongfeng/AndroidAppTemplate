package com.pine.template.base.bgwork;

import android.os.Handler;

import androidx.annotation.NonNull;

public class TimeTickHolder {
    private Handler handler;
    private ITimeTickListener listener;
    private long lastTickTime;
    // 单位：秒
    private int tickSecondInterval;

    public TimeTickHolder(@NonNull Handler handler, int tickSecondInterval, @NonNull ITimeTickListener listener) {
        this.handler = handler;
        this.tickSecondInterval = tickSecondInterval;
        this.listener = listener;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ITimeTickListener getListener() {
        return listener;
    }

    public void setListener(ITimeTickListener listener) {
        this.listener = listener;
    }

    public long getLastTickTime() {
        return lastTickTime;
    }

    public void setLastTickTime(long lastTickTime) {
        this.lastTickTime = lastTickTime;
    }

    public int getTickSecondInterval() {
        return tickSecondInterval;
    }

    public void setTickSecondInterval(int tickSecondInterval) {
        this.tickSecondInterval = tickSecondInterval;
    }
}
