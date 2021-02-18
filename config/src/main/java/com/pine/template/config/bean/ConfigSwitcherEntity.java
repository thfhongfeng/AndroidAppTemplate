package com.pine.template.config.bean;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherEntity {

    /**
     * configKey : bundle_login
     * open : true
     */

    private String configKey;
    // 是否开放：0-关闭；1-开放
    private int state;
    // 配置类型:0-缺省；1-模块开关；2-功能开关
    private int configType;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
