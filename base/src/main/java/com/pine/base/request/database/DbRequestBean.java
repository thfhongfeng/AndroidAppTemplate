package com.pine.base.request.database;

import com.pine.base.request.database.IDbRequestManager.ActionType;
import com.pine.base.request.database.callback.DbJsonCallback;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class DbRequestBean implements Serializable {
    // 该callback对应的http请求的key
    private String key;
    private Map<String, String> params;
    //模块标识，默认common
    private String moduleTag = "common";
    private int what;
    private boolean needLogin;
    private DbJsonCallback callback;

    private ActionType actionType = ActionType.COMMON;

    private int command = IDbCommand.DEFAULT;

    private DbResponse response;

    public DbRequestBean(int what) {
        this.what = what;
        this.key = hashCode() + "_ " + what;
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getParams() {
        return params;
    }

    protected void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    protected void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public int getWhat() {
        return what;
    }

    protected void setWhat(int what) {
        this.what = what;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    protected void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public DbJsonCallback getCallback() {
        return callback;
    }

    public void setCallback(DbJsonCallback callback) {
        this.callback = callback;
    }

    public ActionType getActionType() {
        return actionType;
    }

    protected void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public DbResponse getResponse() {
        return response;
    }

    public void setResponse(DbResponse response) {
        this.response = response;
    }
}
