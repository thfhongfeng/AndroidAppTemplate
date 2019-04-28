package com.pine.login.model;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.login.bean.AccountBean;

import java.util.HashMap;

public interface ILoginAccountModel {
    boolean requestLogin(HashMap<String, String> params, int requestCode, ILoginResponse callback);

    void requestLogout();

    void requestRegister(final HashMap<String, String> params,
                         @NonNull final IModelAsyncResponse<AccountBean> callback);
}
