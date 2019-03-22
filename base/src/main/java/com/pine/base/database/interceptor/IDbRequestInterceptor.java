package com.pine.base.database.interceptor;

import com.pine.base.database.DbRequestBean;

public interface IDbRequestInterceptor {
    boolean onIntercept(int what, DbRequestBean requestBean);
}
