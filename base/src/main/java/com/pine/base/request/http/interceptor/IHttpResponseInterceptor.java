package com.pine.base.request.http.interceptor;

import com.pine.base.request.http.HttpRequestBean;
import com.pine.base.request.http.HttpResponse;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IHttpResponseInterceptor {
    boolean onIntercept(int what, HttpRequestBean requestBean, HttpResponse response);
}
