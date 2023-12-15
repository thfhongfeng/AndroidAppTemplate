package com.pine.template.welcome.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class BootCompleteReceiver extends BroadcastReceiver {
    private final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            LogUtils.d(TAG, "BootCompleteReceiver :" + intent.getAction());
            AppUtils.startApp(context, context.getPackageName());
        }
    }
}
