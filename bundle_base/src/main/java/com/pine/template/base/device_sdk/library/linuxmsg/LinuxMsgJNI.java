package com.pine.template.base.device_sdk.library.linuxmsg;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;

/**
 * 操作类
 */
public class LinuxMsgJNI {
    public static boolean sendMsg(int msgType, String msg) {
        try {
            return DeviceSdkManager.getInstance().sendLinuxMsg(msgType, msg);
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getMsg(int msgType) {
        try {
            return DeviceSdkManager.getInstance().getLinuxMsg(msgType);
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean reInitMsgQueue(int msgType) {
        try {
            return DeviceSdkManager.getInstance().reInitMsgQueue(msgType);
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return false;
    }
}
