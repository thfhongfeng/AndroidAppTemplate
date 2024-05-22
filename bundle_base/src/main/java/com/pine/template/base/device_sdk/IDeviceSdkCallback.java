package com.pine.template.base.device_sdk;

public interface IDeviceSdkCallback {
    void onError(int errCode, String errMsg);

    void onSuccess(String resJson);
}
