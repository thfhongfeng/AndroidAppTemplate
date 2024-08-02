package com.pine.template.base.manager.tts;

import android.text.TextUtils;

public class TtsEntity {
    private String utteranceId;
    private String msg;
    private boolean immediately;
    private boolean notAllowInterrupt;
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
                ", addTime=" + addTime +
                '}';
    }
}
