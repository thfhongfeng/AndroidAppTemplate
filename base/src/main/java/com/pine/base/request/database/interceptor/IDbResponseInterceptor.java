package com.pine.base.request.database.interceptor;

import com.pine.base.request.database.DbRequestBean;
import com.pine.base.request.database.DbResponse;

public interface IDbResponseInterceptor {
    boolean onIntercept(int what, DbRequestBean requestBean, DbResponse response);
}
