package com.pine.template.base.business.track.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "db_app_track")
public class AppTrack {
    // id
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    // 用户id
    @ColumnInfo(name = "account_id")
    private String accountId = "default";

    // 用户类型
    @NonNull
    @ColumnInfo(name = "account_type")
    private int accountType = -1;

    // 用户名
    @ColumnInfo(name = "user_name")
    private String userName;

    // ip
    @ColumnInfo(name = "ip")
    private String ip;

    // 版本名
    @ColumnInfo(name = "version_name")
    private String versionName;

    // 版本code
    @ColumnInfo(name = "version_code")
    private int versionCode;

    // 埋点所属模块
    @NonNull
    @ColumnInfo(name = "module_tag")
    private String moduleTag;

    // 当前class名
    @NonNull
    @ColumnInfo(name = "cur_class")
    private String curClass;

    // 埋点类型：0-设置事件；1-页面停留事件； ……;9899-业务操作日志;9999-埋点数据超容量
    @NonNull
    @ColumnInfo(name = "track_type")
    private int trackType;

    // 动作名称（埋点事件对应的动作）
    @ColumnInfo(name = "action_name")
    private String actionName;

    // 动作数据（不同动作对应数据结构不一样）
    @ColumnInfo(name = "action_data")
    private String actionData;

    // 动作开始时间
    @ColumnInfo(name = "time_in_stamp")
    private long actionInStamp;

    // 动作结束时间
    @ColumnInfo(name = "time_out_stamp")
    private long actionOutStamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @NonNull
    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(@NonNull String moduleTag) {
        this.moduleTag = moduleTag;
    }

    @NonNull
    public String getCurClass() {
        return curClass;
    }

    public void setCurClass(@NonNull String curClass) {
        this.curClass = curClass;
    }

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public long getActionInStamp() {
        return actionInStamp;
    }

    public void setActionInStamp(long actionInStamp) {
        this.actionInStamp = actionInStamp;
    }

    public long getActionOutStamp() {
        return actionOutStamp;
    }

    public void setActionOutStamp(long actionOutStamp) {
        this.actionOutStamp = actionOutStamp;
    }

    @Override
    public String toString() {
        return "AppTrack{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", accountType=" + accountType +
                ", userName='" + userName + '\'' +
                ", ip='" + ip + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", moduleTag='" + moduleTag + '\'' +
                ", curClass='" + curClass + '\'' +
                ", trackType=" + trackType +
                ", actionName='" + actionName + '\'' +
                ", actionData='" + actionData + '\'' +
                ", actionInStamp=" + actionInStamp +
                ", actionOutStamp=" + actionOutStamp +
                '}';
    }
}
