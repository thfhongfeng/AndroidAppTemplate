package com.pine.login.interceptor;

import com.pine.base.http.HttpRequestBean;
import com.pine.base.http.Interceptor.IHttpRequestInterceptor;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginRequestInterceptor implements IHttpRequestInterceptor {
    @Override
    public boolean onIntercept(int what, HttpRequestBean requestBean) {
        return false;
    }
}
