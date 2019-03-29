package com.pine.base.request.http.interceptor;

import com.pine.base.request.http.HttpRequestBean;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IHttpRequestInterceptor {
    boolean onIntercept(int what, HttpRequestBean requestBean);
}
