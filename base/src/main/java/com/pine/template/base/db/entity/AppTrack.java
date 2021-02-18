package com.pine.template.base.db.entity;

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

    // 埋点所属模块
    @NonNull
    @ColumnInfo(name = "module_tag")
    private String moduleTag;

    // 用户id
    @ColumnInfo(name = "account_id")
    private String accountId;

    @NonNull
    @ColumnInfo(name = "account_type")
    private int accountType;

    // 用户名
    @ColumnInfo(name = "user_name")
    private String userName;

    // 埋点类型：0-点击事件；1-页面
    @NonNull
    @ColumnInfo(name = "track_type")
    private int trackType;

    // 业务title
    @ColumnInfo
    private String title;

    // 当前class名
    @NonNull
    @ColumnInfo(name = "cur_class")
    private String curClass;

    // 前一个class名
    @ColumnInfo(name = "pre_class")
    private String preClass;

    // 按键名称（对应埋点点击事件类型）
    @ColumnInfo(name = "button_name")
    private String buttonName;

    @ColumnInfo(name = "ip")
    private String ip;

    // 进入页面/点击button 时间
    @ColumnInfo(name = "time_in_stamp")
    private long timeInStamp;

    // 离开页面时间（对应埋点页面类型）
    @ColumnInfo(name = "time_out_stamp")
    private long timeOutStamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(@NonNull String moduleTag) {
        this.moduleTag = moduleTag;
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

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    public String getCurClass() {
        return curClass;
    }

    public void setCurClass(@NonNull String curClass) {
        this.curClass = curClass;
    }

    public String getPreClass() {
        return preClass;
    }

    public void setPreClass(String preClass) {
        this.preClass = preClass;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTimeInStamp() {
        return timeInStamp;
    }

    public void setTimeInStamp(long timeInStamp) {
        this.timeInStamp = timeInStamp;
    }

    public long getTimeOutStamp() {
        return timeOutStamp;
    }

    public void setTimeOutStamp(long timeOutStamp) {
        this.timeOutStamp = timeOutStamp;
    }

    @Override
    public String toString() {
        return "{moduleTag:" + moduleTag + ",accountId:" + accountId +
                ",accountType:" + accountType + ",userName:" + userName +
                ",trackType:" + trackType + ",title:" + title +
                ",curClass:" + curClass + ",preClass:" + preClass + ",buttonName:" + buttonName +
                ",ip:" + ip + ",timeInStamp:" + timeInStamp +
                ",timeOutStamp:" + timeOutStamp + "}";
    }
}
