package com.pine.base.request.database;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class DbResponse {
    private int responseCode;
    private boolean succeed;
    private Object tag;
    private Object data;
    private HashMap<String, HashMap<String, String>> responseHeader;
    private Exception exception;

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

    public HashMap<String, HashMap<String, String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(HashMap<String, HashMap<String, String>> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
