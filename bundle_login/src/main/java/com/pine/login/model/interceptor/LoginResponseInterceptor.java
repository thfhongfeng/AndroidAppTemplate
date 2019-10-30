package com.pine.login.model.interceptor;

import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.ResponseCode;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.callback.LoginCallback;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestExtraSwitcher;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.request.interceptor.IResponseInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginResponseInterceptor implements IResponseInterceptor {
    private final int MAX_PER_RE_LOGIN_COUNT = 3;
    private final int MAX_TOTAL_RE_LOGIN_COUNT = 50;
    private Map<String, RequestBean> mNoAuthRequestMap = new HashMap<String, RequestBean>();
    private int mPerReLoginCount = 0;
    private int mTotalReLoginCount = 0;
    private volatile boolean mIsReLoginProcessing = false;

    @Override
    public boolean onIntercept(int what, RequestBean requestBean, Response response) {
        if (requestBean.getCallback() instanceof LoginCallback) {
            mIsReLoginProcessing = false;
            if (!response.isSucceed() && what == LoginCallback.RE_LOGIN_CODE) {
                LoginApplication.setLogin(false);
                if (!tryToSendReLogin()) { // 发出自动登录失败
                    flushAllNoAuthRequest();
                    mNoAuthRequestMap.clear();
                    return false;
                }
                return true;
            } else if (what == LoginCallback.RE_LOGIN_CODE) {
                String res = (String) response.getData();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if (jsonObject == null || !jsonObject.optBoolean(LoginConstants.SUCCESS, false)) {
                        mPerReLoginCount = 0;
                        reloadAllNoAuthRequest();
                    }
                } catch (JSONException e) {
                }
            }
        } else if (requestBean.getRequestType() == IRequestManager.RequestType.STRING &&
                requestBean.isExtraSwitcherOpen(RequestExtraSwitcher.RELOAD_FOR_NO_AUTH_WHEN_RE_LOGIN)) {
            if (response.getResponseCode() == ResponseCode.NOT_LOGIN) { // 拦截401错误
                if (mNoAuthRequestMap != null &&
                        !mNoAuthRequestMap.containsKey(requestBean.getKey())) {
                    mNoAuthRequestMap.put(requestBean.getKey(), requestBean);
                }
                if (!mIsReLoginProcessing) {
                    LoginApplication.setLogin(false);
                    if (!tryToSendReLogin()) { // 发出自动登录失败
                        flushAllNoAuthRequest();
                        mNoAuthRequestMap.clear();
                    }
                }
                return true;
            }
        }
        if (IRequestManager.ActionType.RETRY_AFTER_RE_LOGIN == requestBean.getActionType()) {
            if (mNoAuthRequestMap != null &&
                    mNoAuthRequestMap.containsKey(requestBean.getKey())) {
                mNoAuthRequestMap.remove(requestBean);
            }
        }
        return false;
    }

    private boolean tryToSendReLogin() {
        if (mPerReLoginCount >= MAX_PER_RE_LOGIN_COUNT ||
                mTotalReLoginCount >= MAX_TOTAL_RE_LOGIN_COUNT) {
            return false;
        }
        if (LoginManager.reLogin()) {
            mPerReLoginCount++;
            mTotalReLoginCount++;
            mIsReLoginProcessing = true;
            return true;
        }
        return false;
    }

    // 重新发起之前因401终止的指定的key的请求
    public void reloadNoAuthRequest(String key) {
        if (mNoAuthRequestMap == null) {
            return;
        }
        RequestBean bean = mNoAuthRequestMap.get(key);
        if (bean == null || bean.getRequestType() != IRequestManager.RequestType.STRING) {
            return;
        }
        bean.setActionType(IRequestManager.ActionType.RETRY_AFTER_RE_LOGIN);
        RequestManager.setJsonRequest(bean, (JsonCallback) bean.getCallback());
    }

    // 重新发起之前所有因401终止的请求
    public void reloadAllNoAuthRequest() {
        if (mNoAuthRequestMap == null) {
            return;
        }
        Iterator<String> iterator = mNoAuthRequestMap.keySet().iterator();
        while (iterator.hasNext()) {
            reloadNoAuthRequest(iterator.next());
        }
    }

    public void flushNoAuthRequest(String key) {
        if (mNoAuthRequestMap == null) {
            return;
        }
        RequestBean bean = mNoAuthRequestMap.get(key);
        if (bean == null || bean.getCallback() == null || bean.getRequestType() != IRequestManager.RequestType.STRING) {
            return;
        }
        if (bean.getResponse().isSucceed()) {
            ((JsonCallback) bean.getCallback()).onResponse(bean.getWhat(), bean.getResponse());
        } else {
            ((JsonCallback) bean.getCallback()).onFail(bean.getWhat(), bean.getResponse().getException(), bean.getResponse());
        }
    }

    public void flushAllNoAuthRequest() {
        if (mNoAuthRequestMap == null) {
            return;
        }
        Iterator<String> iterator = mNoAuthRequestMap.keySet().iterator();
        while (iterator.hasNext()) {
            flushNoAuthRequest(iterator.next());
        }
    }
}
