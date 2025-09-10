package com.pine.app.lib.mqtt.framework;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;

public class SubscribeInfo implements Serializable {
    public String[] topics;
    public int[] qos;

    public boolean isValid() {
        return topics != null && topics.length > 0 && qos != null && topics.length == qos.length;
    }

    @Override
    public String toString() {
        return "SubscribeInfo{" +
                "topics=" + Arrays.toString(topics) +
                ", qos=" + Arrays.toString(qos) +
                '}';
    }

    public static class SubscribeItem implements Serializable {
        public String topic;
        public int qos;

        public SubscribeItem(String topic, int qos) {
            this.topic = topic;
            this.qos = qos;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(topic) && qos >= 0;
        }

        @Override
        public String toString() {
            return "SubscribeInfo{" +
                    "topic='" + topic + '\'' +
                    ", qos=" + qos +
                    '}';
        }
    }
}
