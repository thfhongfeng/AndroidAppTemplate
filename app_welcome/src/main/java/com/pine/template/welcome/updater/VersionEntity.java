package com.pine.template.welcome.updater;

import com.pine.tool.util.AppUtils;

import java.io.File;

/**
 * Created by tanghongfeng on 2018/9/25
 */

public class VersionEntity {

    /**
     * packageName : ""
     * VersionCode : 10002
     * VersionName : "1.0.002"
     * fileName : ""
     * fileSize : 120120150
     * downloadUrl : ""
     * force : 0 // 0-代表不更新，1-代表有版本更新，不需要强制升级，2-代表有版本更新，需要强制升级
     * minSupportedVersion: 10001
     * remark : ""
     */

    private String packageName;
    private int versionCode;
    private String versionName;
    private String fileName;
    private long fileSize;
    private String downloadUrl;
    private int force;
    private int minSupportedVersion;
    private String remark;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getMinSupportedVersion() {
        return minSupportedVersion;
    }

    public void setMinSupportedVersion(int minSupportedVersion) {
        this.minSupportedVersion = minSupportedVersion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isForce() {
        return force == 2;
    }

    public boolean isFileDownloaded(File oldFile) {
        return oldFile != null && oldFile.exists() && getFileSize() > 0
                && oldFile.length() == getFileSize();
    }

    public boolean isNewVersion() {
        int versionCode = AppUtils.getVersionCode();
        boolean isNewVersion = versionCode < getVersionCode();
        return isNewVersion;
    }

    @Override
    public String toString() {
        return "VersionEntity{" +
                "packageName='" + packageName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", force=" + force +
                ", minSupportedVersion=" + minSupportedVersion +
                ", remark='" + remark + '\'' +
                '}';
    }
}
