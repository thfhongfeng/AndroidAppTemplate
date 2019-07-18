package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_app_version")
public class AppVersion {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    private String packageName;

    @NonNull
    private String versionName;

    @NonNull
    private int versionCode;

    private String minSupportedVersion;

    // 是否强制更新：0-不强制；1-强制
    private int force;

    private String fileName;

    private String path;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(@NonNull String packageName) {
        this.packageName = packageName;
    }

    @NonNull
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(@NonNull String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getMinSupportedVersion() {
        return minSupportedVersion;
    }

    public void setMinSupportedVersion(String minSupportedVersion) {
        this.minSupportedVersion = minSupportedVersion;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
