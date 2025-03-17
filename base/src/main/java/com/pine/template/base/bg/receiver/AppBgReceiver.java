package com.pine.template.base.bg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.pine.template.base.bg.network.BgNetWorker;
import com.pine.template.base.bg.network.BgNetworkHelper;
import com.pine.template.base.bg.network.NetworkType;
import com.pine.template.base.bg.service.AppBgService;
import com.pine.tool.util.LogUtils;

public class AppBgReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.i(TAG, "onReceive action: " + action);
        if (TextUtils.equals(ConnectivityManager.CONNECTIVITY_ACTION, action)) {
            NetworkType networkType = BgNetworkHelper.getNetworkType();
            BgNetWorker.getInstance().setNetworkType(networkType);
            // 切换飞行模式时，在关闭飞行模式网络恢复后，如果马上获取网络状态，
            // 有概率会networkType会为NETWORK_NO，因此延时再做一次网络检查和设置
            mMainHandler.removeCallbacksAndMessages(null);
            if (networkType == NetworkType.NETWORK_NO) {
                LogUtils.i(TAG, "CONNECTIVITY_ACTION NetworkType is NETWORK_NO," +
                        " try check and set network type after 500ms for some reason");
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NetworkType networkType = BgNetworkHelper.getNetworkType();
                        BgNetWorker.getInstance().setNetworkType(networkType);
                    }
                }, 500);
            }
        } else if (TextUtils.equals(WifiManager.RSSI_CHANGED_ACTION, action)) {
            BgNetWorker.getInstance().setWifiLevel(true);
        }
    }

    private Intent buildCmdIntent(Context context, int command, int delay) {
        Intent intent = new Intent(context, AppBgService.class);
        intent.putExtra("command", command);
        intent.putExtra("delay", delay);
        return intent;
    }

    private Intent buildCmdIntent(Context context, int command, Bundle bundle, int delay) {
        Intent intent = new Intent(context, AppBgService.class);
        intent.putExtra("command", command);
        intent.putExtra("bundle", bundle);
        intent.putExtra("delay", delay);
        return intent;
    }
}
