package com.pine.template.base.manager.tts;

import android.text.TextUtils;

public class TtsEntity {
    private String utteranceId;
    private String msg;
    // 需要马上播放
    // 如果之前没有不允许被打断的tts或者该tts同时也不允许被打断，则清除之前的并立马播放该tts，否则添加到播放序列中
    private boolean immediately;
    // 不允许被打断
    // 播放的时候不允许后来的tts打断它，除非新来的tts需要立马播放且也不允许被打断（新来的具有更高优先级，直接播放新来的tts）
    private boolean notAllowInterrupt;
    // 用于tts播放在被设置全程静默的情况，该tts依然可以播放(一般用于非播不可的情况)
    private boolean ignoreDisable;

    private long addTime;

    public boolean isValid() {
        return !TextUtils.isEmpty(utteranceId) && !TextUtils.isEmpty(msg);
    }

    public TtsEntity() {

    }

    public TtsEntity(String utteranceId) {
        this.utteranceId = utteranceId;
    }

    public String getUtteranceId() {
        return utteranceId;
    }

    public void setUtteranceId(String utteranceId) {
        this.utteranceId = utteranceId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public boolean isIgnoreDisable() {
        return ignoreDisable;
    }

    public void setIgnoreDisable(boolean ignoreDisable) {
        this.ignoreDisable = ignoreDisable;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "TtsEntity{" +
                "utteranceId='" + utteranceId + '\'' +
                ", msg='" + msg + '\'' +
                ", immediately=" + immediately +
                ", notAllowInterrupt=" + notAllowInterrupt +
                ", ignoreDisable=" + ignoreDisable +
                ", addTime=" + addTime +
                '}';
    }
}
