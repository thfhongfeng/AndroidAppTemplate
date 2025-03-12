package com.pine.tool.request.response;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface IAsyncResponse<T> {
    void onResponse(T t);

    boolean onFail(Exception e);

    void onCancel();
}
