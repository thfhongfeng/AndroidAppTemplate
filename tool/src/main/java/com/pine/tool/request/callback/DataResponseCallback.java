package com.pine.tool.request.callback;

import com.pine.tool.request.Response;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class DataResponseCallback extends AbstractBaseCallback {
    public abstract void onResponse(int what, Response response);

    public abstract boolean onFail(int what, Exception e, Response response);

    public abstract void onCancel(int what);
}
