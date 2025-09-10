package com.pine.app.lib.mqtt.framework.listener;

import com.pine.app.lib.mqtt.framework.Topic;

public class MqttListener {
    public String tag;
    public boolean persist;
    public IMqttListener listener;
    public long regTime;

    public MqttListener(String tag, IMqttListener listener, long regTime) {
        this.tag = tag;
        this.listener = listener;
        this.regTime = regTime;
    }

    public interface IMqttListener<T> {
        void onReceive(Topic topic, T data);
    }
}
