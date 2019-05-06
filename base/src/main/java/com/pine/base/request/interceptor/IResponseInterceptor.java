package com.pine.base.request.interceptor;

import com.pine.base.request.RequestBean;
import com.pine.base.request.Response;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IResponseInterceptor {
    boolean onIntercept(int what, RequestBean requestBean, Response response);
}
