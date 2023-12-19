package com.pine.template.base.bg.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.pine.template.base.R;
import com.pine.template.base.bg.receiver.AppBgReceiver;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class AppBgService extends Service {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate: BgService created");
        mContext = this;

        mWorkThread = new HandlerThread(TAG);
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());

        regNetworkReceiver();
    }

    public static final int COMMAND_INIT = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.releaseLog(TAG, "onStartCommand: BgService started");
        if (intent == null) {
            return Service.START_STICKY;
        }
        startServiceForeground();
        int command = intent.getIntExtra("command", COMMAND_INIT);
        int delayTime = intent.getIntExtra("delay", 0);
        Bundle bundle = intent.getBundleExtra("bundle");

        doWork(command, bundle, delayTime);
        // 如果服务被意外销毁，系统会尝试重新创建服务，并通过这个方法传递之前的Intent
        return START_STICKY;
    }

    //开启通知
    @SuppressLint("NewApi")
    private void startServiceForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nm = (NotificationManager) getSystemService(ns);
            NotificationChannel nc = new NotificationChannel(TAG, AppUtils.getAppName(), NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(nc);
            Notification notification = new Notification.Builder(mContext, TAG)
                    .setContentTitle(getString(R.string.base_bg_service_content_title, AppUtils.getAppName()))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.base_ic_launcher).build();
            startForeground(2, notification);
        }
    }

    //关闭通知
    private void stopServiceForeground() {
        stopForeground(true);
    }

    private AppBgReceiver mNetworkReceiver = new AppBgReceiver();

    private void regNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        registerReceiver(mNetworkReceiver, intentFilter);
    }

    private void unRegNetworkReceiver() {
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 如果你的服务不支持绑定，可以返回null
        return null;
    }

    @Override
    public void onDestroy() {
        unRegNetworkReceiver();

        mMainHandler.removeCallbacksAndMessages(null);
        mWorkHandler.removeCallbacksAndMessages(null);

        mWorkThread.interrupt();

        stopServiceForeground();
        super.onDestroy();
        LogUtils.releaseLog(TAG, "onDestroy: BgService destroyed");
    }

    private void doWork(int command, Bundle bundle, int delayTime) {
        LogUtils.releaseLog(TAG, "try do Work command=" + command + ", delayTime=" + delayTime);
        if (!mWorkHandler.hasMessages(command)) {
            LogUtils.releaseLog(TAG, "start do Work command=" + command);
            Message msg = new Message();
            msg.what = command;
            msg.obj = bundle;
            if (delayTime > 0) {
                mWorkHandler.sendMessageDelayed(msg, delayTime);
            } else {
                mWorkHandler.sendMessage(msg);
            }
        }
    }

    private class WorkHandler extends Handler {
        WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Bundle bundle = (Bundle) msg.obj;
            switch (msg.what) {
                default:
                    break;
            }
        }
    }

    private void onConnectivityAction(int cmd, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        postToMainThread(cmd, bundle);
    }

    private void onRssiChangeAction(int cmd, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        postToMainThread(cmd, bundle);
    }

    private void postToMainThread(final int cmd, final Bundle bundle) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
