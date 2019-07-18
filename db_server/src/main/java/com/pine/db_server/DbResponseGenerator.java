package com.pine.db_server;

import android.text.TextUtils;

import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import java.util.HashMap;

public class DbResponseGenerator {

    public static DbResponse getSuccessJsonRep(DbRequestBean requestBean,
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

    public static DbResponse getLoginFailJsonRep(DbRequestBean requestBean,
                                                 HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':401, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getServerDbOpFailJsonRep(DbRequestBean requestBean,
                                                      HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':501, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getExistAccountJsonRep(DbRequestBean requestBean,
                                                    HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':601, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        return response;
    }

    public static DbResponse getNoSuchTableJsonRep(DbRequestBean requestBean,
                                                   HashMap<String, String> cookies) {
        String dataContainer = "{'success':false, 'code':602, 'message':'No table'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new Exception("No table"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getBadArgsJsonRep(DbRequestBean requestBean,
                                               HashMap<String, String> cookies) {
        String dataContainer = "{'success':false, 'code':603, 'message':'Bad args'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new Exception("Bad args"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }

    public static DbResponse getExceptionJsonRep(DbRequestBean requestBean,
                                                 HashMap<String, String> cookies, Exception e) {
        String dataContainer = "{'success':false, 'code':604, 'message':'" + e.toString() + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(e);
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        return response;
    }
}
