package com.pine.welcome.bean;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class BundleSwitcherEntity {

    /**
     * configKey : bundle_login
     * open : true
     */

    private String configKey;
    // 是否开放：0-关闭；1-开放
    private int open;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
