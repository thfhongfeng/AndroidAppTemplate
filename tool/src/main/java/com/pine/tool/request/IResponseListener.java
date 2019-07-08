package com.pine.tool.request;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IResponseListener {
    interface OnResponseListener {
        void onStart(int what);

        void onSucceed(int what, Response response);

        void onFailed(int what, Response response);

        void onFinish(int what);
    }

    interface OnDownloadListener {
        void onDownloadError(int what, Exception exception);

        void onStart(int what, boolean isResume, long rangeSize, long allCount);

        /**
         * @param what
         * @param progress  0-100
         * @param fileCount
         * @param speed
         */
        void onProgress(int what, int progress, long fileCount, long speed);

        void onFinish(int what, String filePath);

        void onCancel(int what);
    }

    interface OnUploadListener {
        void onStart(int what, RequestBean.FileBean fileBean);

        void onCancel(int what, RequestBean.FileBean fileBean);

        /**
         * @param what
         * @param fileBean
         * @param progress 0-100
         */
        void onProgress(int what, RequestBean.FileBean fileBean, int progress);

        void onFinish(int what, RequestBean.FileBean fileBean);

        void onError(int what, RequestBean.FileBean fileBean, Exception exception);
    }
}
