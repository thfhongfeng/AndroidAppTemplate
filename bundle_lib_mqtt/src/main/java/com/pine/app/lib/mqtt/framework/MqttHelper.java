package com.pine.app.lib.mqtt.framework;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pine.app.lib.mqtt.framework.listener.MqttListener;
import com.pine.app.lib.mqtt.framework.listener.MqttRespond;
import com.pine.app.lib.mqtt.framework.service.MqttService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MqttHelper {
    protected final String TAG = this.getClass().getSimpleName();

    private static MqttHelper instance;

    private MqttHelper() {

    }

    public static synchronized MqttHelper getInstance() {
        if (instance == null) {
            instance = new MqttHelper();
        }
        return instance;
    }

    private final long RESPONSE_DEFAULT_TIMEOUT = 10 * 1000;

    protected Context mContext;
    protected MqttService.MqttBinder mBinder;
    protected MqttConfig mConfig;
    protected Gson sGson = new GsonBuilder().disableHtmlEscaping().create();

    protected ConcurrentHashMap<String, LinkedHashMap<String, MqttRespond>> mResponseCbMap = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, LinkedHashMap<String, MqttListener>> mListenersMap = new ConcurrentHashMap<>();
    protected Handler mResponseTimeoutH;

    public void init(Context context, MqttService.MqttBinder binder, MqttConfig config) {
        mContext = context;
        mBinder = binder;
        mConfig = config;
        mResponseTimeoutH = new Handler(Looper.getMainLooper());
    }

    public synchronized boolean isMqttConnected() {
        return mBinder != null && mBinder.isMqttConnected();
    }

    public synchronized void release() {
        mResponseTimeoutH.removeCallbacksAndMessages(null);
        mResponseCbMap.clear();
        mListenersMap.clear();
        mContext = null;
        mBinder = null;
    }

    public <T> void reply(Topic requestTopic, ReplyData<T> replyData) {
        reply(requestTopic, replyData, 1, false);
    }

    public <T> void reply(Topic requestTopic, ReplyData<T> replyData, int qos, boolean retained) {
        if (replyData != null) {
            replyData.setReplyTime(System.currentTimeMillis());
        }
        String replyTopic = TopicRuleManager.toReplyTopic(requestTopic, mConfig.getMySubject(), mConfig.getMyId());
        publish(replyTopic, sGson.toJson(replyData), qos, retained);
    }

    public void request(String action, String targetSubject, String targetFlag) {
        request(action, targetSubject, targetFlag, null);
    }

    public void request(String action, Topic.SubjectEnum targetSubject, String targetFlag) {
        request(action, targetSubject.getContent(), targetFlag, null);
    }

    public <T> void request(String action, Topic.SubjectEnum targetSubject, String targetFlag, T data) {
        request(action, targetSubject.getContent(), targetFlag, data);
    }

    public <T> void request(String action, String targetSubject, String targetFlag, T data) {
        request(action, targetSubject, targetFlag, data, 1, false);
    }

    public <T> void request(String action, Topic.SubjectEnum targetSubject, String targetFlag, T data, int qos, boolean retained) {
        request(action, targetSubject.getContent(), targetFlag, data, qos, retained);
    }

    public <T> void request(String action, String targetSubject, String targetFlag, T data, int qos, boolean retained) {
        RequestData requestData;
        if (data == null) {
            requestData = RequestData.build(mConfig.getBaseParams());
        } else {
            requestData = RequestData.build(data, mConfig.getBaseParams());
        }
        requestData.setRequestTime(System.currentTimeMillis());
        String topic = TopicRuleManager.buildTopicRequest(mConfig.getTopicHead(), mConfig.getMySubject(), mConfig.getMyId(), action, targetSubject, targetFlag);
        publish(topic, sGson.toJson(requestData), qos, retained);
    }

    public <T> void requestGroup(String action, String group, T data) {
        requestGroup(action, group, data, 1, false);
    }

    public <T> void requestGroup(String action, String group, T data, int qos, boolean retained) {
        RequestData requestData;
        if (data == null) {
            requestData = RequestData.build(mConfig.getBaseParams());
        } else {
            requestData = RequestData.build(data, mConfig.getBaseParams());
        }
        requestData.setRequestTime(System.currentTimeMillis());
        String topic = TopicRuleManager.buildGroupRequest(mConfig.getTopicHead(), mConfig.getMySubject(), mConfig.getMyId(), group, action);
        publish(topic, sGson.toJson(requestData), qos, retained);
    }

    public <T> void requestBroadcast(String action, T data) {
        requestBroadcast(action, data, 1, false);
    }

    public <T> void requestBroadcast(String action, T data, int qos, boolean retained) {
        RequestData requestData;
        if (data == null) {
            requestData = RequestData.build(mConfig.getBaseParams());
        } else {
            requestData = RequestData.build(data, mConfig.getBaseParams());
        }
        requestData.setRequestTime(System.currentTimeMillis());
        String topic = TopicRuleManager.buildBroadcastRequest(mConfig.getTopicHead(), mConfig.getMySubject(), mConfig.getMyId(), action);
        publish(topic, sGson.toJson(requestData), qos, retained);
    }

    public synchronized void addResponseCb(@NonNull String action, @NonNull String tag, MqttRespond.IMqttRespond<String> callback) {
        addResponseCb(action, tag, 0, callback);
    }

    public synchronized void addResponseCb(@NonNull String action, @NonNull String tag, int timeout, MqttRespond.IMqttRespond<String> callback) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(tag) || callback == null) {
            return;
        }
        LinkedHashMap<String, MqttRespond> map = mResponseCbMap.get(action);
        if (map == null) {
            map = new LinkedHashMap<>();
            mResponseCbMap.put(action, map);
        }
        final MqttRespond cb = new MqttRespond(action, tag, callback, System.currentTimeMillis());
        cb.timeOut = timeout;
        long delay = timeout > 0 ? timeout * 1000L : RESPONSE_DEFAULT_TIMEOUT;
        map.put(tag, cb);
        mResponseTimeoutH.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkResponseTimeout(cb);
            }
        }, delay);
    }

    private synchronized void checkResponseTimeout(MqttRespond cb) {
        if (cb == null || !cb.isValid()) {
            return;
        }
        LinkedHashMap<String, MqttRespond> map = mResponseCbMap.get(cb.action);
        if (map == null || !map.containsKey(cb.tag)) {
            return;
        }
        map.remove(cb.tag);
        if (cb.callback != null) {
            cb.callback.onFail(MqttRespond.ERR_TIMEOUT);
        }
    }

    public synchronized void clearResponseCb(@NonNull String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Set<String> actions = mResponseCbMap.keySet();
        for (String action : actions) {
            LinkedHashMap<String, MqttRespond> map = mResponseCbMap.get(action);
            if (map != null) {
                map.remove(tag);
            }
        }
    }

    public synchronized void clearAllResponseCb() {
        mResponseCbMap.clear();
    }

    public synchronized boolean listen(@NonNull String tag, String action, MqttListener.IMqttListener<String> listener) {
        return listen(tag, action, true, listener);
    }

    public synchronized boolean listen(@NonNull String tag, String action, boolean persist, MqttListener.IMqttListener<String> listener) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(tag)) {
            return false;
        }
        LinkedHashMap<String, MqttListener> map = mListenersMap.get(action);
        if (map == null) {
            map = new LinkedHashMap<>();
            mListenersMap.put(action, map);
        }
        MqttListener bean = new MqttListener(tag, listener, System.currentTimeMillis());
        bean.persist = persist;
        map.put(tag, bean);
        return true;
    }

    public synchronized void unListen(@NonNull String tag, String action) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(action)) {
            return;
        }
        LinkedHashMap<String, MqttListener> map = mListenersMap.get(action);
        if (map != null) {
            map.remove(tag);
        }
    }

    public synchronized void unListen(@NonNull String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Set<String> actions = mListenersMap.keySet();
        for (String action : actions) {
            LinkedHashMap<String, MqttListener> map = mListenersMap.get(action);
            if (map != null) {
                map.remove(tag);
            }
        }
    }

    protected void publish(String topic, String msg) {
        publish(topic, msg, 1, false);
    }

    protected void publish(String topic, String msg, int qos) {
        publish(topic, msg, qos, false);
    }

    protected void publish(String topic, String msg, int qos, boolean retained) {
        if (!isMqttConnected()) {
            return;
        }
        mBinder.publish(topic, msg, qos, retained);
    }

    protected void subscribe(String[] topic, int[] qos) {
        if (!isMqttConnected()) {
            return;
        }
        mBinder.subscribe(topic, qos);
    }

    public void onRequestMsg(@NonNull Topic topic, String data) {
        if (topic == null || TextUtils.isEmpty(topic.action)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(data);
            RequestData<String> requestData = RequestData.build(object.optString("mode"),
                    sGson.<BaseParams>fromJson(object.optString("BaseParams"), new TypeToken<BaseParams>() {
                    }.getType()));
            requestData.setRequestTime(object.optLong("requestTime"));
            LinkedHashMap<String, MqttListener> map = mListenersMap.get(topic.action);
            if (map != null) {
                Set<String> tags = map.keySet();
                List<String> removeTag = new ArrayList<>();
                for (String tag : tags) {
                    MqttListener listener = map.get(tag);
                    if (listener != null) {
                        if (listener.listener != null) {
                            listener.listener.onReceive(topic, requestData.getMode());
                        }
                        if (!listener.persist) {
                            removeTag.add(tag);
                        }
                    }
                }
                for (String tag : removeTag) {
                    map.remove(tag);
                }
                if (map.size() < 1) {
                    mListenersMap.remove(topic.action);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onReplyMsg(@NonNull Topic topic, String data) {
        if (topic == null || TextUtils.isEmpty(topic.action)) {
            return;
        }

        try {
            JSONObject object = new JSONObject(data);
            ReplyData<String> replyData = ReplyData.build(object.optString("mode"));
            replyData.setBaseParams(sGson.<BaseParams>fromJson(object.optString("BaseParams"),
                    new TypeToken<BaseParams>() {
                    }.getType()));
            replyData.setReplyTime(object.optLong("replyTime"));
            replyData.setSuccess(object.optBoolean("success"));
            replyData.setCode(object.optInt("code"));
            replyData.setMessage(object.optString("message"));
            LinkedHashMap<String, MqttRespond> map = mResponseCbMap.remove(topic.action);
            if (map != null) {
                Set<String> tags = map.keySet();
                for (String tag : tags) {
                    MqttRespond respond = map.get(tag);
                    if (respond != null && respond.callback != null) {
                        if (replyData.isSuccess()) {
                            respond.callback.onReply(replyData.getMode());
                        } else {
                            respond.callback.onFail(replyData.getCode());
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
