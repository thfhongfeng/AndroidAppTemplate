package com.pine.template.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.business.track.TrackDefaultBuilder;
import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.helper.AutoRebootHelper;
import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoPowerReceiver extends BroadcastReceiver {
    private final String TAG = "AutoPowerReceiver";

    public final static String ACTION_AUTO_REBOOT = "com.pine.template.action.ACTION_AUTO_REBOOT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "onReceive, action = " + action);
        if (TextUtils.equals(ACTION_AUTO_REBOOT, action)) {
            recordAutoReboot(context);
            autoReboot(context);
        } else if (TextUtils.equals(Intent.ACTION_TIME_CHANGED, action)) {
            AutoRebootHelper.setupAutoReboot(context);
        }
    }

    private void autoReboot(Context context) {
        try {
            DeviceSdkManager.getInstance().reboot(false, "autoReboot", false);
        } catch (DeviceSdkException e) {
            LogUtils.w(TAG, "DeviceSdkException for reboot, ignore");
            AutoRebootHelper.setupAutoReboot(context);
        }
    }

    private final String DEFAULT_CUR_CLASS = "TrackRecordHelper";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void recordAutoReboot(@NonNull Context context) {
        Date recordDate = new Date();
        String actionData = context.getString(R.string.info_auto_reboot,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordOperation(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_AUTO_REBOOT, actionData,
                recordDate.getTime(), true, true);
    }
}