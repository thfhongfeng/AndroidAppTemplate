package com.pine.tool.architecture.mvvm.model;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface IModelAsyncResponse<T> {
    void onResponse(T t);

    boolean onFail(Exception e);

    void onCancel();
}
