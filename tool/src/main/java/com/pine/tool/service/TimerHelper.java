package com.pine.tool.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用运行期定时任务协助类
 */
public class TimerHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static TimerHelper mInstance;
    private final Timer mTimer = new Timer(TAG);

    private ConcurrentHashMap<String, TimerTaskEntity> mTimerTaskMap = new ConcurrentHashMap<>();

    // Timer定时器跟系统时间有关，系统时间如果调到过去，会造成Timer定时器挂起。需要在系统时间改变时，重启定时任务
    private BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(Intent.ACTION_DATE_CHANGED, action)) {
                LogUtils.d(TAG, "onReceive ACTION_DATE_CHANGED");
                restartTimerTask();
            } else if (TextUtils.equals(Intent.ACTION_TIME_CHANGED, action)) {
                LogUtils.d(TAG, "onReceive ACTION_TIME_CHANGED");
                restartTimerTask();
            }
        }
    };

    private TimerHelper() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        AppUtils.getApplicationContext().registerReceiver(mTimeChangedReceiver, filter);
    }

    public synchronized static TimerHelper getInstance() {
        if (mInstance == null) {
            mInstance = new TimerHelper();
        }
        return mInstance;
    }

    private synchronized void restartTimerTask() {
        if (mTimer == null) {
            return;
        }
        List<TimerTaskEntity> list = getAllTimeTask(false);
        for (TimerTaskEntity timerTaskEntity : list) {
            if (timerTaskEntity != null && timerTaskEntity.isValid()) {
                if (timerTaskEntity.type == TimerTaskEntity.TYPE_ONCE) {
                    if (!timerTaskEntity.isStart) {
                        cancelImpl(timerTaskEntity.tag);
                        TimerTaskEntity resetEntity = timerTaskEntity.resetEntity();
                        if (resetEntity != null) {
                            realSchemeTimerWork(resetEntity);
                        }
                    }
                } else if (timerTaskEntity.type == TimerTaskEntity.TYPE_PERIOD) {
                    cancelImpl(timerTaskEntity.tag);
                    TimerTaskEntity resetEntity = timerTaskEntity.resetEntity();
                    if (resetEntity != null) {
                        realSchemeTimerWork(resetEntity);
                    }
                }
            }
        }
    }

    private void addTimerTask(String tag, TimerTaskEntity timerTaskEntity) {
        synchronized (mTimerTaskMap) {
            mTimerTaskMap.put(tag, timerTaskEntity);
        }
    }

    private TimerTaskEntity getTimerTask(String tag) {
        synchronized (mTimerTaskMap) {
            return mTimerTaskMap.get(tag);
        }
    }

    private List<TimerTaskEntity> getAllTimeTask(boolean clear) {
        List<TimerTaskEntity> taskList = new ArrayList<>();
        synchronized (mTimerTaskMap) {
            Set<String> keySet = mTimerTaskMap.keySet();
            for (String key : keySet) {
                TimerTaskEntity timerTaskEntity = mTimerTaskMap.get(key);
                if (timerTaskEntity != null) {
                    taskList.add(timerTaskEntity);
                }
            }
            if (clear) {
                mTimerTaskMap.clear();
            }
        }
        return taskList;
    }

    private TimerTaskEntity getAndRemoveTimerTask(String tag) {
        synchronized (mTimerTaskMap) {
            return mTimerTaskMap.remove(tag);
        }
    }

    private void removeTimerTask(String tag) {
        synchronized (mTimerTaskMap) {
            mTimerTaskMap.remove(tag);
        }
    }

    /**
     * 同步定时任务
     *
     * @param tag      定时器标识
     * @param delay    定时时间，单位毫秒
     * @param runnable 定时任务
     */
    public void schemeTimerWorkImpl(final String tag, long delay, final Runnable runnable) {
        final TimerTaskEntity timerTaskEntity = new TimerTaskEntity();
        timerTaskEntity.tag = tag;
        timerTaskEntity.type = TimerTaskEntity.TYPE_ONCE;
        timerTaskEntity.delay = delay;
        timerTaskEntity.async = false;
        timerTaskEntity.runnable = runnable;

        timerTaskEntity.timerTask = buildTimerTask(timerTaskEntity, runnable);

        cancel(timerTaskEntity.tag);
        realSchemeTimerWork(timerTaskEntity);
    }

    /**
     * 异步定时任务
     *
     * @param tag      定时器标识
     * @param delay    定时时间，单位毫秒
     * @param runnable 定时任务
     */
    public void schemeAsyncTimerWorkImpl(final String tag, long delay, final Runnable runnable) {
        final TimerTaskEntity timerTaskEntity = new TimerTaskEntity();
        timerTaskEntity.tag = tag;
        timerTaskEntity.type = TimerTaskEntity.TYPE_ONCE;
        timerTaskEntity.delay = delay;
        timerTaskEntity.async = true;
        timerTaskEntity.runnable = runnable;

        timerTaskEntity.timerTask = buildTimerTask(timerTaskEntity, runnable);

        cancel(timerTaskEntity.tag);
        realSchemeTimerWork(timerTaskEntity);
    }

    public void schemeTimerWorkImpl(final String tag, long delay, long period, final Runnable runnable) {
        final TimerTaskEntity timerTaskEntity = new TimerTaskEntity();
        timerTaskEntity.tag = tag;
        timerTaskEntity.type = TimerTaskEntity.TYPE_PERIOD;
        timerTaskEntity.delay = delay;
        timerTaskEntity.period = period;
        timerTaskEntity.async = false;
        timerTaskEntity.runnable = runnable;

        timerTaskEntity.timerTask = buildTimerTask(timerTaskEntity, runnable);

        cancel(timerTaskEntity.tag);
        realSchemeTimerWork(timerTaskEntity);
    }

    public void schemeAsyncTimerWorkImpl(String tag, long delay, long period, final Runnable runnable) {
        final TimerTaskEntity timerTaskEntity = new TimerTaskEntity();
        timerTaskEntity.tag = tag;
        timerTaskEntity.type = TimerTaskEntity.TYPE_PERIOD;
        timerTaskEntity.delay = delay;
        timerTaskEntity.period = period;
        timerTaskEntity.async = true;
        timerTaskEntity.runnable = runnable;

        timerTaskEntity.timerTask = buildTimerTask(timerTaskEntity, runnable);

        cancel(timerTaskEntity.tag);
        realSchemeTimerWork(timerTaskEntity);
    }

    public void realSchemeTimerWork(final TimerTaskEntity timerTaskEntity) {
        if (timerTaskEntity == null || !timerTaskEntity.isValid()) {
            LogUtils.d(TAG, "realSchemeTimerWork task:" + timerTaskEntity + " is invalid");
            return;
        }
        if (timerTaskEntity.period > 0) {
            mTimer.schedule(timerTaskEntity.timerTask, timerTaskEntity.delay, timerTaskEntity.period);
        } else {
            mTimer.schedule(timerTaskEntity.timerTask, timerTaskEntity.delay);
        }
        addTimerTask(timerTaskEntity.tag, timerTaskEntity);
        LogUtils.d(TAG, "realSchemeTimerWork task:" + timerTaskEntity);
    }

    public void cancelImpl(TimerTaskEntity timerTaskEntity) {
        if (timerTaskEntity != null && timerTaskEntity.timerTask != null) {
            LogUtils.d(TAG, "cancelImpl task:" + timerTaskEntity);
            timerTaskEntity.timerTask.cancel();
        }
    }

    public void cancelImpl(String tag) {
        TimerTaskEntity timerTaskEntity = getAndRemoveTimerTask(tag);
        cancelImpl(timerTaskEntity);
    }

    public void cancelAllImpl() {
        List<TimerTaskEntity> list = getAllTimeTask(true);
        for (TimerTaskEntity timerTaskEntity : list) {
            cancelImpl(timerTaskEntity);
        }
    }

    private TimerTask buildTimerTask(@NonNull final TimerTaskEntity timerTaskEntity,
                                     @NonNull final Runnable runnable) {
        TimerTask timeWorker = null;
        if (timerTaskEntity.async) {
            final Handler handler = new Handler();
            timeWorker = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            timerTaskEntity.isStart = true;
                            if (runnable != null) {
                                runnable.run();
                            }
                            if (timerTaskEntity.period <= 0) {
                                removeTimerTask(timerTaskEntity.tag);
                            }
                        }
                    });
                }
            };
        } else {
            timeWorker = new TimerTask() {
                @Override
                public void run() {
                    timerTaskEntity.isStart = true;
                    if (runnable != null) {
                        runnable.run();
                    }
                    if (timerTaskEntity.period <= 0) {
                        removeTimerTask(timerTaskEntity.tag);
                    }
                }
            };
        }
        return timeWorker;
    }

    class TimerTaskEntity {
        public static final int TYPE_ONCE = 0;
        public static final int TYPE_PERIOD = 1;
        public String tag;
        public int type;
        public TimerTask timerTask;
        public Runnable runnable;
        public long delay;
        public long period;
        public boolean isStart;
        public boolean async;

        public TimerTaskEntity resetEntity() {
            if (runnable == null) {
                LogUtils.d(TAG, "resetEntity runnable is null, return null");
                return null;
            }
            timerTask = buildTimerTask(this, runnable);
            isStart = false;
            return this;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(tag) && timerTask != null
                    && (type == TYPE_ONCE || type == TYPE_PERIOD);
        }

        @Override
        public String toString() {
            return "TimerTaskEntity{" +
                    "tag='" + tag + '\'' +
                    ", type=" + type +
                    ", timerTask=" + timerTask +
                    ", delay=" + delay +
                    ", period=" + period +
                    ", isStart=" + isStart +
                    ", async=" + async +
                    '}';
        }
    }

    public static void schemeTimerWork(int tag, long delay, final Runnable runnable) {
        getInstance().schemeTimerWorkImpl(tag + "", delay, runnable);
    }

    public static void schemeTimerWork(String tag, long delay, final Runnable runnable) {
        getInstance().schemeTimerWorkImpl(tag, delay, runnable);
    }

    public static void schemeAsyncTimerWork(int tag, long delay, final Runnable runnable) {
        getInstance().schemeAsyncTimerWorkImpl(tag + "", delay, runnable);
    }

    public static void schemeAsyncTimerWork(String tag, long delay, final Runnable runnable) {
        getInstance().schemeAsyncTimerWorkImpl(tag, delay, runnable);
    }

    public static void schemeTimerWork(int tag, long delay, long period, final Runnable runnable) {
        getInstance().schemeTimerWorkImpl(tag + "", delay, period, runnable);
    }

    public static void schemeTimerWork(String tag, long delay, long period, final Runnable runnable) {
        getInstance().schemeTimerWorkImpl(tag, delay, period, runnable);
    }

    public static void schemeAsyncTimerWork(int tag, long delay, long period, final Runnable runnable) {
        getInstance().schemeAsyncTimerWorkImpl(tag + "", delay, period, runnable);
    }

    public static void schemeAsyncTimerWork(String tag, long delay, long period, final Runnable runnable) {
        getInstance().schemeAsyncTimerWorkImpl(tag, delay, period, runnable);
    }

    public static void cancel(int tag) {
        cancel(tag + "");
    }

    public static void cancel(String tag) {
        getInstance().cancelImpl(tag);
    }

    public static void cancelAll() {
        getInstance().cancelAllImpl();
    }
}
