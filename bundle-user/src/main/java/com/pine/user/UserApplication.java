package com.pine.user;

import android.app.Application;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class UserApplication {
    private final static String TAG = LogUtils.makeLogTag(UserApplication.class);
    public static Application mApplication;

    public static void init(Application application) {
        mApplication = application;
    }
}
