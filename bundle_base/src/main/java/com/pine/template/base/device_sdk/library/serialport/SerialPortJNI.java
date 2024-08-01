package com.pine.template.base.device_sdk.library.serialport;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;

/**
 * 串口操作类
 */
public class SerialPortJNI {

    /**
     * 打开串口并设置串口数据位，校验位, 速率，停止位
     *
     * @param path     串口地址
     * @param baudRate 波特率
     * @param dataBits 数据位
     * @param stopBits 停止位
     * @param parity   校验类型 取值N ,E, O
     * @return
     */
    public static boolean openPort(String path, int baudRate, int dataBits,
                                   int stopBits, char parity) {
        try {
            return DeviceSdkManager.getInstance()
                    .openSerialPort(path, baudRate, dataBits, stopBits, parity) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置是否使用原始模式(Raw Mode)方式来通讯 取值0,1,2
     *
     * @param path 串口地址
     * @param mode 0=nothing
     *             1=Raw mode
     *             2=no raw mode
     * @return
     */
    public static boolean setMode(String path, int mode) {
        try {
            return DeviceSdkManager.getInstance().setSerialPortMode(path, mode) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取串口数据
     *
     * @param path    串口地址
     * @param maxSize 数据最大长度
     * @return 串口数据
     */
    public static byte[] readPort(String path, int maxSize) {
        try {
            byte[] responseData = new byte[maxSize];
            int length = DeviceSdkManager.getInstance().readSerialPort(path, maxSize, responseData);
            if (length > 0) {
                byte[] validData = new byte[length];
                System.arraycopy(responseData, 0, validData, 0, length);
                return validData;
            } else {
                return null;
            }
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入串口数据
     *
     * @param path 串口地址
     * @param data 串口数据指令
     * @return
     */
    public static boolean writePort(String path, byte[] data) {
        try {
            return DeviceSdkManager.getInstance().writeSerialPort(path, data) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭串口
     *
     * @param path 串口地址
     * @return
     */
    public static boolean closePort(String path) {
        try {
            return DeviceSdkManager.getInstance().closeSerialPort(path) == 1;
        } catch (DeviceSdkException e) {
            e.printStackTrace();
            return false;
        }
    }
}
