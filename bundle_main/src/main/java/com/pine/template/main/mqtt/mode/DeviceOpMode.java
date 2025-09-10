package com.pine.template.main.mqtt.mode;

public class DeviceOpMode<T> {
    public final static String SOURCE_WEB = "web";

    private boolean opSuccess;
    private String opSource;
    private String opTarget;
    private T data;

    public boolean isFromWeb() {
        return SOURCE_WEB.equals(opSource);
    }

    public boolean isOpSuccess() {
        return opSuccess;
    }

    public void setOpSuccess(boolean opSuccess) {
        this.opSuccess = opSuccess;
    }

    public String getOpSource() {
        return opSource;
    }

    public void setOpSource(String opSource) {
        this.opSource = opSource;
    }

    public String getOpTarget() {
        return opTarget;
    }

    public void setOpTarget(String opTarget) {
        this.opTarget = opTarget;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
