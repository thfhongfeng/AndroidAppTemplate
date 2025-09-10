package com.pine.app.lib.mqtt.framework;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.lib.mqtt.framework.listener.MqttRespond;
import com.pine.app.lib.mqtt.framework.service.MqttService;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;

import java.util.concurrent.ConcurrentHashMap;

public class MqttManager {
    protected final String TAG = this.getClass().getSimpleName();

    private static MqttManager instance;

    private MqttManager() {
        mWorker = MqttHelper.getInstance();
    }

    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }

    private boolean ENABLE = true;

    public void enable(boolean enable) {
        ENABLE = enable;
    }

    public boolean isMqttEnable() {
        return ENABLE;
    }


    protected Context mContext;
    private ConcurrentHashMap<String, MqttService.IConnectionCallback> mConnectCbMap = new ConcurrentHashMap<>();
    private IInitCallback mInitCallback;

    private MqttService.IMqttBinder mMqttBinder;
    protected MqttConfig mConfig;
    protected MqttHelper mWorker;
    private volatile boolean mIsInitProcessing;

    private AcceptActionRegistry mAcceptActionRegistry = new AcceptActionRegistry();

    public synchronized void init(Context context, @NonNull MqttConfig config) {
        init(context, config, null, null);
    }

    public synchronized void init(Context context, @NonNull MqttConfig config,
                                  IInitCallback callback, IServiceListener listener) {
        mConfig = config;
        mAcceptActionRegistry.setConfig(config);
        mInitCallback = callback;
        if (!isMqttEnable()) {
            if (callback != null) {
                callback.onFail(IInitCallback.ERR_FUN_NOT_ENABLE);
            }
            LogUtils.w(TAG, "init => not enable");
            return;
        }
        if (isInit()) {
            if (callback != null) {
                callback.onInit();
            }
            LogUtils.w(TAG, "init => already init");
            return;
        }
        if (mIsInitProcessing) {
            LogUtils.w(TAG, "init => init is processing");
            return;
        }
        LogUtils.d(TAG, "init");
        mContext = context;
        mReleaseFlag = false;
        mIsInitProcessing = true;

        MqttService.setConnectListener(new MqttService.IConnectionCallback() {
            @Override
            public void onConnected() {
                for (MqttService.IConnectionCallback callback : mConnectCbMap.values()) {
                    if (callback != null) {
                        callback.onConnected();
                    }
                }
            }

            @Override
            public void onConnecting() {
                for (MqttService.IConnectionCallback callback : mConnectCbMap.values()) {
                    if (callback != null) {
                        callback.onConnecting();
                    }
                }
            }

            @Override
            public void onDisconnect(int reason, Throwable cause) {
                for (MqttService.IConnectionCallback callback : mConnectCbMap.values()) {
                    if (callback != null) {
                        callback.onDisconnect(reason, cause);
                    }
                }
            }

            @Override
            public void reConnectByChecker() {
                for (MqttService.IConnectionCallback callback : mConnectCbMap.values()) {
                    if (callback != null) {
                        callback.reConnectByChecker();
                    }
                }
            }

            @Override
            public void onServerState(String state) {
                mIsInitProcessing = false;
                if (TextUtils.equals(state, STATE_ON_CREATE)) {
                    if (mInitCallback != null) {
                        mInitCallback.onInit();
                    }
                    if (listener != null) {
                        listener.onMqttServerInit();
                    }
                } else if (TextUtils.equals(state, STATE_ON_DESTROY)) {
                    if (listener != null) {
                        listener.onMqttServerRelease();
                    }
                    if (!mReleaseFlag) {
                        bindMqttService();
                    }
                }
                for (MqttService.IConnectionCallback callback : mConnectCbMap.values()) {
                    if (callback != null) {
                        callback.onServerState(state);
                    }
                }
            }
        });

        //绑定Mqtt Service
        bindMqttService();
    }

    public void addConnectListener(String tag, MqttService.IConnectionCallback callback) {
        mConnectCbMap.put(tag, callback);
    }

    public void removeConnectListener(String tag) {
        mConnectCbMap.remove(tag);
    }

    private boolean mReleaseFlag;

    public synchronized void release() {
        mReleaseFlag = true;
        if (!isMqttEnable()) {
            LogUtils.w(TAG, "init => not enable");
            return;
        }
        LogUtils.d(TAG, "release");
        clearAllResponseCb();
        if (mContext != null) {
            try {
                mContext.unbindService(mMqttConnection);
            } catch (Exception e) {
            }
        }
        mMqttBinder = null;
        mIsInitProcessing = false;
        mInitCallback = null;
        mConnectCbMap.clear();
        mConfig = null;
        mWorker = null;
    }

    public synchronized void registerActionAcceptor(Object actionAcceptor) {
        if (ENABLE) {
            mAcceptActionRegistry.register(actionAcceptor);
        }
    }

    public synchronized void restart() {
        if (!isMqttEnable()) {
            return;
        }
        release();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                init(mContext, null);
            }
        });
    }

    public synchronized boolean isInit() {
        return mContext != null && mMqttBinder != null;
    }

    public synchronized boolean isInit(MqttRespond.IMqttRespond callback) {
        boolean init = isInit();
        if (!init && callback != null) {
            callback.onFail(MqttRespond.ERR_MQTT_SERVER_NOT_INIT);
        }
        return init;
    }

    public synchronized boolean isMqttConnected() {
        return isInit() && mWorker.isMqttConnected();
    }

    public synchronized boolean isMqttConnected(MqttRespond.IMqttRespond callback) {
        if (!isInit(callback)) {
            return false;
        }
        boolean prepared = isMqttConnected();
        if (!prepared && callback != null) {
            callback.onFail(NetWorkUtils.checkNetWork()
                    ? MqttRespond.ERR_MQTT_NOT_PREPARED : MqttRespond.ERR_NO_NET);
        }
        return prepared;
    }

    public <T> void reply(Topic requestTopic, ReplyData<T> replyData) {
        reply(requestTopic, replyData, 1, false);
    }

    public <T> void reply(Topic requestTopic, ReplyData<T> replyData, int qos, boolean retained) {
        if (!isMqttConnected()) {
            return;
        }
        mWorker.reply(requestTopic, replyData, qos, retained);
    }

    public void request(String tag, String action, String targetSubject, String targetFlag,
                        MqttRespond.IMqttRespond<String> callback) {
        request(tag, action, targetSubject, targetFlag, null, callback);
    }

    public void request(String tag, String action, Topic.SubjectEnum targetSubject, String targetFlag,
                        MqttRespond.IMqttRespond<String> callback) {
        request(tag, action, targetSubject.getContent(), targetFlag, null, callback);
    }

    public <T> void request(String tag, String action, Topic.SubjectEnum targetSubject, String targetFlag, T data,
                            MqttRespond.IMqttRespond<String> callback) {
        request(tag, action, targetSubject.getContent(), targetFlag, data, callback);
    }

    public <T> void request(String tag, String action, String targetSubject, String targetFlag, T data,
                            MqttRespond.IMqttRespond<String> callback) {
        request(tag, action, targetSubject, targetFlag, data, 1, false, callback);
    }

    public <T> void request(String tag, String action, Topic.SubjectEnum targetSubject, String targetFlag,
                            T data, int qos, boolean retained, MqttRespond.IMqttRespond<String> callback) {
        request(tag, action, targetSubject.getContent(), targetFlag, data, qos, retained, callback);
    }

    public <T> void request(String tag, String action, String targetSubject, String targetFlag, T data,
                            int qos, boolean retained, MqttRespond.IMqttRespond<String> callback) {
        if (!isMqttConnected()) {
            return;
        }
        if (callback != null) {
            mWorker.addResponseCb(action, tag, callback);
        }
        mWorker.request(action, targetSubject, targetFlag, data, qos, retained);
    }

    public <T> void requestGroup(String action, String group, T data) {
        requestGroup(action, group, data, 1, false);
    }

    public <T> void requestGroup(String action, String group, T data, int qos, boolean retained) {
        mWorker.requestGroup(action, group, data, qos, retained);
    }

    public <T> void requestBroadcast(String action, T data) {
        requestBroadcast(action, data, 1, false);
    }

    public <T> void requestBroadcast(String action, T data, int qos, boolean retained) {
        mWorker.requestBroadcast(action, data, qos, retained);
    }

    private ServiceConnection mMqttConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "onServiceConnected name:" + name);
            mMqttBinder = (MqttService.IMqttBinder) service;
            mIsInitProcessing = false;
            mMqttBinder.connect();
            mMqttBinder.setWorker(mWorker);
        }

        // 当调用 unbindService 时，onServiceDisconnected 不一定会被立即调用，而是在系统认为适当的时机。
        // 系统可能会等待一段时间，直到认为没有其他组件再与服务保持连接，然后再触发 onServiceDisconnected。
        // 因此清理工作不要完全依赖在onServiceDisconnected中进行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "onServiceDisconnected name:" + name);
            mIsInitProcessing = false;
        }
    };

    private void bindMqttService() {
        Intent intent = new Intent(mContext, MqttService.class);
        intent.putExtra(MqttService.INTENT_CONFIG_DATA_KEY, mConfig);
        mContext.bindService(intent, mMqttConnection, Context.BIND_AUTO_CREATE);
    }

    public void clearResponseCb(String tag) {
        mWorker.clearResponseCb(tag);
    }

    public synchronized void clearAllResponseCb() {
        mWorker.clearAllResponseCb();
    }

    public void clearAllCb(String tag) {
        clearResponseCb(tag);
        mWorker.unListen(tag);
    }

    public interface IInitCallback {
        int ERR_DEFAULT = 0;
        int ERR_FUN_NOT_ENABLE = 1;
        int ERR_INIT_WAIT_NET_CONN = 2;

        void onInit();

        void onFail(int errCode);
    }

    public interface IServiceListener {
        void onMqttServerInit();

        void onMqttServerRelease();
    }
}
