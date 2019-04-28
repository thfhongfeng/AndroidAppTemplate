package com.pine.welcome.bean;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class BundleSwitcherEntity {

    /**
     * configKey : login_bundle
     * open : true
     */

    private String configKey;
    private boolean open;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
