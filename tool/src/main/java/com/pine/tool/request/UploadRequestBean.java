package com.pine.tool.request;

import androidx.annotation.NonNull;

import com.pine.tool.request.callback.UploadCallback;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class UploadRequestBean extends RequestBean {
    // 上传文件的key
    private String upLoadFileKey;
    // 上传文件集合
    private List<FileBean> uploadFileList;
    // 文件上传回调
    private UploadCallback uploadCallback;

    public UploadRequestBean(@NonNull String url, int what, Map<String, String> params,
                             String upLoadFileKey, @NonNull String fileName, @NonNull File file) {
        super(url, what, params);
        this.upLoadFileKey = upLoadFileKey;
        this.uploadFileList = new ArrayList<>();
        FileBean fileBean = new FileBean(upLoadFileKey, fileName, file, 0);
        uploadFileList.add(fileBean);
    }

    public UploadRequestBean(@NonNull String url, int what, Map<String, String> params,
                             String upLoadFileKey, @NonNull List<FileBean> uploadFileList) {
        this(url, what, params, upLoadFileKey, uploadFileList,
                false, null, RequestMethod.POST);
    }

    public UploadRequestBean(@NonNull String url, int what, Map<String, String> params,
                             String upLoadFileKey, @NonNull List<FileBean> uploadFileList,
                             boolean needLogin) {
        this(url, what, params, upLoadFileKey, uploadFileList,
                needLogin, null, RequestMethod.POST);
    }

    public UploadRequestBean(@NonNull String url, int what, Map<String, String> params,
                             String upLoadFileKey, @NonNull List<FileBean> uploadFileList,
                             boolean needLogin, Object sign) {
        this(url, what, params, upLoadFileKey, uploadFileList,
                needLogin, sign, RequestMethod.POST);
    }

    public UploadRequestBean(@NonNull String url, int what, Map<String, String> params,
                             String upLoadFileKey, @NonNull List<FileBean> uploadFileList,
                             boolean needLogin, Object sign, RequestMethod requestMethod) {
        super(url, what, params, needLogin, sign, requestMethod);
        this.upLoadFileKey = upLoadFileKey;
        this.uploadFileList = uploadFileList;
    }

    public String getUpLoadFileKey() {
        return upLoadFileKey;
    }

    protected void setUpLoadFileKey(String upLoadFileKey) {
        this.upLoadFileKey = upLoadFileKey;
    }

    public List<FileBean> getUploadFileList() {
        return uploadFileList;
    }

    protected void setUploadFileList(List<FileBean> uploadFileList) {
        this.uploadFileList = uploadFileList;
    }

    public UploadCallback getUploadCallback() {
        return uploadCallback;
    }

    protected void setUploadCallback(UploadCallback uploadCallback) {
        this.uploadCallback = uploadCallback;
    }

    public static class FileBean implements Serializable {
        private int what;
        private String fileKey;
        private String fileName;
        private File file;
        private int position;

        public FileBean(String fileKey, @NonNull String fileName, @NonNull File file, int position) {
            this.what = hashCode();
            this.fileKey = fileKey;
            this.fileName = fileName;
            this.file = file;
            this.position = position;
        }

        public int getWhat() {
            return what;
        }

        public void setWhat(int what) {
            this.what = what;
        }

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
