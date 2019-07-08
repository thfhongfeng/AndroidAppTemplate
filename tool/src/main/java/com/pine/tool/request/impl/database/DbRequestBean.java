package com.pine.tool.request.impl.database;

import com.pine.tool.request.IRequestManager;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DbRequestBean implements Serializable {
    // 该callback对应的请求的key
    private String key;
    private String url;
    private Map<String, String> params;
    //模块标识，默认common
    private String moduleTag = "common";
    private int what;
    private boolean needLogin;

    // for download
    private String saveFolder;
    private String saveFileName;
    private boolean isContinue;
    private boolean isDeleteOld;

    // for upload
    private String upLoadFileKey;
    private List<FileBean> uploadFileList;

    private IRequestManager.ActionType actionType = IRequestManager.ActionType.COMMON;

    public DbRequestBean(int what) {
        this.what = what;
        this.key = hashCode() + "_ " + what;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getUpLoadFileKey() {
        return upLoadFileKey;
    }

    public void setUpLoadFileKey(String upLoadFileKey) {
        this.upLoadFileKey = upLoadFileKey;
    }

    public List<FileBean> getUploadFileList() {
        return uploadFileList;
    }

    public void setUploadFileList(List<FileBean> uploadFileList) {
        this.uploadFileList = uploadFileList;
    }

    public IRequestManager.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(IRequestManager.ActionType actionType) {
        this.actionType = actionType;
    }

    public static class FileBean implements Serializable {
        private int what;
        private String fileKey;
        private String fileName;
        private File file;
        private int position;

        public FileBean(String fileKey, String fileName, File file, int position) {
            this.what = hashCode();
            this.fileKey = fileKey;
            this.fileName = fileName;
            this.file = file;
            this.position = position;
        }

        public int getWhat() {
            return what;
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
    }
}
