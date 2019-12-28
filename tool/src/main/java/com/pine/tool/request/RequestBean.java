package com.pine.tool.request;

import com.pine.tool.request.IRequestManager.ActionType;
import com.pine.tool.request.IRequestManager.RequestType;
import com.pine.tool.request.callback.AbstractBaseCallback;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class RequestBean {
    // 该callback对应的请求的key
    private String key;
    // 请求URL
    private String url;
    // 请求标识code
    private int what;
    // 请求参数
    private Map<String, String> params;
    // 该请求是否需要登陆
    private boolean needLogin;
    // cancel标识
    private Object sign;
    // 请求方式：GET、POST等
    private RequestMethod requestMethod;
    // 请求优先级，默认100，数字越大，优先级越小
    private int priority = 100;

    // 请求的服务系统标识，默认main，代表应用本体后台服务请求。
    // 主要用于session的管理（不同的后台服务系统应该维护不同的session，不能混在一起）。
    // session的管理与该参数绑定。当请求的是域外的系统服务时，才设置该参数，表示这个请求不属于本体服务请求。
    private String sysTag = "main";
    // 模块标识，默认Pine_Default
    private String moduleTag = "Pine_Default";
    // 请求额外的需要添加的头部信息
    private HashMap<String, String> headerParam = new HashMap<>();
    // 请求动作类别：一般请求动作；重登陆后的请求重发；请求返回错误后的请求重发等
    private ActionType actionType = ActionType.COMMON;

    // 请求响应回调
    private AbstractBaseCallback callback;
    // 请求类型：JSON数据请求；下载请求；上传请求等
    private RequestType requestType;
    // 请求响应体
    private Response response;

    // 请求的额外的开关标识，记录一些额外数据，用于请求的特殊处理
    // 比如其中一个应用：
    // 请求响应ResponseCode.NOT_LOGIN(401)时（RequestExtraSwitcher.RELOAD_FOR_NO_AUTH_WHEN_RE_LOGIN），是否允许重新登陆并在重新登陆后重新发出这些请求
    private HashMap<RequestExtraSwitcher, Boolean> extraSwitcherMap = new HashMap<>();

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

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public Object getSign() {
        return sign;
    }

    public void setSign(Object sign) {
        this.sign = sign;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSysTag() {
        return sysTag;
    }

    public void setSysTag(String sysTag) {
        this.sysTag = sysTag;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public HashMap<String, String> getHeaderParam() {
        return headerParam;
    }

    public void setHeaderParam(HashMap<String, String> headerParam) {
        this.headerParam = headerParam;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public AbstractBaseCallback getCallback() {
        return callback;
    }

    protected void setCallback(AbstractBaseCallback callback) {
        this.callback = callback;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    protected void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Response getResponse() {
        return response;
    }

    protected void setResponse(Response response) {
        this.response = response;
    }

    public void openExtraSwitcher(RequestExtraSwitcher key) {
        extraSwitcherMap.put(key, true);
    }

    public void closeExtraSwitcher(RequestExtraSwitcher key) {
        if (extraSwitcherMap.containsKey(key)) {
            extraSwitcherMap.remove(key);
        }
    }

    public boolean isExtraSwitcherOpen(RequestExtraSwitcher key) {
        return extraSwitcherMap.containsKey(key) && extraSwitcherMap.get(key);
    }
}
