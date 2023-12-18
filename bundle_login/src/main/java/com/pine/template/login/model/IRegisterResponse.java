package com.pine.template.login.model;

import com.pine.template.base.business.bean.AccountBean;

public interface IRegisterResponse {
    boolean onRegisterResponse(boolean isSuccess, AccountBean accountBean);

    void onCancel();
}
