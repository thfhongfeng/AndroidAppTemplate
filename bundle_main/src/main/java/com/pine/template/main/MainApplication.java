package com.pine.template.main;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.base.BaseApplication;
import com.pine.template.base.DeviceConfig;
import com.pine.template.base.bgwork.BgWorkManager;
import com.pine.template.base.bgwork.checker.BgCheckManager;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.business.track.DefaultAppTrackAdapter;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.business.track.entity.AppTracksHeader;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.helper.AutoRebootHelper;
import com.pine.template.base.manager.MainTtsManager;
import com.pine.template.base.manager.tts.TtsManager;
import com.pine.template.base.remote.BaseRouterClient;
import com.pine.template.main.mqtt.MqttClient;
import com.pine.template.main.track.TrackRecordHelper;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;
import com.pine.tool.util.SysSettingsUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class MainApplication extends BaseApplication {
    private final static String TAG = LogUtils.makeLogTag(MainApplication.class);
    public static String DEVICE_ID;

    public static String APP_KEY;
    public static String VERSION_NAME;
    public static int VERSION_CODE;
    public static String SDK_TYPE;

    public static int ORIGINAL_VOLUME = 0;

    public static void attach() {
        DeviceConfig.init();
        DEVICE_ID = DeviceConfig.getDeviceUniqueNum(mApplication);
        APP_KEY = mApplication.getPackageName();
        VERSION_NAME = AppUtils.getVersionName();
        VERSION_CODE = AppUtils.getVersionCode();
        SDK_TYPE = BuildConfig.FLAVOR;
        LogUtils.d(TAG, "APP_Info APP_KEY:" + APP_KEY
                + ", VERSION_NAME:" + VERSION_NAME + ", VERSION_CODE:" + VERSION_CODE
                + ", SDK_TYPE:" + SDK_TYPE);

        AppTrackManager.getInstance().init(mApplication, MainUrlConstants.APP_TRACK(), new DefaultAppTrackAdapter() {
            @Override
            public void setupBaseInfoAndIp(@NonNull Context context, AppTrack appTrack) {
                AccountBean accountBean = BaseRouterClient.getLoginAccount(context, null);
                String accountId = "";
                String name = "";
                int accountType = 0;
                if (accountBean != null) {
                    accountId = accountBean.getId();
                    name = accountBean.getName();
                    accountType = accountBean.getAccountType();
                }
                appTrack.setAccountId(accountId);
                appTrack.setUserName(name);
                appTrack.setAccountType(accountType);

                appTrack.setVersionCode(AppUtils.getVersionCode());
                appTrack.setVersionName(AppUtils.getVersionName());

                String ip = NetWorkUtils.getIpAddress();
                appTrack.setIp(TextUtils.isEmpty(ip) ? "" : ip);
            }

            @Override
            public AppTracksHeader getTrackHeader(Context context) {
                AppTracksHeader header = new AppTracksHeader();
                header.setDeviceId(DeviceConfig.getDeviceUniqueNum(context));
                header.setDeviceModel(AppUtils.getDeviceModel());
                header.setPkgName(context.getPackageName());
                return header;
            }
        });
        TtsManager.getInstance().init(new MainTtsManager());

        AutoRebootHelper.setupAutoReboot(mApplication);

        addAppStateListener(TAG, new IOnAppStateListener() {
            @Override
            public void onAppForegroundChange(boolean isForeground) {
                if (isForeground) {
                    ORIGINAL_VOLUME = SysSettingsUtils.getTtsVolume(AppUtils.getApplicationContext());
                    int volumePct = ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_VOLUME, -1);
                    LogUtils.d(TAG, "sys volume:" + ORIGINAL_VOLUME + ", app config volume:" + volumePct);
                    if (volumePct >= 0 && volumePct < 100) {
                        SysSettingsUtils.setTtsVolumePct(mApplication, volumePct);
                        LogUtils.d(TAG, "set app volume:" + volumePct);
                    }
                } else {
                    SysSettingsUtils.setTtsVolume(mApplication, ORIGINAL_VOLUME);
                    LogUtils.d(TAG, "setup original volume:" + ORIGINAL_VOLUME + " when app exit");
                }
            }

            @Override
            public void onAppCreated() {
                BgWorkManager.getInstance().init();

                BgCheckManager.getInstance().scheduleChecker();

                TrackRecordHelper.getInstance().init(mApplication);

                MqttClient.getInstance().init(mApplication);
            }

            @Override
            public void onAppDestroyed() {
                MqttClient.getInstance().release();

                BgCheckManager.getInstance().releaseChecker();

                TrackRecordHelper.getInstance().release(mApplication);

                BgWorkManager.getInstance().release();
            }
        });
    }
}
