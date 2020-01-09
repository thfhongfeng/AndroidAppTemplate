package com.pine.tool.service;

import android.os.Handler;

import com.pine.tool.util.LogUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用运行期定时任务协助类
 */
public class TimerWorkHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static TimerWorkHelper mInstance;
    private final Timer mTimer = new Timer(TAG);

    private ConcurrentHashMap<String, TimerTask> mTimerTaskMap = new ConcurrentHashMap<>();

    private TimerWorkHelper() {

    }

    public synchronized static TimerWorkHelper getInstance() {
        if (mInstance == null) {
            mInstance = new TimerWorkHelper();
        }
        return mInstance;
    }

    /**
     * 同步定时任务
     *
     * @param tag      定时器标识
     * @param delay    定时时间，单位毫秒
     * @param runnable 定时任务
     */
    public void schemeTimerWork(final String tag, long delay, final Runnable runnable) {
        cancel(tag);
        final Handler handler = new Handler();
        TimerTask timeWorker = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                        mTimerTaskMap.remove(tag);
                    }
                });
            }
        };
        mTimer.schedule(timeWorker, delay);
        mTimerTaskMap.put(tag, timeWorker);
    }

    /**
     * 异步定时任务
     *
     * @param tag      定时器标识
     * @param delay    定时时间，单位毫秒
     * @param runnable 定时任务
     */
    public void schemeAsyncTimerWork(String tag, long delay, final Runnable runnable) {
        cancel(tag);
        TimerTask timeWorker = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        mTimer.schedule(timeWorker, delay);
        mTimerTaskMap.put(tag, timeWorker);
    }

    public void cancel(String tag) {
        TimerTask timeWorker = mTimerTaskMap.get(tag);
        if (timeWorker != null) {
            timeWorker.cancel();
        }
        mTimerTaskMap.remove(tag);
    }

    public void cancelAll() {
        Iterator<Map.Entry<String, TimerTask>> iterator = mTimerTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TimerTask> entry = iterator.next();
            entry.getValue().cancel();
        }
        mTimerTaskMap.clear();
    }
}
