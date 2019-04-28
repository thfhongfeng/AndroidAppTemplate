package com.pine.base.request.database;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.base.request.database.callback.DbJsonCallback;
import com.pine.base.request.database.interceptor.IDbRequestInterceptor;
import com.pine.base.request.database.interceptor.IDbResponseInterceptor;
import com.pine.router.command.RouterDbCommand;
import com.pine.router.impl.IRouterManager;
import com.pine.router.impl.RouterManager;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbRequestManager {
    public final static String TAG = LogUtils.makeLogTag(DbRequestManager.class);
    private static Context mApplicationContext;
    private static IRouterManager mRequestManager;
    private static HashMap<String, HashMap<String, String>> mRequestHeader = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> mResponseHeader = new HashMap<>();

    private static List<IDbRequestInterceptor> mRequestInterceptorList = new ArrayList<>();

    private static List<IDbResponseInterceptor> mResponseInterceptorList = new ArrayList<>();

    public static void init(Context context) {
        mApplicationContext = context;
        mRequestManager = RouterManager.getDbServerRouter();
    }

    public static void addGlobalRequestInterceptor(IDbRequestInterceptor interceptor) {
        if (!mRequestInterceptorList.contains(interceptor)) {
            mRequestInterceptorList.add(interceptor);
            LogUtils.releaseLog(TAG, "Global request interceptor: " + interceptor.getClass() + " was added");
        }
    }

    public static void addGlobalResponseInterceptor(IDbResponseInterceptor interceptor) {
        if (!mResponseInterceptorList.contains(interceptor)) {
            mResponseInterceptorList.add(interceptor);
            LogUtils.releaseLog(TAG, "Global response interceptor: " + interceptor.getClass() + " was added");
        }
    }

    public static boolean setJsonRequest(int command, Map<String, String> params,
                                         String moduleTag, int what, DbJsonCallback callback) {
        return setJsonRequest(command, params, moduleTag, what, false, callback);
    }

    public static boolean setJsonRequest(int command, Map<String, String> params, String moduleTag,
                                         int what, boolean needLogin, DbJsonCallback callback) {
        //设置模块名
        if (!TextUtils.isEmpty(moduleTag)) {
            callback.setModuleTag(moduleTag);
        }
        callback.setCommand(command);
        callback.setWhat(what);

        DbRequestBean requestBean = new DbRequestBean(what);
        requestBean.setCommand(command);
        requestBean.setParams(params);
        requestBean.setModuleTag(moduleTag);
        requestBean.setWhat(what);
        if (!TextUtils.isEmpty(moduleTag)) {
            requestBean.setModuleTag(moduleTag);
        }
        requestBean.setNeedLogin(needLogin);
        requestBean.setCallback(callback);
        return setJsonRequest(requestBean);
    }

    public static boolean setJsonRequest(DbRequestBean requestBean, IDbRequestManager.ActionType actionType) {
        requestBean.setActionType(actionType);
        return setJsonRequest(requestBean);
    }

    public static boolean setJsonRequest(DbRequestBean requestBean) {
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(requestBean.getWhat(), requestBean)) {
                    requestBean.getCallback().onCancel(requestBean.getWhat());
                    return false;
                }
            }
        }
        LogUtils.d(TAG, "Request db - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- command: " +
                requestBean.getCommand() + "\r\n- params:" + requestBean.getParams());
        Bundle bundle = new Bundle();
        bundle.putSerializable("requestBean", requestBean);
        bundle.putSerializable("requestHeader", mRequestHeader);
        DbResponse response = mRequestManager.callDataCommandDirect(mApplicationContext,
                RouterDbCommand.callDbServerCommand, bundle);
        if (response == null) {
            requestBean.getCallback().onFail(requestBean.getWhat(), new Exception("remote error"));
            return true;
        }
        mResponseHeader = response.getResponseHeader();
        requestBean.setResponse(response);
        if (response.isSucceed()) {
            LogUtils.d(TAG, "Response db onSucceed - " + requestBean.getModuleTag() +
                    "(what:" + requestBean.getWhat() + ")" + "\r\n- command: " + requestBean.getCommand() +
                    "\r\n- response: " + response.getData());
            if (mResponseInterceptorList != null) {
                for (int i = 0; i < mResponseInterceptorList.size(); i++) {
                    if (mResponseInterceptorList.get(i).onIntercept(requestBean.getWhat(), requestBean, response)) {
                        return true;
                    }
                }
            }
            requestBean.getCallback().onResponse(requestBean.getWhat(), response);
        } else {
            LogUtils.d(TAG, "Response db onFailed - " + requestBean.getModuleTag() +
                    "(what:" + requestBean.getWhat() + ")" + "\r\n- command: " + requestBean.getCommand() +
                    "\r\n- response: " + response.getData());
            requestBean.getCallback().onFail(requestBean.getWhat(), response.getException());
        }
        return true;
    }
}
