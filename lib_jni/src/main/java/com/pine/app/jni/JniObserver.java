package com.pine.app.jni;

import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.jni.listener.IJniListener;
import com.pine.app.jni.listener.IRequestListener;
import com.pine.app.jni.listener.RequestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JniObserver {
    public static HashMap<String, HashMap<String, IJniListener>> mListenerMap = new HashMap<>();
    public static HashMap<String, String> mLastListenDataInfo = new HashMap<>();

    public static void listen(@NonNull String action, String callTag, IJniListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mListenerMap) {
            HashMap<String, IJniListener> map = mListenerMap.get(action);
            if (map == null) {
                map = new HashMap<>();
                mListenerMap.put(action, map);
            }
            map.put(callTag, listener);
        }
    }

    public static void listenPersist(@NonNull String action, String callTag, IJniListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mListenerMap) {
            HashMap<String, IJniListener> map = mListenerMap.get(action);
            if (map == null) {
                map = new HashMap<>();
                mListenerMap.put(action, map);
            }
            map.put(callTag, listener);
        }
        String lastData = null;
        synchronized (mLastListenDataInfo) {
            lastData = mLastListenDataInfo.get(action);
        }
        if (!TextUtils.isEmpty(lastData)) {
            listener.onReceive(action, lastData);
        }
    }

    public static void unListen(@NonNull String callTag) {
        synchronized (mListenerMap) {
            for (String key : mListenerMap.keySet()) {
                HashMap<String, IJniListener> map = mListenerMap.get(key);
                if (map != null) {
                    map.remove(callTag);
                }
            }
        }
    }

    public static void unListen(@NonNull String action, @NonNull String callTag) {
        synchronized (mListenerMap) {
            HashMap<String, IJniListener> map = mListenerMap.get(action);
            if (map != null) {
                map.remove(callTag);
            }
        }
    }

    public static void onReceive(@NonNull String action, String data) {
        List<IJniListener> listeners = new ArrayList<>();
        synchronized (mListenerMap) {
            HashMap<String, IJniListener> actionMap = mListenerMap.get(action);
            if (actionMap != null) {
                for (String key : actionMap.keySet()) {
                    IJniListener listener = actionMap.get(key);
                    if (listener != null) {
                        listeners.add(listener);
                    }
                }
            }
        }
        synchronized (mLastListenDataInfo) {
            mLastListenDataInfo.put(action, data);
        }
        for (IJniListener listener : listeners) {
            if (listener != null) {
                listener.onReceive(action, data);
            }
        }
    }

    public static HashMap<String, HashMap<String, RequestInfo>> mRequestMap = new HashMap<>();

    public static void addCallback(@NonNull String action, @NonNull String callTag,
                                   IRequestListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mRequestMap) {
            HashMap<String, RequestInfo> map = mRequestMap.get(action);
            if (map == null) {
                map = new HashMap<>();
                mRequestMap.put(action, map);
            }
            RequestInfo requestInfo = new RequestInfo(action, callTag);
            requestInfo.setCallTime(SystemClock.uptimeMillis());
            requestInfo.setListener(listener);
            map.put(callTag, requestInfo);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                checkRequestListenerState();
            }
        });
    }

    public static void onResponse(@NonNull String action, @NonNull String callTag, String data) {
        RequestInfo requestInfo = null;
        synchronized (mRequestMap) {
            HashMap<String, RequestInfo> actionMap = mRequestMap.get(action);
            if (actionMap != null) {
                requestInfo = actionMap.remove(callTag);
            }
        }
        if (requestInfo != null && requestInfo.getListener() != null) {
            requestInfo.getListener().onResponse(action, data);
        }
    }

    public static void onFail(@NonNull String action, @NonNull String callTag, int errCode) {
        RequestInfo requestInfo = null;
        synchronized (mRequestMap) {
            HashMap<String, RequestInfo> actionMap = mRequestMap.get(action);
            if (actionMap != null) {
                requestInfo = actionMap.remove(callTag);
            }
        }
        if (requestInfo != null && requestInfo.getListener() != null) {
            requestInfo.getListener().onFail(action, errCode);
        }
    }

    public static void checkRequestListenerState() {
        List<RequestInfo> listeners = new ArrayList<>();
        synchronized (mRequestMap) {
            for (String actionKey : mRequestMap.keySet()) {
                HashMap<String, RequestInfo> map = mRequestMap.get(actionKey);
                if (map != null) {
                    for (String callTagKey : map.keySet()) {
                        RequestInfo requestInfo = map.get(callTagKey);
                        if (requestInfo != null && requestInfo.getCallTime() > 0) {
                            long now = SystemClock.uptimeMillis();
                            if (now - requestInfo.getCallTime() > 30 * 60 * 1000) {
                                map.remove(callTagKey);
                                listeners.add(requestInfo);
                            }
                        }
                    }
                }
            }
        }
        for (RequestInfo requestInfo : listeners) {
            if (!TextUtils.isEmpty(requestInfo.getAction()) && requestInfo.getListener() != null) {
                requestInfo.getListener().onFail(requestInfo.getAction(), IRequestListener.ERR_CODE_TIMEOUT);
            }
        }
    }
}
