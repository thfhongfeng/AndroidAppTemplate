package com.pine.template.base.bgwork.checker;

import android.os.Handler;
import android.os.Looper;

import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BgCheckManager {
    private final String TAG = this.getClass().getSimpleName();

    private static BgCheckManager instance;

    public static synchronized BgCheckManager getInstance() {
        if (instance == null) {
            instance = new BgCheckManager();
        }
        return instance;
    }

    private BgCheckManager() {

    }

    public static final boolean ENABLE_BG_CHECKER = false;

    // 建立网络连接检查机制
    private final long CHECK_START_DELAY = 2 * 60;     //单位:秒
    private final long CHECK_INTERVAL = 1 * 60;    //单位:秒
    private ScheduledExecutorService mCheckAliveExecutor;
    private volatile boolean mCheckReleaseFlag;

    public void releaseChecker() {
        LogUtils.d(TAG, "scheduleChecker release");
        mCheckNetListenerMap.clear();
        mCheckReleaseFlag = true;
        if (mCheckAliveExecutor != null) {
            mCheckAliveExecutor.shutdownNow();
            mCheckAliveExecutor = null;
        }
    }

    public void scheduleChecker() {
        if (!ENABLE_BG_CHECKER) {
            return;
        }
        mCheckReleaseFlag = false;
        if (mCheckAliveExecutor != null) {
            try {
                mCheckAliveExecutor.shutdownNow();
                LogUtils.w(TAG, "scheduleChecker executor not null" +
                        ", shutdownNow and prepare to start new");
            } catch (Exception e) {
                LogUtils.e(TAG, "scheduleChecker executor not null, shutdownNow err:" + e);
            }
        }
        LogUtils.d(TAG, "scheduleChecker schedule start");
        mCheckAliveExecutor = Executors.newSingleThreadScheduledExecutor();
        mCheckAliveExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mCheckReleaseFlag) {
                    LogUtils.w(TAG, "scheduleChecker state -> release flag is true, ignore");
                    return;
                }
//                checkNetState();
            }
        }, CHECK_START_DELAY, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    private Handler mNetHandler = new Handler(Looper.getMainLooper());
    private final int MAX_NET_IDLE_PER_COUNT = 5;   //检查连续指定次以上都发现没有连接，则认为连接出问题。
    private int mCheckNetIdleCount;
    private int mContinueDoNetJobCount;

    // rk3568系统版本大于等于1.2.1.000才支持开关飞行模式，低于该版本，不要开启该功能
    private void checkNetState() {
        mNetHandler.removeCallbacksAndMessages(null);
        mNetHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean checkPass = NetWorkUtils.checkNetWork();
                if (checkPass) {
                    mCheckNetIdleCount = 0;
                    mContinueDoNetJobCount = 0;
                    Set<String> keys = mCheckNetListenerMap.keySet();
                    for (String key : keys) {
                        mCheckNetListenerMap.get(key).onNetCheckConnected();
                    }
                    return;
                }
                Set<String> keys = mCheckNetListenerMap.keySet();
                for (String key : keys) {
                    mCheckNetListenerMap.get(key).onNetCheckDisconnected();
                }
                LogUtils.w(TAG, "scheduleChecker net state -> net is disconnected");
                mCheckNetIdleCount++;
                int curOutCount = MAX_NET_IDLE_PER_COUNT * (mContinueDoNetJobCount % 10 + 1);
                if (mCheckNetIdleCount > curOutCount) {
                    LogUtils.w(TAG, "scheduleChecker net connect state -> disconnected more than "
                            + curOutCount + " times, do net job now");
                    for (String key : keys) {
                        mCheckNetListenerMap.get(key).doNetPersistDisconnected(mCheckNetIdleCount);
                    }
                    mContinueDoNetJobCount++;
                    mCheckNetIdleCount = 0;
                }
            }
        });
    }

    private ConcurrentHashMap<String, ICheckNetState> mCheckNetListenerMap = new ConcurrentHashMap<>();

    public void addCheckNetListener(String tag, ICheckNetState listener) {
        if (listener == null) {
            return;
        }
        mCheckNetListenerMap.put(tag, listener);
    }

    public void removeCheckNetListener(String tag) {
        mCheckNetListenerMap.remove(tag);
    }

    public interface ICheckNetState {
        void onNetCheckConnected();

        void onNetCheckDisconnected();

        void doNetPersistDisconnected(int checkIdleCount);
    }
}
