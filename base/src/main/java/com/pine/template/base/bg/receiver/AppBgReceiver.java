package com.pine.template.base.bg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.base.bg.network.BgNetWorker;
import com.pine.template.base.bg.network.BgNetworkHelper;
import com.pine.template.base.bg.network.NetworkType;
import com.pine.template.base.bg.service.AppBgService;
import com.pine.tool.util.LogUtils;

public class AppBgReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.i(TAG, "onReceive action: " + action);
        if (TextUtils.equals(ConnectivityManager.CONNECTIVITY_ACTION, action)) {
            NetworkType networkType = BgNetworkHelper.getNetworkType();
            BgNetWorker.getInstance().setNetworkType(networkType);
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
