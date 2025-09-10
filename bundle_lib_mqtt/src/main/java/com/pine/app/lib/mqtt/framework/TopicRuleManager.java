package com.pine.app.lib.mqtt.framework;

public class TopicRuleManager {
    private final static String TAG = TopicRuleManager.class.getSimpleName();

    private static ITopicRule mTopicRule = new TopicRule();

    public static void init(ITopicRule topicRule) {
        mTopicRule = topicRule;
    }

    public static Topic parseTopic(String topicHead, String topicStr) {
        return mTopicRule.parseTopic(topicHead, topicStr);
    }

    public static String subscribeTopicForMe(String topicHead, String mySubject, String myId) {
        return mTopicRule.subscribeTopicForMe(topicHead, mySubject, myId);
    }

    public static String subscribeBroadcast(String topicHead) {
        return mTopicRule.subscribeBroadcast(topicHead);
    }

    public static String subscribeGroup(String topicHead, String group) {
        return mTopicRule.subscribeGroup(topicHead, group);
    }

    public static String buildTopicRequest(String topicHead, String mySubject, String myId,
                                           String action, Topic.SubjectEnum requestReceiver) {
        return buildTopicRequest(topicHead, mySubject, myId, action, requestReceiver, "");
    }

    public static String buildTopicRequest(String topicHead, String mySubject, String myId,
                                           String action, Topic.SubjectEnum requestReceiver, String receiverFlag) {
        return buildTopicRequest(topicHead, mySubject, myId, action, requestReceiver.getContent(), receiverFlag);
    }

    public static String buildTopicRequest(String topicHead, String mySubject, String myId,
                                           String action, String requestReceiver) {
        return buildTopicRequest(topicHead, mySubject, myId, action, requestReceiver, "");
    }

    public static String buildTopicRequest(String topicHead, String mySubject, String myId,
                                           String action, String requestReceiver, String receiverFlag) {
        return mTopicRule.buildTopicRequest(topicHead, mySubject, myId, action, requestReceiver, receiverFlag);
    }

    public static String buildBroadcastRequest(String topicHead, String mySubject, String myId, String action) {
        return mTopicRule.buildBroadcastRequest(topicHead, mySubject, myId, action);
    }

    public static String buildGroupRequest(String topicHead, String mySubject, String myId,
                                           String group, String action) {
        return mTopicRule.buildGroupRequest(topicHead, mySubject, myId, group, action);
    }

    public static String toReplyTopic(Topic topic, String mySubject, String myId) {
        return mTopicRule.toReplyTopic(topic, mySubject, myId);
    }
}
