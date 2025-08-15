package com.pine.template.base.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class WakeLockHelper {
    private final String TAG = this.getClass().getSimpleName();

    private static volatile WakeLockHelper instance;

    public synchronized static WakeLockHelper getInstance() {
        if (instance == null) {
            instance = new WakeLockHelper();
        }
        return instance;
    }

    private Context mContext;
    private Handler mHandler;

    private WakeLockHelper() {
        mContext = AppUtils.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        mPm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mSleepWakeReceiver, intentFilter);
    }

    private PowerManager mPm;
    private PowerManager.WakeLock mWakeLock;

    private long mLastSleepTime, mLastWakeupTime;

    BroadcastReceiver mSleepWakeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // 处理设备进入休眠模式的操作
                // 例如：停止一些不必要的后台任务以节省电量
                mLastSleepTime = System.currentTimeMillis();
                LogUtils.d(TAG, "sleep time:" + mLastSleepTime);
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                // 处理设备唤醒的操作
                // 例如：恢复之前被暂停的任务或显示通知
                mLastWakeupTime = System.currentTimeMillis();
                LogUtils.d(TAG, "wakeup time:" + mLastWakeupTime);
            }
        }
    };

    public long getLastSleepTime() {
        return mLastSleepTime;
    }

    public long getLastWakeupTime() {
        return mLastWakeupTime;
    }

    private synchronized PowerManager.WakeLock createWakeLock() {
        LogUtils.d(TAG, "createWakeLock");
        return mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
    }

    public synchronized void schedule(long delay) {
        LogUtils.d(TAG, "schedule delay:" + delay);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                acquireWakeLock();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        releaseLock();
                    }
                }, delay);
            }
        }, 2000);
    }

    public synchronized void resetTime(long delay) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                releaseLock();
            }
        }, delay);
    }

    public synchronized void acquireWakeLock() {
        LogUtils.d(TAG, "acquireWakeLock");
        mHandler.removeCallbacksAndMessages(null);
        if (mWakeLock != null) {
            return;
        }
        mWakeLock = createWakeLock();
        //acquire()获取相应的锁
        mWakeLock.acquire();
    }

    public synchronized void releaseLockImmediately() {
        mHandler.removeCallbacksAndMessages(null);
        LogUtils.d(TAG, "releaseLockImmediately");
        if (mWakeLock != null) {
            //release释放
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    public synchronized void releaseLock() {
        mHandler.removeCallbacksAndMessages(null);
        long delay = -1;
        try {
            delay = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "releaseLock delay:" + delay);
        if (delay > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    releaseLockImmediately();
                }
            }, delay);
        }
    }
}
