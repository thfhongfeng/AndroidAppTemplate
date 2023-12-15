package com.pine.tool.ui;

import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastEntity {
    @NonNull
    private Toast toast;
    private long timeStamp;

    public ToastEntity() {

    }

    public ToastEntity(@NonNull Toast toast, long timeStamp) {
        this.toast = toast;
        this.timeStamp = timeStamp;
    }

    @NonNull
    public Toast getToast() {
        return toast;
    }

    public void setToast(@NonNull Toast toast) {
        this.toast = toast;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
