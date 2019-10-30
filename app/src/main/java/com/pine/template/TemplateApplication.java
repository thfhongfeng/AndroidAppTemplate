package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.pine.base.BaseApplication;
import com.pine.base.access.UiAccessLoginExecutor;
import com.pine.base.access.UiAccessType;
import com.pine.base.access.UiAccessVipLevelExecutor;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.component.share.manager.ShareManager;
import com.pine.base.router.command.RouterDbServerCommand;
import com.pine.config.BuildConfig;
import com.pine.config.ConfigKey;
import com.pine.db_server.DbServerApplication;
import com.pine.login.LoginApplication;
import com.pine.main.MainApplication;
import com.pine.mvc.MvcApplication;
import com.pine.mvp.MvpApplication;
import com.pine.mvvm.MvvmApplication;
import com.pine.router.IRouterManager;
import com.pine.router.IRouterManagerFactory;
import com.pine.router.RouterManager;
import com.pine.router.impl.arouter.manager.ARouterManager;
import com.pine.tool.access.UiAccessManager;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IRequestManagerFactory;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.impl.database.DbRequestManager;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.request.impl.database.IDbRequestServer;
import com.pine.tool.request.impl.http.nohttp.NoRequestManager;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.user.UserApplication;
import com.pine.welcome.WelcomeApplication;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/7/3.
 */

public class TemplateApplication extends Application {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static Application mApplication;

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
        super.onCreate();
        mApplication = this;

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        LogUtils.setDebuggable(AppUtils.isApkDebuggable(this));

        BaseApplication.init(this);
        initManager();

        WelcomeApplication.attach();
        LoginApplication.attach();
        MainApplication.attach();
        UserApplication.attach();
        MvcApplication.attach();
        MvpApplication.attach();
        MvvmApplication.attach();
        DbServerApplication.attach();
    }

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Context getContext() {
        return mApplication.getApplicationContext();
    }

    private void initManager() {
        RouterManager.init("com.pine.base.router.command", new IRouterManagerFactory() {
            @Override
            public IRouterManager makeRouterManager(String bundleKey) {
                switch (BuildConfig.APP_THIRD_ROUTER_PROVIDER) {
                    case "arouter":
                        return ARouterManager.getInstance(bundleKey);
                    default:
                        return ARouterManager.getInstance(bundleKey);
                }
            }
        });

        ShareManager.getInstance().init(this);

        RequestManager.init(this, new IRequestManagerFactory() {
            @Override
            public IRequestManager makeRequestManager(Context context, HashMap<String, String> header) {
                switch (com.pine.config.BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
                    case "local":
                        return DbRequestManager.getInstance().init(context, header, new IDbRequestServer() {
                            @Override
                            public DbResponse request(Bundle bundle) {
                                return RouterManager.getInstance(ConfigKey.BUNDLE_DB_SEVER_KEY).callDataCommandDirect(mApplication,
                                        RouterDbServerCommand.callDbServerCommand, bundle);
                            }
                        });
                    default:
                        switch (BuildConfig.APP_THIRD_HTTP_REQUEST_PROVIDER) {
                            case "nohttp":
                                return NoRequestManager.getInstance().init(context, header);
                            default:
                                return NoRequestManager.getInstance().init(context, header);
                        }
                }
            }
        });

        MapSdkManager.getInstance().init(this);

        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.LOGIN,
                new UiAccessLoginExecutor());

        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.VIP_LEVEL,
                new UiAccessVipLevelExecutor());
    }
}
