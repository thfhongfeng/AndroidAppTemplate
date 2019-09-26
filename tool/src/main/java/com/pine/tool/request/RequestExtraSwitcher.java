package com.pine.tool.request;

public enum RequestExtraSwitcher {
    // 请求响应ResponseCode.NOT_LOGIN(401)时，是否允许重新登陆并在重新登陆后重新发出这些请求
    RELOAD_FOR_NO_AUTH_WHEN_RE_LOGIN
}
