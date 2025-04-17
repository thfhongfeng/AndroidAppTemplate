package com.pine.template.base.bgwork;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pine.tool.service.TimerHelper;
import com.pine.tool.util.AppUtils;

import java.util.HashMap;

public class BgWorkManager {
    private final String TAG = this.getClass().getSimpleName();

    private static BgWorkManager instance;

    public static synchronized BgWorkManager getInstance() {
        if (instance == null) {
            instance = new BgWorkManager();
        }
        return instance;
    }

    private Context mContext;
    private Gson sGson = new GsonBuilder().disableHtmlEscaping().create();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private BgWorkManager() {
        mContext = AppUtils.getApplicationContext();
    }

    public void release() {
        mMainHandler.removeCallbacksAndMessages(null);
        mBgWorkListenerMap.clear();
    }

    public void init() {
        TimerHelper.schemeTimerWork(TAG, 0, 1000, new Runnable() {
            @Override
            public void run() {
                long now = SystemClock.uptimeMillis();
                for (String key : mTimeTickHolderMap.keySet()) {
                    TimeTickHolder holder = mTimeTickHolderMap.get(key);
                    if (now - holder.getLastTickTime() >= holder.getTickSecondInterval() * 1000 - 10) {
                        holder.setLastTickTime(now);
                        holder.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                holder.getListener().onTick();
                            }
                        });
                    }
                }
            }
        });
    }

    private HashMap<String, TimeTickHolder> mTimeTickHolderMap = new HashMap<>();

    public synchronized void listenTimeTickImpl(String tag, TimeTickHolder listener) {
        mTimeTickHolderMap.put(tag, listener);
    }

    public synchronized void unListenTimeTickImpl(String tag) {
        if (mTimeTickHolderMap.containsKey(tag)) {
            TimeTickHolder holder = mTimeTickHolderMap.remove(tag);
            holder.getHandler().removeCallbacksAndMessages(null);
        }
    }

    private HashMap<String, IBgWorkListener> mBgWorkListenerMap = new HashMap<>();

    public synchronized <T> void listenBgWorkImpl(String tag, IBgWorkListener<T> listener) {
        mBgWorkListenerMap.put(tag, listener);
    }

    public synchronized void unListenBgWorkImpl(String tag) {
        if (mBgWorkListenerMap.containsKey(tag)) {
            mBgWorkListenerMap.remove(tag);
        }
    }

    /////////////////////////////////////////////////////////////////////////

    public <T> void sendEventActionImpl(String actionType, T data) {
        for (String key : mBgWorkListenerMap.keySet()) {
            IBgWorkListener<T> listener = mBgWorkListenerMap.get(key);
            if (listener != null) {
                listener.onBgWork(actionType, data);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////

    public static void listenTimeTick(String tag, TimeTickHolder listener) {
        getInstance().listenTimeTickImpl(tag, listener);
    }

    public static void unListenTimeTick(String tag) {
        getInstance().unListenTimeTickImpl(tag);
    }

    public static void listenBgWork(String tag, IBgWorkListener listener) {
        getInstance().listenBgWorkImpl(tag, listener);
    }

    public static void unListenBgWork(String tag) {
        getInstance().unListenBgWorkImpl(tag);
    }

    public static <T> void sendBgAction(String actionType, T data) {
        getInstance().sendEventActionImpl(actionType, data);
    }
}