package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.pine.template.base.BaseApplication;
import com.pine.template.base.BundleBaseApplication;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.template.login.LoginApplication;
import com.pine.template.main.MainApplication;
import com.pine.template.welcome.WelcomeApplication;
import com.pine.tool.router.RouterException;
import com.pine.tool.router.RouterManager;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/7/3.
 */

public class TemplateApplication extends Application {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static Application mApplication;

    @Override
    public void onCreate() {
        LogUtils.setDebugLevel(0);
        LogUtils.d(TAG, "onCreate");
        super.onCreate();
        mApplication = this;

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        LogUtils.d(TAG, "APP SHA1:" + AppUtils.SHA1(this));

        // 主进程初始化
        if (mApplication.getPackageName().equals(AppUtils.getCurProcessName(mApplication))) {
            BaseApplication.init(this);

            BundleBaseApplication.onCreate();
            WelcomeApplication.onCreate();
            LoginApplication.onCreate();
            MainApplication.onCreate();

            BundleBaseApplication.initManager();

            BundleBaseApplication.attach();
            WelcomeApplication.attach();
            LoginApplication.attach();
            MainApplication.attach();

            if (BuildConfig.BUILD_BIZ_BUNDLE != null) {
                for (String bizBundle : BuildConfig.BUILD_BIZ_BUNDLE) {
                    try {
                        RouterManager.callOpCommandDirect(mApplication, bizBundle,
                                "onAppCreate", null);
                    } catch (RouterException e) {
                        e.printStackTrace();
                    }
                }
                for (String bizBundle : BuildConfig.BUILD_BIZ_BUNDLE) {
                    try {
                        RouterManager.callOpCommandDirect(mApplication, bizBundle,
                                "onAppAttach", null);
                    } catch (RouterException e) {
                        e.printStackTrace();
                    }
                }
            }

            doStartupBusiness();
        }
    }

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }


    private void doStartupBusiness() {

    }
}
