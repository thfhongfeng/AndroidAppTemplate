package com.pine.template.base.device_sdk;

public class DeviceSdkException extends Exception {
    public DeviceSdkException(Exception e) {
        super(e);
    }

    public DeviceSdkException(String message) {
        super(message);
    }
}
