package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.template.base.BaseApplication;
import com.pine.template.base.business.access.UiAccessConfigSwitcherExecutor;
import com.pine.template.base.business.access.UiAccessLoginExecutor;
import com.pine.template.base.business.access.UiAccessType;
import com.pine.template.base.component.scan.IScanManager;
import com.pine.template.base.component.scan.IScanManagerFactory;
import com.pine.template.base.component.scan.ScanManager;
import com.pine.template.base.component.scan.zxing.ZXingScanManager;
import com.pine.template.config.BuildConfig;
import com.pine.template.config.ConfigApplication;
import com.pine.template.config.helper.DeviceInfoHelper;
import com.pine.template.config.router.command.RouterDbServerCommand;
import com.pine.template.config.switcher.ConfigSwitcherServer;
import com.pine.template.login.LoginApplication;
import com.pine.template.main.MainApplication;
import com.pine.template.welcome.WelcomeApplication;
import com.pine.tool.access.UiAccessManager;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IRequestManagerFactory;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.impl.database.DbRequestManager;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.request.impl.database.IDbRequestServer;
import com.pine.tool.request.impl.http.nohttp.NoRequestManager;
import com.pine.tool.router.IRouterManager;
import com.pine.tool.router.IRouterManagerFactory;
import com.pine.tool.router.RouterException;
import com.pine.tool.router.RouterManager;
import com.pine.tool.router.impl.arouter.manager.ARouterManager;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

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
            ConfigApplication.onCreate();
            WelcomeApplication.onCreate();
            LoginApplication.onCreate();
            MainApplication.onCreate();

            initManager();

            ConfigApplication.attach();
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

    private void initManager() {
        RouterManager.init(this, "com.pine.template.config.router.command",
                new IRouterManagerFactory() {
                    @Override
                    public IRouterManager makeRouterManager() {
                        switch (BuildConfig.APP_THIRD_ROUTER_PROVIDER) {
                            case "arouter":
                                return ARouterManager.getInstance();
                            default:
                                return ARouterManager.getInstance();
                        }
                    }

                    @Override
                    public boolean isBundleEnable(String bundleKey) {
                        if (BuildConfig.BUILD_BIZ_BUNDLE != null) {
                            for (String bizBundle : BuildConfig.BUILD_BIZ_BUNDLE) {
                                if (bundleKey.equals(bizBundle)) {
                                    return true;
                                }
                            }
                        }
                        return ConfigSwitcherServer.isEnable(bundleKey);
                    }
                });

        RequestManager.init(this, new IRequestManagerFactory() {
            @Override
            public IRequestManager makeRequestManager() {
                switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
                    case "local":
                        return DbRequestManager.getInstance(new IDbRequestServer() {
                            @Override
                            public DbResponse request(Bundle bundle) {
                                try {
                                    return RouterManager.callDataCommandDirect(mApplication,
                                            BuildConfigKey.BUNDLE_DB_SEVER,
                                            RouterDbServerCommand.callDbServerCommand, bundle);
                                } catch (RouterException e) {
                                    return new DbResponse();
                                }
                            }
                        });
                    default:
                        switch (BuildConfig.APP_THIRD_HTTP_REQUEST_PROVIDER) {
                            case "nohttp":
                                return NoRequestManager.getInstance();
                            default:
                                return NoRequestManager.getInstance();
                        }
                }
            }
        });
        HashMap<String, String> params = DeviceInfoHelper.getDeviceInfoParams();
        if (params != null) {
            RequestManager.addGlobalRequestParams(DeviceInfoHelper.getDeviceInfoParams());
        }

        ScanManager.init(this, new IScanManagerFactory() {
            @Override
            public IScanManager makeScanManager(Context context) {
                switch (BuildConfig.APP_THIRD_SCAN_PROVIDER) {
                    case "zxing":
                        return ZXingScanManager.getInstance();
                    default:
                        return ZXingScanManager.getInstance();
                }
            }
        });

        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.LOGIN,
                new UiAccessLoginExecutor());
        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.CONFIG_SWITCHER,
                new UiAccessConfigSwitcherExecutor());
//        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.VIP_LEVEL,
//                new UiAccessVipLevelExecutor());
    }

    private void doStartupBusiness() {

    }
}
