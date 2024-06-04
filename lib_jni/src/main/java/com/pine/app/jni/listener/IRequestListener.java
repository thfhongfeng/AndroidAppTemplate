package com.pine.app.jni.listener;

public interface IRequestListener {
    int ERR_CODE_DEFAULT = -1;
    int ERR_CODE_EXCEPTION = 100;
    int ERR_CODE_TIMEOUT = 9999;

    void onResponse(String action, String data);

    void onFail(String action, int errCode);
}
