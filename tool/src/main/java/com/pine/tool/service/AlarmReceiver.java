package com.pine.tool.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 常驻后台定时服务{@link AlarmService}对应的广播接收器
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("PineAlarmService".equals(intent.getAction())) {
            Intent alarmServiceIntent = new Intent(context, AlarmService.class);
            alarmServiceIntent.putExtra("bundle", intent.getBundleExtra("bundle"));
            context.startService(alarmServiceIntent);
        }
    }
}
