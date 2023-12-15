package com.pine.template.base.bean;

/**
 * Created by tanghongfeng on 2018/9/25
 */

public class VersionEntity {

    /**
     * ApkMd5 : ""
     * ApkSize : 0
     * AppKey : ""
     * code : 0  // 响应码：0-成功；1-失败
     * DownloadUrl : ""
     * ModifyContent : ""
     * Msg : ""
     * UpdateStatus : 0 // 0-代表不更新，1-代表有版本更新，不需要强制升级，2-代表有版本更新，需要强制升级
     * VersionCode : 1
     * VersionName : ""
     */

    private String ApkMd5;
    private long ApkSize;
    private String AppKey;
    private String DownloadUrl;
    private String ModifyContent;
    private String Msg;
    private int UpdateStatus;
    private int VersionCode;
    private String VersionName;

    public String getApkMd5() {
        return ApkMd5;
    }

    public void setApkMd5(String apkMd5) {
        ApkMd5 = apkMd5;
    }

    public long getApkSize() {
        return ApkSize;
    }

    public void setApkSize(long apkSize) {
        ApkSize = apkSize;
    }

    public String getAppKey() {
        return AppKey;
    }

    public void setAppKey(String appKey) {
        AppKey = appKey;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getModifyContent() {
        return ModifyContent;
    }

    public void setModifyContent(String modifyContent) {
        ModifyContent = modifyContent;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getUpdateStatus() {
        return UpdateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        UpdateStatus = updateStatus;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public boolean isForce() {
        return UpdateStatus == 2;
    }
}
