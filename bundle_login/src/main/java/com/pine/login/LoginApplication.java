package com.pine.login;

import android.app.Application;

import com.pine.base.request.RequestManager;
import com.pine.config.BuildConfig;
import com.pine.login.model.interceptor.LoginResponseInterceptor;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class LoginApplication {
    private final static String TAG = LogUtils.makeLogTag(LoginApplication.class);
    public static Application mApplication;

    public static void init(Application application) {
        mApplication = application;

        switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
            case "local":
                break;
            default:
                RequestManager.addGlobalResponseInterceptor(new LoginResponseInterceptor());
                break;
        }
    }
}
