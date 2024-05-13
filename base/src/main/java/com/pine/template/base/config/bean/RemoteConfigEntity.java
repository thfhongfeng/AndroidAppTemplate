package com.pine.template.base.config.bean;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class RemoteConfigEntity {
    /**
     * configKey : bundle_login
     * configValue : true
     * configName : true
     * configType : 0
     * forceUpdate : 1
     */

    // 配置key
    private String configKey;
    // 配置key的值(不同配置对应数据结构不一样)
    private String configValue;
    // 配置name
    private String configName;
    // 配置类型：0-本地初始配置；1-本地用户配置；2-在线配置
    private int configType;
    // 进行线上配置更新时，是否强制更新为线上配置:0-不强制；1-强制
    private int forceUpdate;

    public RemoteConfigEntity(@NonNull String key, @NonNull String value, int configType) {
        this.configKey = key;
        this.configValue = value;
        this.configType = configType;
    }

    public RemoteConfigEntity(@NonNull String key, @NonNull String value, String name, int configType) {
        this.configKey = key;
        this.configValue = value;
        this.configName = name;
        this.configType = configType;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public ConfigSwitcherEntity toConfigSwitcherEntity() {
        ConfigSwitcherEntity entity = new ConfigSwitcherEntity(configKey, configValue, configName, configType);
        entity.setForce(forceUpdate == 1);
        return entity;
    }
}
