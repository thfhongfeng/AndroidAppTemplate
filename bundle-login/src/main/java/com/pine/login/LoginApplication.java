package com.pine.login;

import android.app.Application;

import com.pine.config.BuildConfig;
import com.pine.login.model.local.LoginDbHelper;
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
                new LoginDbHelper(mApplication).getReadableDatabase();
                break;
            default:
                break;
        }
    }
}
