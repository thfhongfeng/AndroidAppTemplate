package com.pine.tool.ui;

import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastEntity {
    private String content;

    private Integer resId;

    private Integer[] resFormId;

    private long duration = 3 * 1000;

    // 需要马上播放
    // 如果之前没有不允许被打断的tts或者该tts同时也不允许被打断，则清除之前的并立马播放该tts，否则添加到播放序列中
    private boolean immediately = true;
    // 不允许被打断
    // 播放的时候不允许后来的tts打断它，除非新来的tts需要立马播放且也不允许被打断（新来的具有更高优先级，直接播放新来的tts）
    private boolean notAllowInterrupt;

    private Toast toast;
    private long timeStamp;

    public ToastEntity() {

    }

    public ToastEntity(@NonNull Toast toast, long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Integer[] getResFormId() {
        return resFormId;
    }

    public void setResFormId(Integer[] resFormId) {
        this.resFormId = resFormId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isImmediately() {
        return immediately;
    }

    public void setImmediately(boolean immediately) {
        this.immediately = immediately;
    }

    public boolean isNotAllowInterrupt() {
        return notAllowInterrupt;
    }

    public void setNotAllowInterrupt(boolean notAllowInterrupt) {
        this.notAllowInterrupt = notAllowInterrupt;
    }

    public Toast getToast() {
        return toast;
    }

    public void setToast(Toast toast) {
        this.toast = toast;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
