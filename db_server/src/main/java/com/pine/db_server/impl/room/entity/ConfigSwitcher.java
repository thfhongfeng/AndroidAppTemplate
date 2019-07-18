package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_switcher_config")
public class ConfigSwitcher {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    // 账户类型:0-游客（临时账户），999999-超级管理员，其他(0-999之间)
    @NonNull
    private int accountType;

    @NonNull
    private String configKey;

    // 是否开放：0-关闭；1-开放
    @NonNull
    private int open;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    @NonNull
    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(@NonNull String configKey) {
        this.configKey = configKey;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
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
