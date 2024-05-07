package com.pine.template.login;

import com.pine.template.bundle_base.BuildConfig;
import com.pine.template.login.model.interceptor.LoginResponseInterceptor;
import com.pine.tool.RootApplication;
import com.pine.tool.request.RequestManager;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class LoginApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(LoginApplication.class);

    public static void onCreate() {

    }

    public static void attach() {
        switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
            case "local":
                break;
            default:
                RequestManager.addGlobalResponseInterceptor(new LoginResponseInterceptor());
                break;
        }
    }
}
