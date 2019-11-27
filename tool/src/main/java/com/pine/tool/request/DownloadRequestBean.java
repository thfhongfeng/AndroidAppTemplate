package com.pine.tool.request;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class DownloadRequestBean extends RequestBean {
    // 下载文件保存目录
    private String saveFolder;
    // 下载文件保存文件名
    private String saveFileName;
    // 是否继续之前的下载
    private boolean isContinue;
    // 是否删除之前的下载
    private boolean isDeleteOld;

    public DownloadRequestBean(@NonNull String url, int what, Map<String, String> params,
                               @NonNull String saveFolder, @NonNull String saveFileName) {
        this(url, what, params, saveFolder, saveFileName, false, true,
                false, null, RequestMethod.POST);
    }

    public DownloadRequestBean(@NonNull String url, int what, Map<String, String> params,
                               @NonNull String saveFolder, @NonNull String saveFileName,
                               boolean isContinue, boolean isDeleteOld) {
        this(url, what, params, saveFolder, saveFileName, isContinue, isDeleteOld,
                false, null, RequestMethod.POST);
    }

    public DownloadRequestBean(@NonNull String url, int what, Map<String, String> params,
                               @NonNull String saveFolder, @NonNull String saveFileName,
                               boolean isContinue, boolean isDeleteOld,
                               boolean needLogin) {
        this(url, what, params, saveFolder, saveFileName, isContinue, isDeleteOld,
                needLogin, null, RequestMethod.POST);
    }

    public DownloadRequestBean(@NonNull String url, int what, Map<String, String> params,
                               @NonNull String saveFolder, @NonNull String saveFileName,
                               boolean isContinue, boolean isDeleteOld,
                               boolean needLogin, Object sign) {
        this(url, what, params, saveFolder, saveFileName, isContinue, isDeleteOld,
                needLogin, sign, RequestMethod.POST);
    }

    public DownloadRequestBean(@NonNull String url, int what, Map<String, String> params,
                               @NonNull String saveFolder, @NonNull String saveFileName,
                               boolean isContinue, boolean isDeleteOld,
                               boolean needLogin, Object sign, RequestMethod requestMethod) {
        super(url, what, params, needLogin, sign, requestMethod);
        this.saveFolder = saveFolder;
        this.saveFileName = saveFileName;
        this.isContinue = isContinue;
        this.isDeleteOld = isDeleteOld;
    }

    public String getSaveFolder() {
        return saveFolder;
    }

    public void setSaveFolder(String saveFolder) {
        this.saveFolder = saveFolder;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public boolean isDeleteOld() {
        return isDeleteOld;
    }

    public void setDeleteOld(boolean deleteOld) {
        isDeleteOld = deleteOld;
    }
}
