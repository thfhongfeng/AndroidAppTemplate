package com.pine.template.base.config.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherInfo {
    private String version;

    private List<RemoteConfigEntity> configList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ConfigSwitcherEntity> getConfigList() {
        if (configList == null) {
            return null;
        }
        List<ConfigSwitcherEntity> list = new ArrayList<>();
        for (RemoteConfigEntity entity : configList) {
            list.add(entity.toConfigSwitcherEntity());
        }
        return list;
    }

    public void setConfigList(List<RemoteConfigEntity> configList) {
        this.configList = configList;
    }
}
