package com.pine.login.model.interceptor;

import com.pine.base.request.RequestBean;
import com.pine.base.request.interceptor.IRequestInterceptor;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginRequestInterceptor implements IRequestInterceptor {
    @Override
    public boolean onIntercept(int what, RequestBean requestBean) {
        return false;
    }
}
