package com.pine.app.jni.listener;

public interface IRequestListener {
    String KEY_CMD_TYPE = "cmdType";
    String KEY_DATA = "data";
    String KEY_SUCCESS = "success";
    String KEY_CODE = "code";
    String KEY_MSG = "msg";

    int ERR_CODE_DEFAULT = -1;
    int ERR_CODE_EXCEPTION = 100;
    int ERR_CODE_DATA_FORMAT_EXCEPTION = 101;
    int ERR_CODE_TIMEOUT = 9999;

    int ERR_CODE_LOGIN_ALREADY_LOGIN_NOT_SAME = -1004;
    int ERR_CODE_LOGIN_ALREADY = -1003;
    int ERR_CODE_LOGIN_PWD_ERR = -1002;
    int ERR_CODE_LOGIN_ACCOUNT_ERR = -1001;

    void onJniResponse(String action, String msg);

    void onJniFail(String action, int errCode);
}
