package com.pine.base.request.impl.database;

import com.pine.base.request.IRequestManager;

import java.io.Serializable;
import java.util.Map;

public class DbRequestBean implements Serializable {
    // 该callback对应的http请求的key
    private String key;
    private String url;
    private Map<String, String> params;
    //模块标识，默认common
    private String moduleTag = "common";
    private int what;
    private boolean needLogin;

    private IRequestManager.ActionType actionType = IRequestManager.ActionType.COMMON;

    public DbRequestBean(int what) {
        this.what = what;
        this.key = hashCode() + "_ " + what;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public IRequestManager.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(IRequestManager.ActionType actionType) {
        this.actionType = actionType;
    }
}
