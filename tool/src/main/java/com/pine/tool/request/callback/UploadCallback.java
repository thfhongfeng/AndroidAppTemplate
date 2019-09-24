package com.pine.tool.request.callback;

import com.pine.tool.request.UploadRequestBean;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class UploadCallback extends AbstractBaseCallback {

    public abstract void onStart(int what, UploadRequestBean.FileBean fileBean);

    public abstract void onCancel(int what, UploadRequestBean.FileBean fileBean);

    public abstract void onProgress(int what, UploadRequestBean.FileBean fileBean, int progress);

    public abstract boolean onError(int what, UploadRequestBean.FileBean fileBean, Exception e);

    public abstract void onFinish(int what, UploadRequestBean.FileBean fileBean);
}
