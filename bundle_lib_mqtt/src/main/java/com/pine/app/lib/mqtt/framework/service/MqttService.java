package com.pine.app.lib.mqtt.framework.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.pine.app.lib.mqtt.framework.MqttConfig;
import com.pine.app.lib.mqtt.framework.MqttHelper;
import com.pine.app.lib.mqtt.framework.SubscribeInfo;
import com.pine.app.lib.mqtt.framework.Topic;
import com.pine.app.lib.mqtt.framework.TopicRuleManager;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;
import com.pine.tool.util.RandomUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MqttService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    public static final String INTENT_CONFIG_DATA_KEY = "config";

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions connectOptions;

    private MqttBinder mBinder;
    private Handler mConnectHandler, mCheckHandler;
    private MqttHelper mWorker;
    private static IConnectionCallback mConnectCallback;

    private MqttConfig mConfig;

    private final boolean mAutoReconnectBySdk = true;

    // 以防mqtt sdk自动重连机制失效，建立检查连接机制
    private final long CHECK_START_DELAY = 2 * 60;     //单位:秒
    private final long CHECK_INTERVAL = 1 * 60;    //单位:秒
    private final int MAX_IDLE_COUNT = 9;   //检查连续指定次以上都发现没有连接，则认为连接出问题，重新建立mqtt连接。
    private ScheduledExecutorService mCheckMqttAliveExecutor;
    private int mCheckMqttIdleCount;
    private volatile boolean mMqttReleaseFlag;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate");

        mConnectHandler = new Handler(Looper.getMainLooper());
        mCheckHandler = new Handler(Looper.getMainLooper());

        // 以防mqtt sdk自动重连机制失效，建立检查连接机制
        scheduleConnectCheck();

        if (mConnectCallback != null) {
            mConnectCallback.onServerState(IConnectionCallback.STATE_ON_CREATE);
        }
    }

    private void scheduleConnectCheck() {
        mMqttReleaseFlag = false;
        if (mCheckMqttAliveExecutor != null) {
            try {
                mCheckMqttAliveExecutor.shutdownNow();
                LogUtils.w(TAG, "scheduleConnectCheck MqttClient check connect executor not null" +
                        ", shutdownNow and prepare to start new");
            } catch (Exception e) {
                LogUtils.e(TAG, "scheduleConnectCheck MqttClient check connect executor not null, shutdownNow err:" + e);
            }
        }
        LogUtils.d(TAG, "scheduleConnectCheck MqttClient check connect schedule start");
        mCheckMqttAliveExecutor = Executors.newSingleThreadScheduledExecutor();
        mCheckMqttAliveExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mCheckHandler.removeCallbacksAndMessages(null);
                mCheckHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMqttReleaseFlag) {
                            LogUtils.w(TAG, "MqttClient check connect state -> release flag is true, ignore");
                            return;
                        }
                        if (!NetWorkUtils.checkNetWork()) {
                            LogUtils.w(TAG, "MqttClient check connect state -> net is disconnected, ignore");
                            mCheckMqttIdleCount = 0;
                            return;
                        }
                        if (mqttClient == null || !mqttClient.isConnected()) {
                            mCheckMqttIdleCount++;
                            LogUtils.d(TAG, "MqttClient check connect state -> disconnected idle count:"
                                    + mCheckMqttIdleCount);
                        } else {
                            mCheckMqttIdleCount = 0;
                            LogUtils.d(TAG, "MqttClient check connect state -> connected");
                        }
                        if (mCheckMqttIdleCount > MAX_IDLE_COUNT) {
                            LogUtils.w(TAG, "MqttClient check connect state -> disconnected more than "
                                    + MAX_IDLE_COUNT + " times, connect it by checkExecutor");
                            init();
                            connect(false);
                            mCheckMqttIdleCount = 0;
                            if (mConnectCallback != null) {
                                mConnectCallback.reConnectByChecker();
                            }
                        }
                    }
                });
            }
        }, CHECK_START_DELAY, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        mConfig = (MqttConfig) intent.getSerializableExtra(INTENT_CONFIG_DATA_KEY);
        mBinder = new MqttBinder();

        init();

        if (mConnectCallback != null) {
            mConnectCallback.onServerState(IConnectionCallback.STATE_ON_BIND);
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d(TAG, "onUnbind");
        if (mConnectCallback != null) {
            mConnectCallback.onServerState(IConnectionCallback.STATE_ON_UNBIND);
        }
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        if (mConnectCallback != null) {
            mConnectCallback.onServerState(IConnectionCallback.STATE_ON_START_COMMAND);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        if (mConnectCallback != null) {
            mConnectCallback.onServerState(IConnectionCallback.STATE_ON_DESTROY);
        }
        mMqttReleaseFlag = true;
        if (mCheckMqttAliveExecutor != null) {
            mCheckMqttAliveExecutor.shutdownNow();
            mCheckMqttAliveExecutor = null;
        }
        release();
        super.onDestroy();
    }

    private void release() {
        if (mWorker != null) {
            mWorker.release();
            mWorker = null;
        }
        releaseMqtt();
    }

    private void releaseMqtt() {
        mConnectHandler.removeCallbacksAndMessages(null);
        mCheckHandler.removeCallbacksAndMessages(null);
        mIsConnectOping = false;
        if (mqttClient != null) {
            LogUtils.d(TAG, "release mqttClient");
            mqttClient.unregisterResources();
            mqttClient.close();
            mqttClient = null;
        }
    }

    private void init() {
        LogUtils.d(TAG, "Mqtt service init...");
        if (mConfig == null) {
            LogUtils.w(TAG, "MqttClient connect fail for config is null");
            return;
        }
        if (mqttClient != null) {
            LogUtils.d(TAG, "connect mqtt client is not null");
            if (mqttClient.isConnected()) {
                LogUtils.d(TAG, "connect mqtt client is already connected, return");
                return;
            }
            LogUtils.w(TAG, "connect mqtt client for client state abnormal, close first");
            releaseMqtt();
        } else {
            String host = mConfig.getHost();
            String serverURI = host;
            String clientId = mConfig.getMyId() + "_" + RandomUtils.getRandom(RandomUtils.NUMBERS, 8);
            LogUtils.d(TAG, "Mqtt init:" + mConfig);
            mqttClient = new MqttAndroidClient(getApplicationContext(), serverURI, clientId);
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    LogUtils.d(TAG, "(callback) Mqtt connect complete, reconnect:" + reconnect);
                    onConnectSuccess();
                }

                @Override
                public void connectionLost(Throwable cause) {
                    LogUtils.d(TAG, "(callback) Connection lost " + cause
                            + " auto reconnect by mqtt sdk:" + mAutoReconnectBySdk);
                    onConnectFail(cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    LogUtils.i(TAG, "messageArrived topic: " + topic);
                    try {
                        Topic scTopic = TopicRuleManager.parseTopic(mConfig.getTopicHead(), topic);
                        String data = "";
                        if (message != null) {
                            data = new String(message.getPayload(), StandardCharsets.UTF_8);
                        }
                        LogUtils.i(TAG, "messageArrived message: "
                                + (data.length() > 800 ? data.substring(0, 794) + "......" : data));
                        if (scTopic != null && scTopic.isValid) {
                            try {
                                if (scTopic.isRequestTopic()) {
                                    if (mWorker != null) {
                                        mWorker.onRequestMsg(scTopic, data);
                                    }
                                } else if (scTopic.isReplyTopic()) {
                                    if (mWorker != null) {
                                        mWorker.onReplyMsg(scTopic, data);
                                    }
                                }
                            } catch (Exception e) {
                                LogUtils.e(TAG, "onMsgArrived err:" + e);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.d(TAG, "Receive message exception:" + e);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LogUtils.d(TAG, "deliveryComplete");
                }
            });
            LogUtils.d(TAG, "mqttClient is created");
        }
        connectOptions = new MqttConnectOptions();
        connectOptions.setAutomaticReconnect(mAutoReconnectBySdk);
        connectOptions.setCleanSession(true);
        connectOptions.setConnectionTimeout(mConfig.getConnectionTimeout());
        connectOptions.setKeepAliveInterval(mConfig.getKeepAliveInterval());
        connectOptions.setUserName(mConfig.getUsername());
        connectOptions.setPassword(mConfig.getPwd().toCharArray());
    }

    public boolean isMqttConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private void onConnectSuccess() {
        mIsConnectOping = false;
        subscribeTopic();
        if (mConnectCallback != null) {
            mConnectCallback.onConnected();
        }
    }

    private void onConnectFail(Throwable cause) {
        if (mConnectCallback != null) {
            mConnectCallback.onDisconnect(NetWorkUtils.checkNetWork() ? IConnectionCallback.REASON_CONNECT_FAIL
                    : IConnectionCallback.REASON_NET_DISCONNECT, cause);
        }
        mIsConnectOping = false;
        // 根据mqtt是否设置重连机制来决定是否需要主动重连
        if (!mAutoReconnectBySdk) {
            LogUtils.d(TAG, "schedule reconnect after 10 second for device_sdk not auto mode");
            //连接失败后延迟10秒重新连接
            mConnectHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect(false);
                }
            }, 10000);
        }
    }

    private volatile boolean mIsConnectOping;
    private volatile boolean mUserConnect;

    private synchronized void connect(boolean byUser) {
        if (byUser) {
            mUserConnect = true;
        }
        if (mqttClient == null) {
            return;
        }
        if (mIsConnectOping) {
            return;
        }
        try {
            if (mqttClient.isConnected()) {
                LogUtils.d(TAG, "Client is connected, can't connect repeat.");
                return;
            }
            LogUtils.d(TAG, "Connect start");
            mIsConnectOping = true;
            if (mConnectCallback != null) {
                mConnectCallback.onConnecting();
            }
            mConnectHandler.removeCallbacksAndMessages(null);
            mqttClient.connect(connectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.d(TAG, "(connect) Connection success");
                    // MqttCallbackExtended回调中处理，这里不用再处理
//                    onConnectSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    LogUtils.d(TAG, "(connect) Connection failure " + e
                            + " auto reconnect by mqtt sdk:" + mAutoReconnectBySdk);
                    // MqttCallbackExtended回调中处理，这里不用再处理
//                    onConnectFail();
                }
            });
        } catch (MqttException e) {
            LogUtils.e(TAG, "MqttException:" + e);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception:" + e);
        }
    }

    private synchronized void disconnect(boolean byUser) {
        if (byUser) {
            mUserConnect = false;
        }
        if (mqttClient == null) {
            return;
        }
        try {
            mConnectHandler.removeCallbacksAndMessages(null);
            mqttClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mIsConnectOping = false;
                    LogUtils.d(TAG, "(disconnect) Success Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mIsConnectOping = false;
                    LogUtils.d(TAG, "(disconnect) Failed to disconnect" + ", exception:" + exception);
                }
            });
        } catch (MqttException e) {
            LogUtils.e(TAG, "MqttException:" + e);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception:" + e);
        }
    }

    private void subscribeTopic() {
        SubscribeInfo subscribe = mConfig.getSubscribeTopics();
        LogUtils.d(TAG, "subscribeTopic:" + subscribe);
        if (subscribe != null && subscribe.isValid()) {
            subscribe(subscribe.topics, subscribe.qos);
        }
    }

    private synchronized void subscribe(final String[] topic, final int[] qos) {
        if (mqttClient == null) {
            return;
        }
        try {
            mqttClient.subscribe(topic, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.d(TAG, "(subscribe) Success Subscribed to " + Arrays.asList(topic));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.d(TAG, "(subscribe) Failed to subscribe " + Arrays.asList(topic)
                            + ", exception:" + exception);
                }
            });
            return;
        } catch (MqttException e) {
            LogUtils.e(TAG, "MqttException:" + e);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception:" + e);
        }
    }

    private synchronized void unsubscribe(final String topic) {
        if (mqttClient == null) {
            return;
        }
        try {
            mqttClient.unsubscribe(topic, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.d(TAG, "(unsubscribe) Success unsubscribe to " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.d(TAG, "(unsubscribe) Failed to unsubscribe " + topic + ", exception:" + exception);
                }
            });
        } catch (MqttException e) {
            LogUtils.e(TAG, "MqttException:" + e);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception:" + e);
        }
    }

    private synchronized void publish(final String topic, final String msg, int qos, boolean retained) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes(StandardCharsets.UTF_8));
            message.setQos(qos);
            message.setRetained(retained);
            mqttClient.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.d(TAG, "(publish) Success publish to " + topic + ", msg:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.d(TAG, "(publish) Failed to publish " + topic + ", exception:" + exception);
                }
            });
        } catch (MqttException e) {
            LogUtils.e(TAG, "MqttException:" + e);
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception:" + e);
        }
    }

    public static void setConnectListener(IConnectionCallback callback) {
        mConnectCallback = callback;
    }

    public class MqttBinder extends Binder implements IMqttBinder {

        @Override
        public void setWorker(MqttHelper worker) {
            mWorker = worker;
            mWorker.init(MqttService.this, mBinder, mConfig);
        }

        @Override
        public boolean isMqttConnected() {
            return MqttService.this.isMqttConnected();
        }

        @Override
        public void connect() {
            MqttService.this.connect(true);
        }

        @Override
        public void disconnect() {
            MqttService.this.disconnect(true);
        }

        @Override
        public void publish(String topic, String msg, int qos, boolean retained) {
            MqttService.this.publish(topic, msg, qos, retained);
        }

        @Override
        public void unsubscribe(String topic) {
            MqttService.this.unsubscribe(topic);
        }

        @Override
        public void subscribe(String[] topic, int[] qos) {
            MqttService.this.subscribe(topic, qos);
        }
    }

    public interface IMqttBinder {
        void setWorker(MqttHelper worker);

        boolean isMqttConnected();

        void connect();

        void disconnect();

        void publish(String topic, String msg, int qos, boolean retained);

        void unsubscribe(String topic);

        void subscribe(String[] topic, int[] qos);
    }

    public interface IConnectionCallback {
        int REASON_DEFAULT = 0;
        int REASON_EXCEPTION = 1;
        int REASON_FUN_NOT_ENABLE = 2;
        int REASON_NOT_INIT = 3;
        int REASON_CONNECT_FAIL = 4;
        int REASON_NET_DISCONNECT = 5;
        int REASON_SUBSCRIBE_FAIL = 6;
        int REASON_RELEASE = 7;

        String STATE_ON_CREATE = "onCreate";
        String STATE_ON_BIND = "onBind";
        String STATE_ON_START_COMMAND = "onStartCommand";
        String STATE_ON_UNBIND = "onUnbind";
        String STATE_ON_DESTROY = "onDestroy";

        void onConnected();

        void onConnecting();

        void onDisconnect(int errCode, Throwable cause);

        void reConnectByChecker();

        void onServerState(String state);
    }
}
