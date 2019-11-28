package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.pine.base.BaseApplication;
import com.pine.base.BaseUrlConstants;
import com.pine.base.access.UiAccessConfigSwitcherExecutor;
import com.pine.base.access.UiAccessLoginExecutor;
import com.pine.base.access.UiAccessType;
import com.pine.base.access.UiAccessVipLevelExecutor;
import com.pine.base.component.map.IMapManager;
import com.pine.base.component.map.IMapManagerFactory;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.component.map.baidu.BaiduMapManager;
import com.pine.base.component.scan.IScanManager;
import com.pine.base.component.scan.IScanManagerFactory;
import com.pine.base.component.scan.ScanManager;
import com.pine.base.component.scan.zxing.ZXingScanManager;
import com.pine.base.component.share.manager.ShareManager;
import com.pine.base.router.command.RouterDbServerCommand;
import com.pine.base.track.AppTrackManager;
import com.pine.config.BuildConfig;
import com.pine.config.ConfigKey;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.db_server.DbServerApplication;
import com.pine.login.LoginApplication;
import com.pine.main.MainApplication;
import com.pine.mvc.MvcApplication;
import com.pine.mvp.MvpApplication;
import com.pine.mvvm.MvvmApplication;
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
import com.pine.tool.router.RouterManager;
import com.pine.tool.router.impl.arouter.manager.ARouterManager;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.user.UserApplication;
import com.pine.welcome.WelcomeApplication;

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

        // 主进程初始化
        if (mApplication.getPackageName().equals(AppUtils.getCurProcessName(mApplication))) {
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

            doStartupBusiness();
        }
    }

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }

    private void initManager() {
        RouterManager.init(this, "com.pine.base.router.command",
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
                        return ConfigSwitcherServer.getInstance().isEnable(bundleKey);
                    }
                });

        ShareManager.getInstance().init(this, R.mipmap.res_ic_launcher);

        RequestManager.init(this, new IRequestManagerFactory() {
            @Override
            public IRequestManager makeRequestManager() {
                switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
                    case "local":
                        return DbRequestManager.getInstance(new IDbRequestServer() {
                            @Override
                            public DbResponse request(Bundle bundle) {
                                return RouterManager.callDataCommandDirect(mApplication, ConfigKey.BUNDLE_DB_SEVER_KEY,
                                        RouterDbServerCommand.callDbServerCommand, bundle);
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

        MapSdkManager.init(this, new IMapManagerFactory() {
            @Override
            public IMapManager makeMapManager(Context context) {
                switch (BuildConfig.APP_THIRD_MAP_PROVIDER) {
                    case "baidu":
                        return BaiduMapManager.getInstance();
                    default:
                        return BaiduMapManager.getInstance();
                }
            }
        });

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
        UiAccessManager.getInstance().addAccessExecutor(UiAccessType.VIP_LEVEL,
                new UiAccessVipLevelExecutor());
    }

    private void doStartupBusiness() {
        AppTrackManager.getInstance().init(this, BaseUrlConstants.APP_TRACK_UPLOAD);
        AppTrackManager.getInstance().uploadAllExistTrack(null);
    }
}
