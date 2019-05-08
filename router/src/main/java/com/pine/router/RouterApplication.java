package com.pine.router;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public class RouterApplication {
    private final static String TAG = LogUtils.makeLogTag(RouterApplication.class);
    public static Application mApplication;

    public static void attach(Application application) {
        mApplication = application;

        initRouter();
    }

    private static void initRouter() {
        if (AppUtils.isApkDebuggable(mApplication)) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(mApplication);
    }
}
