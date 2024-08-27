package com.pine.template.base.device_sdk.library.linuxmsg;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LinuxMsgEntity {
    // 发送者标识
    private String senderTag;
    // 自动生成，消息标识
    private String msgCode;
    // 消息类别：1-主动消息；2-响应消息
    private int msgClass;
    /**
     * 1-g_iNetDriverMsgQid
     * 2-g_iAvPlayMsgQid
     * 3-g_iPeripheralMsgQid
     * 4-g_iGpsMsgQid
     * 5-g_iSystemMsgQid
     * 6-g_iSchMsgQid
     * 7-g_iStationMsgQid
     * 8-g_iMonitorMsgQid
     * 9-g_iAdtMsgQid
     * 10-g_iUpdateMsgQid
     * 11-g_iWdtMsgQid
     * 12-g_iJniComMsgQid
     * 99-g_iControllerMsgQid
     */
    private int msgType;
    private String msg;

    private int timeout;

    public LinuxMsgEntity(String senderTag, String msg) {
        this.senderTag = senderTag;
        this.msg = msg;
        this.msgCode = hashCode() + "_" + new Random().nextInt(1000);
    }

    public String getSenderTag() {
        return senderTag;
    }

    public void setSenderTag(String senderTag) {
        this.senderTag = senderTag;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public int getMsgClass() {
        return msgClass;
    }

    public void setMsgClass(int msgClass) {
        this.msgClass = msgClass;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "LinuxMsgEntity{" +
                "senderTag='" + senderTag + '\'' +
                ", msgCode='" + msgCode + '\'' +
                ", msgClass=" + msgClass +
                ", msgType=" + msgType +
                ", msg='" + msg + '\'' +
                ", timeout=" + timeout +
                '}';
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("msgType", msgType);
            obj.put("senderTag", senderTag);
            obj.put("msgCode", msgCode);
            obj.put("msgClass", msgClass);
            try {
                JSONObject msgObj = new JSONObject(msg);
                obj.put("msg", msgObj);
            } catch (JSONException e) {
                obj.put("msg", msg);
            }
            obj.put("timeout", timeout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public static LinuxMsgEntity toEntity(String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(jsonData);
            LinuxMsgEntity entity = new LinuxMsgEntity(object.optString("senderTag"),
                    object.optString("msg"));
            entity.setMsgCode(object.optString("msgCode"));
            entity.setMsgClass(object.optInt("msgClass"));
            entity.setMsgType(object.optInt("msgType"));
            entity.setTimeout(object.optInt("timeout"));
            return entity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
