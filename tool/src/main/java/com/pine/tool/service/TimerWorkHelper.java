package com.pine.tool.service;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用运行期定时任务协助类
 */
public class TimerWorkHelper {
    private static TimerWorkHelper mInstance;

    private HashMap<String, Timer> mTimerMap = new HashMap<>();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

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
     * @param runnable 定时runnable
     */
    public void schemeTimerWork(String tag, long delay, final Runnable runnable) {
        Timer timer = mTimerMap.get(tag);
        if (timer == null) {
            timer = new Timer(tag);
            mTimerMap.put(tag, timer);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mMainHandler.post(runnable);
            }
        }, delay);
    }

    /**
     * 异步定时任务
     *
     * @param tag      定时器标识
     * @param delay    定时时间，单位毫秒
     * @param runnable 定时runnable
     */
    public void schemeAsyncTimerWork(String tag, long delay, final Runnable runnable) {
        Timer timer = mTimerMap.get(tag);
        if (timer == null) {
            timer = new Timer(tag);
            mTimerMap.put(tag, timer);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
    }

    public void cancelWorkTimer(String tag) {
        Timer timer = mTimerMap.get(tag);
        if (timer != null) {
            timer.cancel();
        }
    }

    public void cancelAll() {
        Iterator<Map.Entry<String, Timer>> iterator = mTimerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Timer> entry = iterator.next();
            entry.getValue().cancel();
        }
    }

    public void clear(String tag) {
        cancelWorkTimer(tag);
        mTimerMap.remove(tag);
    }

    public void clear() {
        cancelAll();
        mTimerMap.clear();
    }
}
