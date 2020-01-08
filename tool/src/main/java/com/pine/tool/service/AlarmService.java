package com.pine.tool.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.Nullable;

/**
 * 后台定时服务
 */
public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getBundleExtra("bundle");
        boolean startImmediately = bundle.getBoolean("startImmediately", false);
        boolean async = bundle.getBoolean("async", true);
        boolean persistent = bundle.getBoolean("persistent", false);
        long delay = bundle.getLong("delay", 0);
        AlarmWorker worker = (AlarmWorker) bundle.getSerializable("worker");
        if (startImmediately) {
            if (async) {
                new Thread(worker).start();
            } else {
                new Handler(Looper.getMainLooper()).post(worker);
            }
        }
        if (!startImmediately || persistent) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent receiverIntent = new Intent(this, AlarmReceiver.class);
            bundle.putBoolean("startImmediately", true);
            receiverIntent.putExtra("bundle", bundle);
            receiverIntent.setAction("PineAlarmService");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);
            // pendingIntent 为发送广播
            long triggerAtTime = SystemClock.elapsedRealtime() + delay;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), delay, pendingIntent);
            }
        }
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }
}
