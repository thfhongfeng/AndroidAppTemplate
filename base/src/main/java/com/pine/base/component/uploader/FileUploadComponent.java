package com.pine.base.component.uploader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.base.R;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.request.callback.UploadCallback;
import com.pine.tool.util.FileUtils;
import com.pine.tool.util.ImageUtils;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/11/1
 */

public class FileUploadComponent {
    public static final int TYPE_ALL = 1;
    public static final int TYPE_IMAGE = 11;
    public static final int TYPE_WORD_DOC = 12;
    public static final int TYPE_TXT = 13;
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private WeakReference<Context> mContext;
    private Map<Integer, Object> mRequestMap;
    private long mMaxFileSize = 1024 * 1024;
    private int mOutFileWidth = 1440;
    private int mOutFileHeight = 2550;

    public FileUploadComponent(Context context) {
        mContext = new WeakReference<>(context);
        mRequestMap = new HashMap<>();
    }

    public FileUploadComponent(Context context, long maxFileSize) {
        mContext = new WeakReference<>(context);
        mRequestMap = new HashMap<>();
        mMaxFileSize = maxFileSize;
    }

    public FileUploadComponent(Context context, long maxFileSize, int outFileWidth, int outFileHeight) {
        mContext = new WeakReference<>(context);
        mRequestMap = new HashMap<>();
        mMaxFileSize = maxFileSize;
        mOutFileWidth = outFileWidth;
        mOutFileHeight = outFileHeight;
    }

