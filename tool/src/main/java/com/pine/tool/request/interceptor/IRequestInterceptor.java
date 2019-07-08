package com.pine.tool.request.interceptor;

import com.pine.tool.request.RequestBean;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IRequestInterceptor {
    boolean onIntercept(int what, RequestBean requestBean);
}
