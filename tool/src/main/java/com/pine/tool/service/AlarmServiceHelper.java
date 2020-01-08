package com.pine.tool.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 后台定时服务协助类
 */
public class AlarmServiceHelper {
    private static AlarmServiceHelper mInstance;

    private AlarmServiceHelper() {

    }

    public synchronized static AlarmServiceHelper getInstance() {
        if (mInstance == null) {
            mInstance = new AlarmServiceHelper();
        }
        return mInstance;
    }

    /**
     * 同步定时任务
     *
     * @param context
     * @param persistent 是否每隔delay时间启动一次任务(单次任务还是循环任务)
     * @param delay      定时时间，单位毫秒，最少5秒
     * @param worker     定时runnable, 不能是内部类
     */
    public void schemeAlarmWork(Context context, boolean persistent, long delay, AlarmWorker worker) {
        Intent intent = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("startImmediately", false);
        bundle.putBoolean("async", false);
        bundle.putBoolean("persistent", persistent);
        bundle.putLong("delay", delay);
        bundle.putSerializable("worker", worker);
        intent.putExtra("bundle", bundle);
        context.startService(intent);
    }

    /**
     * 异步定时任务
     *
     * @param context
     * @param persistent 是否每隔delay时间启动一次任务(单次任务还是循环任务)
     * @param delay      定时时间，单位毫秒，最少5秒
     * @param worker     定时runnable, 不能是内部类
     */
    public void schemeAsyncAlarmWork(Context context, boolean persistent, long delay, AlarmWorker worker) {
        Intent intent = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("startImmediately", false);
        bundle.putBoolean("async", true);
        bundle.putBoolean("persistent", persistent);
        bundle.putLong("delay", delay);
        bundle.putSerializable("worker", worker);
        intent.putExtra("bundle", bundle);
        context.startService(intent);
    }
}
