package com.pine.base.request.impl.database;

import android.content.Context;
import android.os.Bundle;

import com.pine.base.request.IRequestManager;
import com.pine.base.request.IResponseListener;
import com.pine.base.request.RequestBean;
import com.pine.base.request.Response;
import com.pine.router.command.RouterDbServerCommand;
import com.pine.router.impl.IRouterManager;
import com.pine.router.impl.RouterManager;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class DbRequestManager implements IRequestManager {
    private final static String TAG = LogUtils.makeLogTag(DbRequestManager.class);
    private static volatile DbRequestManager mInstance;
    private static Context mApplicationContext;
    private static HashMap<String, String> mHeaderParams = new HashMap<>();
    private static HashMap<String, String> mCookies = new HashMap<>();
    private static IRouterManager mRequestManager;
    private String mSessionId;

    private DbRequestManager() {

    }

    public static DbRequestManager getInstance() {
        if (mInstance == null) {
            synchronized (DbRequestManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use http request: nohttp");
                    mInstance = new DbRequestManager();
                }
            }
        }
        return mInstance;
    }


    @Override
    public IRequestManager init(Context context, HashMap<String, String> head) {
        mApplicationContext = context;
        if (head != null) {
            mHeaderParams = head;
        }
        mRequestManager = RouterManager.getDbServerRouter();
        return this;
    }

    @Override
    public void setJsonRequest(RequestBean requestBean, IResponseListener.OnResponseListener listener) {
        DbRequestBean dbRequestBean = new DbRequestBean(requestBean.getWhat());
        dbRequestBean.setUrl(requestBean.getUrl());
        dbRequestBean.setParams(requestBean.getParams());
        dbRequestBean.setModuleTag(requestBean.getModuleTag());
        dbRequestBean.setNeedLogin(requestBean.isNeedLogin());

        Bundle bundle = new Bundle();
        bundle.putSerializable("requestBean", dbRequestBean);
        bundle.putSerializable("cookies", mCookies);
        listener.onStart(requestBean.getWhat());
        DbResponse dbResponse = mRequestManager.callDataCommandDirect(mApplicationContext,
                RouterDbServerCommand.callDbServerCommand, bundle);
        if (dbResponse == null) {
            Response failRsp = new Response();
            failRsp.setException(new Exception("remote error"));
            listener.onFailed(requestBean.getWhat(), failRsp);
            return;
        }

        Response response = new Response();
        response.setSucceed(dbResponse.isSucceed());
        response.setTag(dbResponse.getTag());
        response.setResponseCode(dbResponse.getResponseCode());
        response.setData(dbResponse.getData());
        response.setCookies(dbResponse.getCookies());
        response.setException(dbResponse.getException());

        mCookies = dbResponse.getCookies();
        if (response.isSucceed()) {
            listener.onSucceed(requestBean.getWhat(), response);
        } else {
            listener.onFailed(requestBean.getWhat(), response);
        }
        listener.onFinish(requestBean.getWhat());
    }

    @Override
    public void setDownloadRequest(RequestBean requestBean, IResponseListener.OnDownloadListener listener) {

    }

    @Override
    public void setUploadRequest(RequestBean requestBean, IResponseListener.OnUploadListener processListener,
                                 IResponseListener.OnResponseListener responseListener) {

    }

    @Override
    public void cancelBySign(Object sign) {

    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void addGlobalSessionCookie(HashMap<String, String> headerMap) {
        if (headerMap == null) {
            return;
        }
        mHeaderParams.putAll(headerMap);
    }

    @Override
    public void removeGlobalSessionCookie(List<String> keyList) {
        if (keyList == null || keyList.size() < 1) {
            return;
        }
        for (String key : keyList) {
            mHeaderParams.remove(key);
        }
    }

    @Override
    public String getSessionId() {
        return mSessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    @Override
    public void clearCookie() {
        mCookies = new HashMap<>();
    }

    @Override
    public Map<String, String> getSessionCookie() {
        return mCookies;
    }
}
