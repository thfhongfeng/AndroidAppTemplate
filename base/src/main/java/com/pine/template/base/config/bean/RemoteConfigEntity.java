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

    public static final int TYPE_INPUT = 0;
    public static final int TYPE_SELECTOR_SINGLE = 1;
    public static final int TYPE_SELECTOR_MULTI = 2;

    // 配置名称
    private String configName;
    // 配置key
    private String configKey;
    // 配置类型：0-文本项；1-单选项；2-多选项
    private int type;
    // 配置值：多选项情况下以逗号分割
    private String configValue;
    // 配置项描述说明
    private String configHint;
    // 进行线上配置更新时，是否强制更新为线上配置:0-不强制；1-强制
    // 不强制情况下：只要用户本地修改过配置，则不会更新，否则还是会更新。
    private int forceUpdate;
    // type为文本项时值是否可以为空
    private boolean canEmpty;
    // type为单选项或者多选项时生效，标识可选项的显示文本，以逗号分割
    private String optionName;
    // type为单选项或者多选项时生效，标识可选项的值，以逗号分割
    private String optionValue;

    public RemoteConfigEntity(int type, @NonNull String key, @NonNull String value) {
        this.type = type;
        this.configKey = key;
        this.configValue = value;
    }

    public RemoteConfigEntity(int type, @NonNull String key, @NonNull String value, String name) {
        this.type = type;
        this.configKey = key;
        this.configValue = value;
        this.configName = name;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigHint() {
        return configHint;
    }

    public void setConfigHint(String configHint) {
        this.configHint = configHint;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isCanEmpty() {
        return canEmpty;
    }

    public void setCanEmpty(boolean canEmpty) {
        this.canEmpty = canEmpty;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public ConfigSwitcherEntity toConfigSwitcherEntity() {
        ConfigSwitcherEntity entity = new ConfigSwitcherEntity(configKey, configValue, configName, 2);
        entity.setForce(forceUpdate == 1);
        return entity;
    }

    public static RemoteConfigEntity buildInputEntity(@NonNull String key, @NonNull String value,
                                                      @NonNull String name, boolean canEmpty) {
        RemoteConfigEntity entity = new RemoteConfigEntity(TYPE_INPUT, key, value, name);
        entity.setCanEmpty(canEmpty);
        return entity;
    }

    public static RemoteConfigEntity buildSingleSelectorEntity(@NonNull String key, @NonNull String value,
                                                               @NonNull String name, @NonNull String optionValue,
                                                               @NonNull String optionName) {
        RemoteConfigEntity entity = new RemoteConfigEntity(TYPE_SELECTOR_SINGLE, key, value, name);
        entity.setOptionName(optionName);
        entity.setOptionValue(optionValue);
        return entity;
    }

    public static RemoteConfigEntity buildMultiSelectorEntity(@NonNull String key, @NonNull String value,
                                                              @NonNull String name, @NonNull String optionValue,
                                                              @NonNull String optionName) {
        RemoteConfigEntity entity = new RemoteConfigEntity(TYPE_SELECTOR_MULTI, key, value, name);
        entity.setOptionName(optionName);
        entity.setOptionValue(optionValue);
        return entity;
    }

    @Override
    public String toString() {
        return "RemoteConfigEntity{" +
                "configName='" + configName + '\'' +
                ", configKey='" + configKey + '\'' +
                ", type=" + type +
                ", configValue='" + configValue + '\'' +
                ", configHint='" + configHint + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", canEmpty=" + canEmpty +
                ", optionName='" + optionName + '\'' +
                ", optionValue='" + optionValue + '\'' +
                '}';
    }
}