package com.pine.template.main.mqtt;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;

import com.pine.app.lib.mqtt.framework.MqttManager;
import com.pine.app.lib.mqtt.framework.listener.MqttRespond;
import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.manager.StorageManager;
import com.pine.template.main.mqtt.mode.KeepAliveMode;
import com.pine.template.main.track.TrackRecordHelper;
import com.pine.tool.service.TimerHelper;
import com.pine.tool.util.LogUtils;

public class MqttClient {
    private final String TAG = this.getClass().getSimpleName();
    private static MqttClient instance;
    private boolean FUN_MQTT = false;

    private MqttActionRequest mRequest;
    private Handler mCheckHandler = new Handler(Looper.getMainLooper());

    private Context mContext;

    private MqttClient() {
        FUN_MQTT = ConfigSwitcherServer.isEnable(BuildConfigKey.FUN_MQTT)
                && ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_MQTT);
        mRequest = MqttActionRequest.getInstance();
    }

    public static synchronized MqttClient getInstance() {
        if (instance == null) {
            instance = new MqttClient();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        MqttManager.getInstance().enable(FUN_MQTT);
        MqttManager.getInstance().init(mContext, MqttConfigBuilder.buildConfig(),
                null, new MqttManager.IServiceListener() {
                    @Override
                    public void onMqttServerInit() {
                        MqttManager.getInstance().registerActionAcceptor(new MqttActionAcceptor());
                        mLastKeepLive = System.currentTimeMillis();
                        startKeepAliveTimer();
                        scheduleCheckState(75 * 1000, 5 * 60 * 1000);
                    }

                    @Override
                    public void onMqttServerRelease() {
                        mCheckHandler.removeCallbacksAndMessages(null);
                        recordKeepLive();
                        stopKeepAliveTimer();
                    }
                });
    }

    public void release() {
        MqttManager.getInstance().release();
    }

    public boolean isPrepared() {
        return MqttManager.getInstance().isMqttConnected();
    }

    public boolean isPrepared(MqttRespond.IMqttRespond callback) {
        return MqttManager.getInstance().isMqttConnected(callback);
    }

    private long mLastKeepLive;
    private int mNotPrepareCount, mCheckCount;
    private int mKeepLiveSend, mKeepLiveReply;

    private void scheduleCheckState(long delay, long period) {
        mCheckHandler.removeCallbacksAndMessages(null);
        mCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scheduleCheckState(period, period);
                mCheckCount++;
                if (System.currentTimeMillis() - mLastKeepLive > 10 * 60 * 1000) {
                    LogUtils.w(TAG, "scheduleCheckState not run keep live more than 10 minus , mqtt prepared:" + isPrepared());
                    if (isPrepared()) {
                        mLastKeepLive = System.currentTimeMillis();
                        startKeepAliveTimer();
                    }
                }
                if (isPrepared()) {
                    mNotPrepareCount = 0;
                } else {
                    mNotPrepareCount++;
                    if (mNotPrepareCount >= 2) {
                        LogUtils.w(TAG, "scheduleCheckState mqtt is not prepare more than 2 time");
                        TrackRecordHelper.getInstance().recordInfoMqttKeepLiveFail("Mqtt未准备就绪");
                    }
                }
                if (mCheckCount % 12 == 1) {
                    recordKeepLive();
                }
            }
        }, delay);
    }

    private void recordKeepLive() {
        if (mKeepLiveSend == 0) {
            return;
        }
        TrackRecordHelper.getInstance().recordInfoMqttKeepLive(
                String.valueOf(mKeepLiveSend), String.valueOf(mKeepLiveReply));
        mKeepLiveSend = 0;
        mKeepLiveReply = 0;
    }

    public void startKeepAliveTimer() {
        stopKeepAliveTimer();
        LogUtils.d(TAG, "startKeepAliveTimer");
        long ttl = ConfigSwitcherServer.getConfigInt(BuildConfigKey.CACHE_MQTT_TTL_INTERVAL, 30);
        TimerHelper.schemeTimerWork(RequestAction.A_KEEP_LIVE, ttl * 500, ttl * 1000, new Runnable() {
            @Override
            public void run() {
                mLastKeepLive = System.currentTimeMillis();
                try {
                    if (isPrepared()) {
                        KeepAliveMode mode = new KeepAliveMode();
                        mKeepLiveSend++;
                        mRequest.keepLive(TAG, mode, new MqttRespond.IMqttRespond<KeepAliveMode>() {
                            @Override
                            public void onReply(KeepAliveMode data) {
                                mKeepLiveReply++;
                                LogUtils.d(TAG, "keepLive onReply:" + data);
                            }

                            @Override
                            public void onFail(int errCode) {
                                LogUtils.d(TAG, "keepLive onFail errCode:" + errCode);
                            }
                        });
                    } else {
                        LogUtils.d(TAG, "keepLive onFail mqtt not prepared");
                    }

                    ActivityManager.MemoryInfo memoryInfo = StorageManager.getInstance().getMemoryInfo();
                    StorageManager.getInstance().checkDisk();
                    LogUtils.d(TAG, "系统总共内存:" + Formatter.formatFileSize(mContext, memoryInfo.totalMem)
                            + "，系统剩余内存:" + Formatter.formatFileSize(mContext, memoryInfo.availMem)
                            + "，系统低内存阈值:" + Formatter.formatFileSize(mContext, memoryInfo.threshold)
                            + "，系统是否处于低内存运行：" + memoryInfo.lowMemory);
                } catch (Exception e) {
                }
            }
        });
    }

    public void stopKeepAliveTimer() {
        LogUtils.d(TAG, "stopKeepAliveTimer");
        TimerHelper.cancel(RequestAction.A_KEEP_LIVE);
    }

    /////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MQTT请求///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
}
