package com.pine.tool.request;

import android.support.annotation.NonNull;

import com.pine.tool.request.IRequestManager.ActionType;
import com.pine.tool.request.IRequestManager.RequestType;
import com.pine.tool.request.callback.AbstractBaseCallback;

import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class RequestBean {
    // 该callback对应的请求的key
    private String key;
    // 请求URL
    private String url;
    // 请求方式：GET、POST等
    private RequestMethod requestMethod;
    // 请求参数
    private Map<String, String> params;
    // 模块标识，默认common
    private String moduleTag = "common";
    // 请求标识code
    private int what;
    // cancel标识
    private Object sign;
    // 该请求是否需要登陆
    private boolean needLogin;
    // 请求类型：JSON数据请求；下载请求；上传请求等
    private RequestType requestType;
    // 请求响应回调
    private AbstractBaseCallback callback;
    // 请求动作类别：一般请求动作；重登陆后的请求重发；请求返回错误后的请求重发等
    private ActionType actionType = ActionType.COMMON;
    // 请求响应体
    private Response response;

    // 该请求出现ResponseCode.NOT_LOGIN(401)时，是否允许在重新登陆后重新发出这些请求
    private boolean reloadForNoAuthWhenReLogin;

    public RequestBean(@NonNull String url, int what, Map<String, String> params) {
        this(url, what, params, false, null, RequestMethod.POST);
    }

    public RequestBean(@NonNull String url, int what, Map<String, String> params, boolean needLogin) {
        this(url, what, params, needLogin, null, RequestMethod.POST);
    }

    public RequestBean(@NonNull String url, int what, Map<String, String> params, boolean needLogin,
                       Object sign) {
        this(url, what, params, needLogin, sign, RequestMethod.POST);
    }

    public RequestBean(@NonNull String url, int what, Map<String, String> params, boolean needLogin,
                       Object sign, RequestMethod requestMethod) {
        this.url = url;
        this.what = what;
        this.params = params;
        this.needLogin = needLogin;
        this.sign = sign;
        this.requestMethod = requestMethod;
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

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
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

    public Object getSign() {
        return sign;
    }

    public void setSign(Object sign) {
        this.sign = sign;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    protected void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public AbstractBaseCallback getCallback() {
        return callback;
    }

    protected void setCallback(AbstractBaseCallback callback) {
        this.callback = callback;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Response getResponse() {
        return response;
    }

    protected void setResponse(Response response) {
        this.response = response;
    }

    public boolean isReloadForNoAuthWhenReLogin() {
        return reloadForNoAuthWhenReLogin;
    }

    public void setReloadForNoAuthWhenReLogin(boolean reloadForNoAuthWhenReLogin) {
        this.reloadForNoAuthWhenReLogin = reloadForNoAuthWhenReLogin;
    }
}
