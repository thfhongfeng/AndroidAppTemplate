package com.pine.base.request.database;

import android.text.TextUtils;

import java.util.HashMap;

public class DbResponseGenerator {

    public static DbResponse getSuccessRep(DbRequestBean requestBean,
                                           HashMap<String, HashMap<String, String>> header,
                                           String data) {
        String dataContainer;
        if (!TextUtils.isEmpty(data)) {
            dataContainer = "{'success':true, 'code':200, 'message':'','data':" + data + "}";
        } else {
            dataContainer = "{'success':true, 'code':200, 'message':''}";
        }
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setResponseHeader(header);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getLoginFailRep(DbRequestBean requestBean,
                                             HashMap<String, HashMap<String, String>> header, String message) {
        String dataContainer = "{'success':false, 'code':401, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setResponseHeader(header);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getNoSuchTableRep(DbRequestBean requestBean,
                                               HashMap<String, HashMap<String, String>> header) {
        String dataContainer = "{'success':false, 'code':2, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setResponseHeader(header);
        response.setException(new Exception("No table"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getBadArgsRep(DbRequestBean requestBean,
                                           HashMap<String, HashMap<String, String>> header) {
        String dataContainer = "{'success':false, 'code':1001, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setResponseHeader(header);
        response.setException(new Exception("Bad args"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getExceptionRep(DbRequestBean requestBean,
                                             HashMap<String, HashMap<String, String>> header, Exception e) {
        String dataContainer = "{'success':false, 'code':1002, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setResponseHeader(header);
        response.setException(e);
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }
}
