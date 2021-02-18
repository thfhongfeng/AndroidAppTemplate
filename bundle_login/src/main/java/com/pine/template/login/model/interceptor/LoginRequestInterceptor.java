package com.pine.template.login.model.interceptor;

import com.pine.tool.request.RequestBean;
import com.pine.tool.request.interceptor.IRequestInterceptor;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginRequestInterceptor implements IRequestInterceptor {
    @Override
    public boolean onIntercept(int what, RequestBean requestBean) {
        return false;
    }
}
