package com.pine.tool.request.response;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface IAsyncMultiResponse<T, M> {
    void onResponse(T t, M m);

    boolean onFail(Exception e);

    void onCancel();
}
