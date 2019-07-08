package com.pine.tool.request;

import com.pine.tool.request.IRequestManager.ActionType;
import com.pine.tool.request.IRequestManager.RequestType;
import com.pine.tool.request.callback.AbstractBaseCallback;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class RequestBean {
    // 该callback对应的请求的key
    private String key;
    private String url;
    private RequestMethod requestMethod;
    private Map<String, String> params;
    //模块标识，默认common
    private String moduleTag = "common";
    private int what;
    private Object sign;
    private boolean needLogin;
    private RequestType requestType;
    private AbstractBaseCallback callback;

    // for download
    private String saveFolder;
    private String saveFileName;
    private boolean isContinue;
    private boolean isDeleteOld;

    // for upload
    private String upLoadFileKey;
    private List<FileBean> uploadFileList;

    private ActionType actionType = ActionType.COMMON;

    private Response response;

    public RequestBean(int what, AbstractBaseCallback callback) {
        this.what = what;
        this.callback = callback;
        this.key = hashCode() + "_ " + what;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    protected void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, String> getParams() {
        return params;
    }

    protected void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    protected void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public int getWhat() {
        return what;
    }

    protected void setWhat(int what) {
        this.what = what;
    }

    public Object getSign() {
        return sign;
    }

    protected void setSign(Object sign) {
        this.sign = sign;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    protected void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    protected void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public AbstractBaseCallback getCallback() {
        return callback;
    }

    public void setCallback(AbstractBaseCallback callback) {
        this.callback = callback;
    }

    public ActionType getActionType() {
        return actionType;
    }

    protected void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Response getResponse() {
        return response;
    }

    protected void setResponse(Response response) {
        this.response = response;
    }

    public String getSaveFolder() {
        return saveFolder;
    }

    protected void setSaveFolder(String saveFolder) {
        this.saveFolder = saveFolder;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    protected void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public boolean isContinue() {
        return isContinue;
    }

    protected void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public boolean isDeleteOld() {
        return isDeleteOld;
    }

    protected void setDeleteOld(boolean deleteOld) {
        isDeleteOld = deleteOld;
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
