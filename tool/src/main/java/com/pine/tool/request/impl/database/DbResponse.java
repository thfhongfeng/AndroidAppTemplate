package com.pine.tool.request.impl.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class DbResponse implements Serializable {
    private int responseCode;
    private boolean succeed;
    private Object tag;
    private Object data;
    private Exception exception;
    private Map<String, List<String>> headers;
    private HashMap<String, String> cookies;

    // for upload
    private boolean multiUpload;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean isMultiUpload() {
        return multiUpload;
    }

    public void setMultiUpload(boolean multiUpload) {
        this.multiUpload = multiUpload;
    }
}
