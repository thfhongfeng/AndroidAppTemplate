package com.pine.template.base.config.bean;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherEntity {
    // 配置类型（配置项来源）：本地初始配置
    public static final int CONFIG_TYPE_LOCAL_INIT = 0;
    // 配置类型（配置项来源）：本地用户配置（用户做了修改）
    public static final int CONFIG_TYPE_LOCAL_USER = 1;
    // 配置类型（配置项来源）：远程配置（远程获取的配置）
    public static final int CONFIG_TYPE_REMOTE = 2;

    /**
     * key : bundle_login
     * value : true
     * name : true
     * configType : 0
     * force : false
     */

    // 配置key
    private String key;
    // 配置key的值(不同配置对应数据结构不一样)
    private String value;
    // 配置name
    private String name;
    // 配置类型：0-本地初始配置；1-本地用户配置；2-在线配置
    private int configType;
    // 进行线上配置更新时，是否强制更新为线上配置
    private boolean force;

    public ConfigSwitcherEntity(@NonNull String key, @NonNull String value, int configType) {
        this.key = key;
        this.value = value;
        this.configType = configType;
    }

    public ConfigSwitcherEntity(@NonNull String key, @NonNull String value, String name, int configType) {
        this.key = key;
        this.value = value;
        this.name = name;
        this.configType = configType;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isEnable() {
        return "true".equalsIgnoreCase(value);
    }

    public ConfigSwitcherEntity clone() {
        ConfigSwitcherEntity entity = new ConfigSwitcherEntity(key, value, name, configType);
        entity.force = force;
        return entity;
    }
}
