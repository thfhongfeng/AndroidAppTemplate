package com.pine.base.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 附件缓存表
 */

@Entity(tableName = "app_attach_cache")
public class AppAttachCache {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    // 下载附件所属模块
    @NonNull
    @ColumnInfo(name = "module")
    private String module;

    // 下载附件文件名
    @ColumnInfo(name = "file_name")
    private String fileName;

    @NonNull
    @ColumnInfo(name = "file_size")
    private long fileSize;

    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    @NonNull
    @ColumnInfo(name = "cache_url")
    private String cacheUrl;

    @ColumnInfo(name = "remark")
    private String remark;

    // 是否有效：0-有效；1-无效；2-已删除
    @NonNull
    @ColumnInfo(name = "invalid")
    private int invalid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getModule() {
        return module;
    }

    public void setModule(@NonNull String module) {
        this.module = module;
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

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(@NonNull String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    @Override
    public String toString() {
        return "AppAttachCache{" +
                "id=" + id +
                ", module='" + module + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", url='" + url + '\'' +
                ", cacheUrl='" + cacheUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", invalid=" + invalid +
                '}';
    }
}
