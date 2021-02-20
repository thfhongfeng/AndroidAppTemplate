package com.pine.template.config.bean;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherEntity {

    /**
     * configKey : bundle_login
     * open : true
     */

    // 配置key
    private String configKey;
    // 配置key的父配置key(只有父配置都开放的情况下，本配置才开放)
    private String parentConfigKey;
    // 是否开放：0-关闭；1-开放
    private int state;
    // 配置类型:0-缺省；1-模块开关；2-功能开关；3-配置开关
    private int configType;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getParentConfigKey() {
        return parentConfigKey;
    }

    public void setParentConfigKey(String parentConfigKey) {
        this.parentConfigKey = parentConfigKey;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
