package com.pine.template.base.component.uploader.bean;

/**
 * Created by tanghongfeng on 2019/10/14.
 */

public class RemoteUploadFileInfo {
    private String url;
    private String fileName;
    private String fileSize;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
