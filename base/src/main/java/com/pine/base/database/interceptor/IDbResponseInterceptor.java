package com.pine.base.database.interceptor;

import com.pine.base.database.DbRequestBean;
import com.pine.base.database.DbResponse;

public interface IDbResponseInterceptor {
    boolean onIntercept(int what, DbRequestBean requestBean, DbResponse response);
}
