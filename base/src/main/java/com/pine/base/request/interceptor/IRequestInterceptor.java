package com.pine.base.request.interceptor;

import com.pine.base.request.RequestBean;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IRequestInterceptor {
    boolean onIntercept(int what, RequestBean requestBean);
}
