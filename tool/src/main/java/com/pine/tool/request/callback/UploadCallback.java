package com.pine.tool.request.callback;

import com.pine.tool.request.RequestBean;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class UploadCallback extends AbstractBaseCallback {

    public abstract void onStart(int what, RequestBean.FileBean fileBean);

    public abstract void onCancel(int what, RequestBean.FileBean fileBean);

    public abstract void onProgress(int what, RequestBean.FileBean fileBean, int progress);

    public abstract boolean onError(int what, RequestBean.FileBean fileBean, Exception e);

    public abstract void onFinish(int what, RequestBean.FileBean fileBean);
}
