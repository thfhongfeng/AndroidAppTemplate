package com.pine.template.base.config.bean;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherInfo {
    private String version;

    private List<ConfigSwitcherEntity> configList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ConfigSwitcherEntity> getConfigList() {
        return configList;
    }

    public void setConfigList(List<ConfigSwitcherEntity> configList) {
        this.configList = configList;
    }
}
