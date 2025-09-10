package com.pine.app.lib.mqtt.framework;

public interface ITopicRule {
    Topic parseTopic(String topicHead, String topicStr);

    String subscribeTopicForMe(String topicHead, String mySubject, String myId);

    String subscribeBroadcast(String topicHead);

    String subscribeGroup(String topicHead, String group);

    String buildTopicRequest(String topicHead, String mySubject, String myId,
                             String action, String requestReceiver, String receiverFlag);

    String buildBroadcastRequest(String topicHead, String mySubject, String myId, String action);

    String buildGroupRequest(String topicHead, String mySubject, String myId,
                             String group, String action);

    String toReplyTopic(Topic topic, String mySubject, String myId);
}
