package com.pine.template.base.device_sdk.library.gpioport;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;

/**
 * gpio操作类
 */
public class GpioPortJNI {

    /**
     * 打开gpio
     *
     * @param port gpio引脚端口号
     * @return
     */
    public static boolean openPort(String port) {
        try {
            return DeviceSdkManager.getInstance().openGpioPort(port) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取gpio数据
     *
     * @param port gpio引脚端口号
     * @return gpio数据
     */
    public static int readPort(String port) {
        try {
            return DeviceSdkManager.getInstance().readGpioStatus(port);
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 读取gpio方向
     *
     * @param port gpio引脚端口号
     * @return （0-输入，1-输出）
     */
    public static int readDirect(String port) {
        try {
            return DeviceSdkManager.getInstance().readGpioDirect(port);
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 写入gpio数据
     *
     * @param port   gpio引脚端口号
     * @param status gpio数据指令
     */
    public static boolean writePort(String port, int status) {
        try {
            return DeviceSdkManager.getInstance().writeGpioStatus(port, status) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入gpio方向
     *
     * @param port   gpio引脚端口号
     * @param direct （0-输入，1-输出）
     */
    public static boolean writeDirect(String port, int direct) {
        try {
            return DeviceSdkManager.getInstance().writeGpioDirect(port, direct) > 0;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭gpio
     *
     * @param port gpio引脚端口号
     */
    public static boolean closePort(String port) {
        try {
            return DeviceSdkManager.getInstance().closeGpioPort(port) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
            return false;
        }
    }
}
