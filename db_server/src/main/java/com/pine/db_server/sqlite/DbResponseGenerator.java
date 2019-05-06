package com.pine.db_server.sqlite;

import android.text.TextUtils;

import com.pine.base.request.impl.database.DbRequestBean;
import com.pine.base.request.impl.database.DbResponse;

import java.util.HashMap;

public class DbResponseGenerator {

    public static DbResponse getSuccessRep(DbRequestBean requestBean,
                                           HashMap<String, String> cookies,
                                           String data) {
        String dataContainer;
        if (!TextUtils.isEmpty(data)) {
            dataContainer = "{'success':true, 'code':200, 'message':'','data':" + data + "}";
        } else {
            dataContainer = "{'success':true, 'code':200, 'message':''}";
        }
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getLoginFailRep(DbRequestBean requestBean,
                                             HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':401, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getNoSuchTableRep(DbRequestBean requestBean,
                                               HashMap<String, String> cookies) {
        String dataContainer = "{'success':false, 'code':2, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new Exception("No table"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getBadArgsRep(DbRequestBean requestBean,
                                           HashMap<String, String> cookies) {
        String dataContainer = "{'success':false, 'code':1001, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new Exception("Bad args"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getExceptionRep(DbRequestBean requestBean,
                                             HashMap<String, String> cookies, Exception e) {
        String dataContainer = "{'success':false, 'code':1002, 'message':''}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(e);
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }
}