    public void startSingle(@NonNull final FileUploadBean uploadBean, final OneByOneUploadCallback callback) {
        File file = checkFile(uploadBean, callback);
        if (file == null) {
            LogUtils.d(TAG, "upload check fail");
            return;
        }
        mRequestMap.put(uploadBean.hashCode(), uploadBean);
        RequestManager.setUploadRequest(uploadBean.getRequestUrl(), uploadBean.getParams(),
                uploadBean.getFileKey(), uploadBean.getFileName(), file, uploadBean.hashCode(),
                uploadBean.hashCode(), new UploadCallback() {
                    @Override
                    public void onStart(int what, RequestBean.FileBean fileBean) {
                        LogUtils.d(TAG, "onStart what :" + what);
                        if (callback != null) {
                            callback.onStart(uploadBean);
                        }
                    }

                    @Override
                    public void onCancel(int what, RequestBean.FileBean fileBean) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onCancel what :" + what);
                        if (callback != null) {
                            callback.onCancel(uploadBean);
                        }
                        deleteTempFile(uploadBean);
                    }

                    @Override
                    public void onProgress(int what, RequestBean.FileBean fileBean, int progress) {
                        LogUtils.d(TAG, "onProgress what:" + what + ", progress:" + progress);
                        if (callback != null) {
                            callback.onProgress(uploadBean, progress);
                        }
                    }

                    @Override
                    public boolean onError(int what, RequestBean.FileBean fileBean, Exception e) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onError Exception :" + e);
                        deleteTempFile(uploadBean);
                        return false;
                    }

                    @Override
                    public void onFinish(int what, RequestBean.FileBean fileBean) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onFinish what:" + what);
                        deleteTempFile(uploadBean);
                    }
                }, new JsonCallback() {

                    @Override
                    public void onResponse(int what, JSONObject jsonObject) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onResponse what:" + what);
                        if (callback != null) {
                            callback.onSuccess(uploadBean, jsonObject);
                        }
                    }

                    @Override
                    public boolean onFail(int what, Exception e) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onError what:" + what);
                        if (callback != null) {
                            callback.onFailed(uploadBean, e.toString());
                        }
                        return false;
                    }

                    @Override
                    public void onCancel(int what) {
                        mRequestMap.remove(uploadBean.hashCode());
                        LogUtils.d(TAG, "onCancel what :" + what);
                        if (callback != null) {
                            callback.onCancel(uploadBean);
                        }
                        deleteTempFile(uploadBean);
                    }
                });
    }

    public void startOneByOne(@NonNull List<FileUploadBean> fileBeanList, OneByOneUploadCallback callback) {
        if (fileBeanList != null && fileBeanList.size() > 0) {
            for (int i = 0; i < fileBeanList.size(); i++) {
                startSingle(fileBeanList.get(i), callback);
            }
        }
    }

    public void startTogether(@NonNull String url, @NonNull Map<String, String> params,
                              String fileKey, final @NonNull List<FileUploadBean> uploadBeanList,
                              final TogetherUploadListCallback callback) {
        List<RequestBean.FileBean> checkFileList = checkFileList(uploadBeanList, callback);
        if (checkFileList == null) {
            LogUtils.d(TAG, "upload check fail");
            return;
        }
        final Map<Integer, Integer> progressMap = new HashMap<>();
        final int totalProgress = checkFileList.size() * 100;
        mRequestMap.put(uploadBeanList.hashCode(), uploadBeanList);
        RequestManager.setUploadRequest(url, params, fileKey, checkFileList,
                uploadBeanList.hashCode(), uploadBeanList.hashCode(), new UploadCallback() {
                    int preActualProgress = -10;

                    @Override
                    public void onStart(int what, RequestBean.FileBean fileBean) {
                        LogUtils.d(TAG, "onStart what :" + what);
                        if (callback != null) {
                            callback.onSingleFileStart(uploadBeanList.get(fileBean.getPosition()));
                        }
                    }

                    @Override
                    public void onCancel(int what, RequestBean.FileBean fileBean) {
                        LogUtils.d(TAG, "onCancel what :" + what);
                        if (callback != null) {
                            callback.onSingleFileCancel(uploadBeanList.get(fileBean.getPosition()));
                        }
                    }

                    @Override
                    public void onProgress(int what, RequestBean.FileBean fileBean, int progress) {
                        progressMap.put(what, progress);
                        int curProgress = 0;
                        Iterator<Map.Entry<Integer, Integer>> iterator = progressMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, Integer> entry = iterator.next();
                            curProgress += entry.getValue();
                        }
                        int actualPro = curProgress * 100 / totalProgress;
                        if (preActualProgress + 1 <= actualPro) {
                            LogUtils.d(TAG, "onProgress what:" + what + ", progress:" + actualPro);
                            if (callback != null) {
                                callback.onProgress(uploadBeanList, actualPro);
                                preActualProgress = actualPro;
                            }
                        }
                    }

                    @Override
                    public boolean onError(int what, RequestBean.FileBean fileBean, Exception e) {
                        LogUtils.d(TAG, "onError Exception :" + e);
                        if (callback != null) {
                            callback.onSingleFileError(uploadBeanList.get(fileBean.getPosition()));
                        }
                        return false;
                    }

                    @Override
                    public void onFinish(int what, RequestBean.FileBean fileBean) {
                        LogUtils.d(TAG, "onFinish what:" + what);
                        if (callback != null) {
                            callback.onSingleFileFinish(uploadBeanList.get(fileBean.getPosition()));
                        }
                    }
                }, new JsonCallback() {

                    @Override
                    public void onResponse(int what, JSONObject jsonObject) {
                        mRequestMap.remove(uploadBeanList.hashCode());
                        LogUtils.d(TAG, "onResponse what:" + what);
                        if (callback != null) {
                            callback.onSuccess(uploadBeanList, jsonObject);
                        }
                        deleteTempFileList(uploadBeanList);
                    }

                    @Override
                    public boolean onFail(int what, Exception e) {
                        mRequestMap.remove(uploadBeanList.hashCode());
                        LogUtils.d(TAG, "onFail what:" + what);
                        if (callback != null) {
                            callback.onFailed(uploadBeanList, e.toString());
                        }
                        deleteTempFileList(uploadBeanList);
                        return false;
                    }

                    @Override
                    public void onCancel(int what) {
                        mRequestMap.remove(uploadBeanList.hashCode());
                        LogUtils.d(TAG, "onCancel what:" + what);
                        if (callback != null) {
                            callback.onCancel(uploadBeanList);
                        }
                        deleteTempFileList(uploadBeanList);
                    }
                });
    }

    private File checkFile(FileUploadBean fileBean, OneByOneUploadCallback callback) {
        if (TextUtils.isEmpty(fileBean.getLocalFilePath()) || TextUtils.isEmpty(fileBean.getRequestUrl())) {
            LogUtils.d(TAG, "file bean is null");
            return null;
        }
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        if (mRequestMap.get(fileBean.hashCode()) != null) {
            LogUtils.d(TAG, "request is in processing");
            if (callback != null) {
                callback.onFailed(fileBean, context.getString(R.string.base_file_upload_file_is_uploading));
            }
            return null;
        }
        File file = new File(fileBean.getLocalFilePath());
        if (!file.exists()) {
            if (callback != null) {
                callback.onFailed(fileBean, context.getString(R.string.base_file_upload_file_null));
            }
            return null;
        }
        if (fileBean.getFileType() == TYPE_IMAGE) {
            String targetPath = context.getExternalCacheDir() + File.separator + fileBean.getFileName();
            file = compressImage(fileBean.getLocalFilePath(), targetPath);
            if (file == null) {
                callback.onFailed(fileBean, context.getString(R.string.base_file_upload_compress_file_null));
                return null;
            }
            fileBean.setLocalTempFilePath(file.getPath());
        }
        return file;
    }

    private List<RequestBean.FileBean> checkFileList(@NonNull List<FileUploadBean> fileBeanList,
                                                     TogetherUploadListCallback callback) {
        if (fileBeanList == null && fileBeanList.size() < 1) {
            LogUtils.d(TAG, "no file bean in list");
            return null;
        }
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        if (mRequestMap.get(fileBeanList.hashCode()) != null) {
            LogUtils.d(TAG, "request is in processing");
            if (callback != null) {
                callback.onFailed(fileBeanList, context.getString(R.string.base_file_upload_file_is_uploading));
            }
            return null;
        }
        List<RequestBean.FileBean> httpFileBeanList = new ArrayList<>();
        for (int i = 0; i < fileBeanList.size(); i++) {
            FileUploadBean fileBean = fileBeanList.get(i);
            if (TextUtils.isEmpty(fileBean.getLocalFilePath())) {
                if (callback != null) {
                    callback.onFailed(fileBeanList, context.getString(R.string.base_file_upload_file_null));
                }
                return null;
            }
            File file = new File(fileBean.getLocalFilePath());
            if (!file.exists()) {
                if (callback != null) {
                    callback.onFailed(fileBeanList, context.getString(R.string.base_file_upload_file_null));
                }
                return null;
            }
            if (fileBean.getFileType() == TYPE_IMAGE) {
                String targetPath = context.getExternalCacheDir() + File.separator + fileBean.getFileName();
                file = compressImage(fileBean.getLocalFilePath(), targetPath);
                if (file == null) {
                    callback.onFailed(fileBeanList, context.getString(R.string.base_file_upload_compress_file_null));
                    return null;
                }
                fileBean.setLocalTempFilePath(file.getPath());
            }
            RequestBean.FileBean httpFile = new RequestBean.FileBean(fileBean.getFileKey(),
                    fileBean.getFileName(), file, i);
            httpFileBeanList.add(httpFile);
        }
        return httpFileBeanList.size() < 1 ? null : httpFileBeanList;
    }

    private File compressImage(String srcFilePath, String targetFilePath) {
        FileUtils.deleteFile(targetFilePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageUtils.compressBySize(srcFilePath, mMaxFileSize, mOutFileWidth, mOutFileHeight, bao);
        File targetFile = new File(targetFilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
            bao.writeTo(bos);
            bos.flush();
            bos.close();
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteTempFile(FileUploadBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.getLocalTempFilePath())) {
            new File(bean.getLocalTempFilePath()).deleteOnExit();
            bean.setLocalTempFilePath("");
        }
    }

    private void deleteTempFileList(List<FileUploadBean> list) {
        if (list == null) {
            return;
        }
        for (FileUploadBean bean : list) {
            deleteTempFile(bean);
        }
    }

    public void cancel(FileUploadBean fileBean) {
        if (fileBean != null) {
            RequestManager.cancelBySign(fileBean.hashCode());
        }
    }

    public void cancel(List<FileUploadBean> fileBeanList) {
        if (fileBeanList != null) {
            RequestManager.cancelBySign(fileBeanList.hashCode());
        }
    }

    public void cancelAll() {
        for (Object object : mRequestMap.values()) {
            if (object instanceof List) {
                RequestManager.cancelBySign(object.hashCode());
            } else if (object instanceof FileUploadBean) {
                cancel((FileUploadBean) object);
            }
        }
        mRequestMap.clear();
    }

    /**
     * 一个一个上传文件的回调（n个文件对应n次请求）
     */
    public interface OneByOneUploadCallback {
        // 文件开始上传回调
        void onStart(FileUploadBean uploadBean);

        // 文件开始上传进度回调
        void onProgress(FileUploadBean uploadBean, int progress);

        // 文件上传取消回调
        void onCancel(FileUploadBean uploadBean);

        // 请求出错回调
        void onFailed(FileUploadBean uploadBean, String message);

        // 请求成功回调
        void onSuccess(FileUploadBean uploadBean, JSONObject response);
    }

    /**
     * 多文件一起上传的回调（n个文件对应一次请求）
     */
    public interface TogetherUploadListCallback {

        // 文件开始上传回调
        void onSingleFileStart(FileUploadBean uploadBean);

        // 文件开始上传进度回调
        void onSingleFileProgress(FileUploadBean uploadBean, int progress);

        // 文件上传完成回调
        void onSingleFileFinish(FileUploadBean uploadBean);

        // 文件上传取消回调
        void onSingleFileError(FileUploadBean uploadBean);

        // 文件上传取消回调
        void onSingleFileCancel(FileUploadBean uploadBean);

        // 文件整体上传进度回调
        void onProgress(List<FileUploadBean> uploadBeanList, int progress);

        // 请求取消回调
        void onCancel(List<FileUploadBean> uploadBeanList);

        // 请求出错回调
        void onFailed(List<FileUploadBean> uploadBeanList, String message);

        // 请求成功回调
        void onSuccess(List<FileUploadBean> uploadBeanList, JSONObject response);
    }

    /**
     * 多文件一起上传的回调（n个文件对应一次请求）
     */
    public abstract static class SimpleTogetherUploadListCallback implements TogetherUploadListCallback {

        // 文件开始上传回调
        public void onSingleFileStart(FileUploadBean uploadBean) {

        }

        // 文件开始上传进度回调
        public void onSingleFileProgress(FileUploadBean uploadBean, int progress) {

        }

        // 文件上传完成回调
        public void onSingleFileFinish(FileUploadBean uploadBean) {

        }

        // 文件上传取消回调
        public void onSingleFileError(FileUploadBean uploadBean) {

        }

        // 文件上传取消回调
        public void onSingleFileCancel(FileUploadBean uploadBean) {

        }
    }

    public interface OneByOneUploadAdapter {
        // 获取上传文件的服务器地址
        String getUploadUrl();

        String getFileKey(FileUploadBean fileUploadBean);

        Map<String, String> getUploadParam(FileUploadBean fileUploadBean);

        String getRemoteUrlFromResponse(FileUploadBean fileUploadBean, JSONObject response);
    }

    public interface TogetherUploadAdapter {
        // 获取上传文件的服务器地址
        String getUploadUrl();

        String getFileKey(FileUploadBean fileUploadBean);

        String getFilesKey(List<FileUploadBean> fileUploadBeanList);

        Map<String, String> getUploadParam(List<FileUploadBean> fileUploadBeanList);

        List<String> getRemoteUrlListFromResponse(List<FileUploadBean> fileUploadBeanList, JSONObject response);
    }
}
