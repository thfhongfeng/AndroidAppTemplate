package com.pine.template.welcome;

import com.pine.template.base.bg.AppBgManager;
import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class WelcomeApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(WelcomeApplication.class);

    public static void onCreate() {

    }

    public static void attach() {
        AppBgManager.startBgService(mApplication);
    }
}
