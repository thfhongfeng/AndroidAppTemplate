package com.pine.app.lib.mqtt.framework.listener;

import android.text.TextUtils;

public class MqttRespond {
    public static int SUCCESS = 0;

    public static int ERR_UNKNOWN = 1;
    public static int ERR_PARAM_ILLEGAL = 2;
    public static int ERR_NO_NET = 3;
    public static int ERR_BUSINESS = 4;

    public static int ERR_MQTT_SERVER_NOT_INIT = 11;
    public static int ERR_MQTT_NOT_PREPARED = 12;

    public static int ERR_TIMEOUT = 100;

    public String action;
    public String tag;
    public IMqttRespond callback;
    public long regTime;
    // 响应超时时间，单位秒
    public int timeOut;

    public MqttRespond(String action, String tag, IMqttRespond callback, long regTime) {
        this.action = action;
        this.tag = tag;
        this.callback = callback;
        this.regTime = regTime;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(action) && !TextUtils.isEmpty(tag);
    }

    public interface IMqttRespond<T> {
        void onReply(T data);

        void onFail(int errCode);
    }
}
