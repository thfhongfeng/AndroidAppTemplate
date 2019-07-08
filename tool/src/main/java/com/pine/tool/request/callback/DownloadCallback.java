package com.pine.tool.request.callback;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class DownloadCallback extends AbstractBaseCallback {

    public abstract void onStart(int what, boolean isResume, long rangeSize, long allCount);

    /**
     * @param what
     * @param progress  0-100
     * @param fileCount
     * @param speed
     */
    public abstract void onProgress(int what, int progress, long fileCount, long speed);

    public abstract void onFinish(int what, String filePath);

    public abstract boolean onError(int what, Exception e);

    public abstract void onCancel(int what);
}
