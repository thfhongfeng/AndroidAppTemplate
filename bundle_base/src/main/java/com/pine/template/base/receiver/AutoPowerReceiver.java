package com.pine.template.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.helper.AutoRebootHelper;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

public class AutoPowerReceiver extends BroadcastReceiver {
    private final String TAG = "AutoPowerReceiver";

    public final static String ACTION_AUTO_REBOOT = "com.pine.template.action.ACTION_AUTO_REBOOT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "onReceive, action = " + action);
        if (TextUtils.equals(ACTION_AUTO_REBOOT, action)) {
            autoReboot(context);
        } else if (TextUtils.equals(Intent.ACTION_TIME_CHANGED, action)) {
            AutoRebootHelper.setupAutoReboot(context);
        }
    }

    private void autoReboot(Context context) {
        try {
            SharePreferenceUtils.saveToConfig(AutoRebootHelper.SP_KEY_AUTO_REBOOT_TIME, -1l);
            DeviceSdkManager.getInstance().reboot(false, "autoReboot", false);
        } catch (DeviceSdkException e) {
            LogUtils.w(TAG, "DeviceSdkException for reboot, ignore");
            AutoRebootHelper.setupAutoReboot(context);
        }
    }
}