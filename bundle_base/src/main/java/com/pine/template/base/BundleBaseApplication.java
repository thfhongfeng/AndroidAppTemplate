package com.pine.template.base;

import android.content.Context;
import android.os.Bundle;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.app.template.bundle_base.router.RouterDbServerCommand;
import com.pine.template.base.business.access.UiAccessConfigSwitcherExecutor;
import com.pine.template.base.business.access.UiAccessLoginExecutor;
import com.pine.template.base.business.access.UiAccessType;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.component.scan.IScanManager;
import com.pine.template.base.component.scan.IScanManagerFactory;
import com.pine.template.base.component.scan.ScanManager;
import com.pine.template.base.component.scan.zxing.ZXingScanManager;
import com.pine.template.base.component.share.manager.ShareManager;
import com.pine.template.base.component.share.manager.SinaShareManager;
import com.pine.template.base.component.share.manager.TencentShareManager;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.device_sdk.DeviceSdkProxy;
import com.pine.template.base.helper.DeviceInfoHelper;
import com.pine.template.base.helper.ResourceHelper;
import com.pine.template.base.request.impl.dbServer.DbRequestManager;
import com.pine.template.base.request.impl.dbServer.IDbRequestServer;
import com.pine.template.base.request.impl.http.nohttp.NoRequestManager;
import com.pine.template.base.widget.view.BilingualTextView;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.tool.RootApplication;
import com.pine.tool.access.UiAccessManager;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IRequestManagerFactory;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.router.IRouterManager;
import com.pine.tool.router.IRouterManagerFactory;
import com.pine.tool.router.RouterException;
import com.pine.tool.router.RouterManager;
import com.pine.tool.router.impl.arouter.manager.ARouterManager;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class BundleBaseApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(BundleBaseApplication.class);
    public static boolean SDK_INIT_ALREADY = false;

    protected BundleBaseApplication() {
        throw new IllegalArgumentException(getClass() + " prohibited from being constructed");
    }

    public static void onCreate() {
        SDK_INIT_ALREADY = DeviceSdkManager.init(mApplication, new DeviceSdkProxy());
        ConfigSwitcherServer.init();
    }

    public final static void initManager() {
        BilingualTextView.setup(ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_BILINGUAL_TEXT));

        ResourceHelper.setup(ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_FIRST_LOCAL, "zh_CN"),
                ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_SECOND_LOCAL, "zh_CN"));

        ImageLoaderManager.init(BuildConfig.APP_THIRD_IMAGE_LOADER_PROVIDER);

        TencentShareManager.TencentConfig tencentConfig =
                new TencentShareManager.TencentConfig(BuildConfig.QQ_FOR_APP_ID, BuildConfig.WX_FOR_APP_ID,
                        BuildConfig.WX_SECRET_KEY, R.drawable.base_bg_loading);
        SinaShareManager.SinaConfig sinaConfig =
                new SinaShareManager.SinaConfig(BuildConfig.WEI_BO_FOR_APP_KEY, BuildConfig.WEI_BO_SECRET_KEY);
        ShareManager.init(mApplication, BuildConfig.APP_NAME_ID, BaseUrlConstants.SERVER(),
                tencentConfig, sinaConfig);

        RouterManager.init(mApplication, BuildConfigKey.getBundleRouterMap(),
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
                        if (BuildConfig.BUILD_ALL_BUNDLE != null) {
                            for (String bundle : BuildConfig.BUILD_ALL_BUNDLE) {
                                if (bundleKey.equals(bundle)) {
                                    return true;
                                }
                            }
                        }
                        return ConfigSwitcherServer.isEnable(bundleKey);
                    }
                });

        RequestManager.init(mApplication, new IRequestManagerFactory() {
            @Override
            public IRequestManager makeRequestManager() {
                switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
                    case "local":
                        return DbRequestManager.getInstance(new IDbRequestServer() {
                            @Override
                            public Response request(Bundle bundle) {
                                try {
                                    return RouterManager.callDataCommandDirect(mApplication,
                                            BuildConfigKey.BUNDLE_DB_SEVER,
                                            RouterDbServerCommand.callDbServerCommand, bundle);
                                } catch (RouterException e) {
                                    return new Response();
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

        ScanManager.init(mApplication, new IScanManagerFactory() {
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

    public static void attach() {

    }
}
