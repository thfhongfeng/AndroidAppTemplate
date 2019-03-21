package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.pine.base.BaseApplication;
import com.pine.demo.DemoApplication;
import com.pine.login.LoginApplication;
import com.pine.main.MainApplication;
import com.pine.mvc.MvcApplication;
import com.pine.mvp.MvpApplication;
import com.pine.mvvm.MvvmApplication;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.user.UserApplication;
import com.pine.welcome.WelcomeApplication;

/**
 * Created by tanghongfeng on 2018/7/3.
 */

public class TemplateApplication extends Application {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    @Override
    public void onCreate() {
        super.onCreate();

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        LogUtils.setDebuggable(AppUtils.isApkDebuggable(this));

        BaseApplication.init(this);
        WelcomeApplication.init(this);
        LoginApplication.init(this);
        MainApplication.init(this);
        UserApplication.init(this);
        MvcApplication.init(this);
        MvpApplication.init(this);
        MvvmApplication.init(this);
        DemoApplication.init(this);
    }

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }
}
