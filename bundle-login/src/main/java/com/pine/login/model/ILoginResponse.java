package com.pine.login.model;

public interface ILoginResponse {
    boolean onLoginResponse(boolean isSuccess, String msg);

    void onCancel();
}
