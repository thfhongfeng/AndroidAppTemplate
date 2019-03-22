package com.pine.login.model.local.interceptor;

import com.pine.base.database.DbRequestBean;
import com.pine.base.database.interceptor.IDbRequestInterceptor;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginLocalRequestInterceptor implements IDbRequestInterceptor {
    @Override
    public boolean onIntercept(int what, DbRequestBean requestBean) {
        return false;
    }
}
