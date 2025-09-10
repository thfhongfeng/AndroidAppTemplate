package com.pine.app.lib.mqtt.framework;

public class ReplyData<T> {
    private boolean success;
    private int code;
    private String message;
    private BaseParams baseParams;

    private long replyTime;

    private T mode;

    private ReplyData() {
    }

    public static <T> ReplyData build(T mode) {
        ReplyData<T> entity = new ReplyData<>();
        entity.message = "";
        entity.mode = mode;
        return entity;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(long replyTime) {
        this.replyTime = replyTime;
    }

    @Override
    public String toString() {
        return "ReplyData{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", baseParams=" + baseParams +
                ", replyTime=" + replyTime +
                ", mode=" + mode +
                '}';
    }
}
