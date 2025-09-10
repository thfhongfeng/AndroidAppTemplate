package com.pine.app.lib.mqtt.framework;

import android.text.TextUtils;

import com.pine.template.base.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MqttConfig implements Serializable {
    private String host;

    private String topicHead;

    private String mySubject;
    private String myId;

    private String username;
    private String pwd;
    // 单位：秒
    private int keepAliveInterval = 60;
    private int connectionTimeout = 30;
    // 订阅的广播的qos，-1表示不订阅
    private int subscribeBroadcastQos = -1;
    // 订阅的组播的SubscribeInfo.SubscribeItem List，null表示不订阅
    private List<SubscribeInfo.SubscribeItem> subscribeGroupList;
    // 订阅的接收者为我(${mySubject_myId})的qos，-1表示不订阅
    private int subscribeMyQos;

    private BaseParams baseParams;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTopicHead() {
        return topicHead;
    }

    public void setTopicHead(String topicHead) {
        this.topicHead = topicHead;
    }

    public String getMySubject() {
        return mySubject;
    }

    public void setMySubject(String mySubject) {
        this.mySubject = mySubject;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSubscribeBroadcastQos() {
        return subscribeBroadcastQos;
    }

    public void setSubscribeBroadcastQos(int subscribeBroadcastQos) {
        this.subscribeBroadcastQos = subscribeBroadcastQos;
    }

    public List<SubscribeInfo.SubscribeItem> getSubscribeGroupList() {
        return subscribeGroupList;
    }

    public void setSubscribeGroupList(List<SubscribeInfo.SubscribeItem> subscribeGroupList) {
        this.subscribeGroupList = subscribeGroupList;
    }

    public int getSubscribeMyQos() {
        return subscribeMyQos;
    }

    public void setSubscribeMyQos(int subscribeMyQos) {
        this.subscribeMyQos = subscribeMyQos;
    }

    public BaseParams getBaseParams() {
        return baseParams;
    }

    public void setBaseParams(BaseParams baseParams) {
        this.baseParams = baseParams;
    }

    public SubscribeInfo getSubscribeTopics() {
        List<String> topics = new ArrayList<>();
        List<Integer> qos = new ArrayList<>();
        if (subscribeBroadcastQos > -1) {
            String topic = TopicRuleManager.subscribeBroadcast(topicHead);
            if (!TextUtils.isEmpty(topic)) {
                topics.add(topic);
                qos.add(subscribeBroadcastQos);
            }
        }
        if (subscribeGroupList != null) {
            for (SubscribeInfo.SubscribeItem item : subscribeGroupList) {
                if (item != null && item.isValid()) {
                    String topic = TopicRuleManager.subscribeGroup(topicHead, item.topic);
                    if (!TextUtils.isEmpty(topic)) {
                        topics.add(topic);
                        qos.add(item.qos);
                    }
                }
            }
        }
        if (subscribeMyQos > -1) {
            String topic = TopicRuleManager.subscribeTopicForMe(topicHead, mySubject, myId);
            if (!TextUtils.isEmpty(topic)) {
                topics.add(topic);
                qos.add(subscribeMyQos);
            }
        }
        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.topics = topics.toArray(new String[0]);
        int[] qosArr = new int[qos.size()];
        for (int i = 0; i < qos.size(); i++) {
            qosArr[i] = qos.get(i);
        }
        subscribeInfo.qos = qosArr;
        return subscribeInfo;
    }

    @Override
    public String toString() {
        return "MqttConfig{" +
                "host='" + host + '\'' +
                ", topicHead='" + topicHead + '\'' +
                ", mySubject='" + mySubject + '\'' +
                ", myId='" + myId + '\'' +
                ", username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", keepAliveInterval=" + keepAliveInterval +
                ", connectionTimeout=" + connectionTimeout +
                ", subscribeBroadcastQos=" + subscribeBroadcastQos +
                ", subscribeGroupList=" + subscribeGroupList +
                ", subscribeMyQos=" + subscribeMyQos +
                ", baseParams=" + baseParams +
                '}';
    }
}
