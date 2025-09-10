package com.pine.app.lib.mqtt.framework;

import static com.pine.app.lib.mqtt.framework.Topic.STEP_REPLY;
import static com.pine.app.lib.mqtt.framework.Topic.STEP_REQUEST;

import com.pine.tool.util.LogUtils;

public class TopicRule implements ITopicRule {
    private final static String TAG = TopicRule.class.getSimpleName();

    public static final String GROUP = "group";
    public static final String ACTION_FLAG_SEG = "action";

    /**
     * topic构成
     * 1. 点对点topic
     * 代码: /${head}/${receiver}_[${receiverFlag}]/${sender}_${senderFlag}/${step}/action/${action}
     * 释义: /头标识/接收者枚举名_[接收者自定义标识]/发送者枚举名_[发送者自定义标识]/阶段/action/具体动作标识
     * <p>
     * 2. 组播topic:
     * 代码: /${head}/group_[${group}]/${sender}_${senderFlag}/${step}/action/${action}
     * 释义:/头标识/group_[组标识]/发送者枚举名_[发送者自定义标识]/[阶段/]action/具体动作标识
     * 组播topic的组标识为空，表示广播
     **/
    private String buildGroupHead(String topicHead) {
        return "/" + topicHead + "/" + GROUP + "_";
    }

    private String buildMySeg(String mySubject, String myId) {
        return mySubject + "_" + myId;
    }

    @Override
    public Topic parseTopic(String topicHead, String topicStr) {
        Topic topic = new Topic();
        if (topicStr == null || topicStr.length() < 1) {
            LogUtils.w(TAG, "parseTopic => topic is empty");
            return null;
        }
        String[] segArr = topicStr.split("/" + topicHead + "/");
        if (segArr.length != 2) {
            LogUtils.w(TAG, "parseTopic => do not match topic head");
            return null;
        }
        segArr = segArr[1].replaceAll("/+", "/").split("/" + ACTION_FLAG_SEG + "/");
        if (segArr.length != 2) {
            LogUtils.w(TAG, "parseTopic => do not has action flag seg");
            return null;
        }
        String[] arr = segArr[0].split("/");
        if (arr.length != 3) {
            LogUtils.w(TAG, "parseTopic => topic is not match rule");
            return null;
        }
        int rIndex = arr[0].indexOf("_");
        int sIndex = arr[1].indexOf("_");
        String stepStr = arr[2];
        if (rIndex < 0 || sIndex < 0
                || (!STEP_REQUEST.equals(stepStr) && !STEP_REPLY.equals(stepStr))) {
            LogUtils.w(TAG, "parseTopic => receiver:" + arr[0] + ", or sender:" + arr[1]
                    + ", or step:" + arr[2] + " is no valid");
            return null;
        }
        String receiverStr = arr[0].substring(0, rIndex);
        String receiverFlagStr = rIndex + 1 < arr[0].length() ? arr[0].substring(rIndex + 1) : "";
        String senderStr = arr[1].substring(0, sIndex);
        String senderFlagStr = sIndex + 1 < arr[1].length() ? arr[1].substring(sIndex + 1) : "";

        topic.isValid = true;
        topic.head = topicHead;
        topic.isGroup = GROUP.equals(receiverStr);
        topic.receiver = receiverStr;
        topic.receiverFlag = receiverFlagStr;
        topic.sender = senderStr;
        topic.senderFlag = senderFlagStr;
        topic.step = stepStr;
        topic.action = segArr[1];
        return topic;
    }

    @Override
    public String subscribeTopicForMe(String topicHead, String mySubject, String myId) {
        return "/" + topicHead + "/" + buildMySeg(mySubject, myId) + "/#";
    }

    @Override
    public String subscribeBroadcast(String topicHead) {
        return "/" + topicHead + "/" + GROUP + "_" + "/#";
    }

    @Override

    public String subscribeGroup(String topicHead, String group) {
        return buildGroupHead(topicHead) + group + "/#";
    }

    @Override
    public String buildTopicRequest(String topicHead, String mySubject, String myId,
                                    String action, String requestReceiver, String receiverFlag) {
        return "/" + topicHead + "/" + requestReceiver + "_" + receiverFlag
                + "/" + buildMySeg(mySubject, myId) + "/" + STEP_REQUEST + "/" + ACTION_FLAG_SEG + "/" + action;
    }

    @Override
    public String buildBroadcastRequest(String topicHead, String mySubject, String myId, String action) {
        return buildGroupHead(topicHead) + "/" + buildMySeg(mySubject, myId) + "/"
                + STEP_REQUEST + "/" + ACTION_FLAG_SEG + "/" + action;
    }

    @Override
    public String buildGroupRequest(String topicHead, String mySubject, String myId,
                                    String group, String action) {
        return buildGroupHead(topicHead) + group + "/" + buildMySeg(mySubject, myId) + "/"
                + STEP_REQUEST + "/" + ACTION_FLAG_SEG + "/" + action;
    }

    @Override
    public String toReplyTopic(Topic topic, String mySubject, String myId) {
        return "/" + topic.head + "/" + topic.sender + "_" + topic.senderFlag + "/" + buildMySeg(mySubject, myId)
                + "/" + STEP_REPLY + "/" + ACTION_FLAG_SEG + "/" + topic.action;
    }
}