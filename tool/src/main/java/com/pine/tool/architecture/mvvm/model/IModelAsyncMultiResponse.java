package com.pine.tool.architecture.mvvm.model;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface IModelAsyncMultiResponse<T, M> {
    void onResponse(T t, M m);

    boolean onFail(Exception e);

    void onCancel();
}
