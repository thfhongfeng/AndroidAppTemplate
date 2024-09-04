package com.pine.template.base.config.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherInfo {
    private String version;
    // 0-所有；-1-访客；1-登录用户
    private int stateRange;

    private List<RemoteConfigEntity> configList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStateRange() {
        return stateRange;
    }

    public void setStateRange(int stateRange) {
        this.stateRange = stateRange;
    }

    public boolean isLoginState() {
        return stateRange == 1;
    }

    public boolean isGuestState() {
        return stateRange == -1;
    }

    public boolean isAllState() {
        return stateRange == 0;
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

    @Override
    public String toString() {
        return "ConfigSwitcherInfo{" +
                "version='" + version + '\'' +
                ", stateRange=" + stateRange +
                ", configList size=" + (configList == null ? 0 : configList.size()) +
                '}';
    }
}
