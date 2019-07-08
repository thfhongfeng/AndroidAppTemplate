package com.pine.tool.request.interceptor;

import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface IResponseInterceptor {
    boolean onIntercept(int what, RequestBean requestBean, Response response);
}
