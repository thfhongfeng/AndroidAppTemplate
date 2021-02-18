package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.pine.template.base.BaseApplication;
import com.pine.template.base.BaseUrlConstants;
import com.pine.template.base.access.UiAccessConfigSwitcherExecutor;
import com.pine.template.base.access.UiAccessLoginExecutor;
import com.pine.template.base.access.UiAccessType;
import com.pine.template.base.access.UiAccessVipLevelExecutor;
import com.pine.template.base.component.map.IMapManager;
import com.pine.template.base.component.map.IMapManagerFactory;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.base.component.map.baidu.BaiduMapManager;
import com.pine.template.base.component.map.gaode.GaodeMapManager;
import com.pine.template.base.component.scan.IScanManager;
import com.pine.template.base.component.scan.IScanManagerFactory;
import com.pine.template.base.component.scan.ScanManager;
import com.pine.template.base.component.scan.zxing.ZXingScanManager;
import com.pine.template.base.component.share.manager.ShareManager;
import com.pine.template.base.router.command.RouterDbServerCommand;
import com.pine.template.base.track.AppTrackManager;
import com.pine.template.config.BuildConfig;
import com.pine.template.config.ConfigKey;
import com.pine.template.config.switcher.ConfigSwitcherServer;
import com.pine.template.db_server.DbServerApplication;
import com.pine.template.login.LoginApplication;
import com.pine.template.main.MainApplication;
import com.pine.template.mvc.MvcApplication;
import com.pine.template.mvp.MvpApplication;
import com.pine.template.mvvm.MvvmApplication;
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
import com.pine.template.user.UserApplication;
import com.pine.template.welcome.WelcomeApplication;

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

        LogUtils.d(TAG, "APP SHA1:" + AppUtils.SHA1(this));

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
        RouterManager.init(this, "com.pine.template.base.router.command",
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
                    case "gaode":
                        return GaodeMapManager.getInstance();
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
