package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "db_account")
public class Account implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    @ColumnInfo(name = "id")
    private String accountId;

    @NonNull
    private String account;

    // 账户类型:0-游客（临时账户），999999-超级管理员，其他(0-999之间)
    @NonNull
    private int accountType;

    @NonNull
    private String name;

    @NonNull
    private String password;

    private String headImgUrl;

    // 账户状态:0-删除，1-激活，2-未激活
    @NonNull
    private int state;

    @NonNull
    private String mobile;

    // 0表示当前非登陆状态
    private long curLoginTimeStamp;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(@NonNull String accountId) {
        this.accountId = accountId;
    }

    @NonNull
    public String getAccount() {
        return account;
    }

    public void setAccount(@NonNull String account) {
        this.account = account;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @NonNull
    public String getMobile() {
        return mobile;
    }

    public void setMobile(@NonNull String mobile) {
        this.mobile = mobile;
    }

    public long getCurLoginTimeStamp() {
        return curLoginTimeStamp;
    }

    public void setCurLoginTimeStamp(long curLoginTimeStamp) {
        this.curLoginTimeStamp = curLoginTimeStamp;
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
