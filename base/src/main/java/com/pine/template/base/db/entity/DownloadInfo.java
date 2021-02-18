package com.pine.template.base.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "db_download_info")
public class DownloadInfo {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    // 下载类型：0-缺省；1-一般文件下载；2-网络文件缓存
    @NonNull
    private int type;

    // 标识（表示属于哪个模块或者业务的下载任务）
    @NonNull
    @ColumnInfo(name = "module_tag")
    private String moduleTag;

    // 关联业务id
    @NonNull
    @ColumnInfo(name = "relate_id")
    private String relateId;

    // 远程下载地址
    @NonNull
    @ColumnInfo(name = "remote_url")
    private String remoteUrl;

    // 本地保存地址
    @NonNull
    @ColumnInfo(name = "local_url")
    private String localUrl;

    // 本地保存文件名
    @NonNull
    @ColumnInfo(name = "file_name")
    private String fileName;

    // 文件总大小
    @NonNull
    @ColumnInfo(name = "file_size")
    private long fileSize;

    // 已下载大小
    @NonNull
    @ColumnInfo(name = "downloaded_size")
    private long downloadedSize;

    // 当前下载速度
    @NonNull
    @ColumnInfo(name = "download_speed")
    private long downloadSpeed;

    // 下载状态：0-缺省；1-等待下载队列中；2-下载中；3-暂停；99-下载完成
    @NonNull
    @ColumnInfo(name = "download_state")
    private int downloadState;

    // 业务相关额外的信息
    @ColumnInfo(name = "extra_info")
    private String extraInfo;

    // 备注
    @ColumnInfo(name = "remark")
    private String remark;

    // 是否有效：0-有效；1-无效；99-已删除
    @NonNull
    @ColumnInfo(name = "invalid")
    private int invalid;

    // 更新时间戳（毫秒）
    @NonNull
    @ColumnInfo(name = "update_time")
    private long updateTime;

    // 创建时间戳（毫秒）
    @NonNull
    @ColumnInfo(name = "create_time")
    private long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(@NonNull String moduleTag) {
        this.moduleTag = moduleTag;
    }

    @NonNull
    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(@NonNull String relateId) {
        this.relateId = relateId;
    }

    @NonNull
    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(@NonNull String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @NonNull
    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(@NonNull String localUrl) {
        this.localUrl = localUrl;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
