package com.pine.template.base.bg;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.pine.template.base.bg.network.BgNetWorker;
import com.pine.template.base.bg.network.OnNetworkChangedListener;
import com.pine.template.base.bg.service.AppBgService;

public class AppBgManager {
    private static final boolean mServiceEnable = true;

    private static Context mContext = null;
    private static Intent mAppBgServiceIntent = null;
    private static volatile boolean mServiceStarted = false;

    public synchronized static boolean isBgServicePrepared() {
        return mContext != null && mAppBgServiceIntent != null && mServiceStarted;
    }

    public synchronized static void startBgService(Context context) {
        mContext = context;
        if (mServiceEnable) {
            mAppBgServiceIntent = new Intent(mContext, AppBgService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(mAppBgServiceIntent);
            } else {
                mContext.startService(mAppBgServiceIntent);
            }
            mServiceStarted = true;
        }
    }

    public synchronized static void stopBgService() {
        if (isBgServicePrepared()) {
            mContext.stopService(mAppBgServiceIntent);
            mContext = null;
            mAppBgServiceIntent = null;
        }
    }

    public static void regNetworkChangedListener(final OnNetworkChangedListener listener) {
        BgNetWorker.getInstance().regNetworkChangedListener(listener);
    }

    public static boolean isRegNetworkChangedListener(final OnNetworkChangedListener listener) {
        return BgNetWorker.getInstance().isRegNetworkChangedListener(listener);
    }

    public static void unRegNetworkChangedListener(final OnNetworkChangedListener listener) {
        BgNetWorker.getInstance().unRegNetworkChangedListener(listener);
    }

    public static boolean isMobilType() {
        return BgNetWorker.getInstance().isMobilType();
    }

    public static boolean isWifiType() {
        return BgNetWorker.getInstance().isWifiType();
    }

    public static boolean isEthernet() {
        return BgNetWorker.getInstance().isEthernet();
    }
}
