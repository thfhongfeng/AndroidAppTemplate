package com.pine.app.lib.mqtt.framework;

import android.text.TextUtils;

public class Topic {
    // 主题类型-请求（包括被动请求和主动请求）
    public static String STEP_REQUEST = "request";
    // 主题类型-回复（对请求的回复）（请求不一定需要回复，看具体功能）
    public static String STEP_REPLY = "reply";

    // 头标识
    public String head;
    // 组标识
    public boolean isGroup;
    // 接收者枚举名
    public String receiver;
    // 接收者自定义标识
    public String receiverFlag;
    // 发送者枚举名
    public String sender;
    // 发送者自定义标识
    public String senderFlag;
    // request;reply;
    public String step;
    // 具体动作标识
    public String action;

    // 预留数据字段1
    public String reserved1;
    // 预留数据字段2
    public String reserved2;
    // 预留数据字段3
    public String reserved3;

    public boolean isValid;

    public boolean isBroadcast() {
        return isGroup && TextUtils.isEmpty(receiverFlag);
    }

    public boolean isGroup() {
        return isGroup && !TextUtils.isEmpty(receiverFlag);
    }

    public boolean isFromServer() {
        return SubjectEnum.SERVER.getContent().equals(sender);
    }

    public boolean isFromWeb() {
        return SubjectEnum.WEB.getContent().equals(sender);
    }

    public boolean isFromPhone() {
        return SubjectEnum.PHONE.getContent().equals(sender);
    }

    public boolean isRequestTopic() {
        return STEP_REQUEST.equals(step);
    }

    public boolean isReplyTopic() {
        return STEP_REPLY.equals(step);
    }

    public boolean isAction(String action) {
        return !TextUtils.isEmpty(action) && TextUtils.equals(action, this.action);
    }

    @Override
    public String toString() {
        return "ScTopic{" +
                "head='" + head + '\'' +
                ", isGroup=" + isGroup +
                ", receiver='" + receiver + '\'' +
                ", receiverFlag='" + receiverFlag + '\'' +
                ", sender='" + sender + '\'' +
                ", senderFlag='" + senderFlag + '\'' +
                ", step='" + step + '\'' +
                ", action='" + action + '\'' +
                ", reserved1='" + reserved1 + '\'' +
                ", reserved2='" + reserved2 + '\'' +
                ", reserved3='" + reserved3 + '\'' +
                ", isValid=" + isValid +
                '}';
    }

    /**
     * 主体枚举（发送者和接收者）
     */
    public enum SubjectEnum {
        DEFAULT("default"),
        SERVER("server"),
        WEB("web"),
        PHONE("phone");
        private final String content;

        SubjectEnum(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
