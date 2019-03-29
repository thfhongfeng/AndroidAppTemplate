package com.pine.base.request.database.interceptor;

import com.pine.base.request.database.DbRequestBean;

public interface IDbRequestInterceptor {
    boolean onIntercept(int what, DbRequestBean requestBean);
}
