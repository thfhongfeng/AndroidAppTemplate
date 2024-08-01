package com.pine.template.base.device_sdk.library.serialport;

import android.nfc.FormatException;

import com.pine.template.base.device_sdk.constants.SerialConstants;
import com.pine.tool.util.DataConversion;
import com.pine.tool.util.LogUtils;

/**
 * 串口助手
 */
public class SerialPortHelper {

    private final static String TAG = SerialPortHelper.class.getSimpleName();

    private volatile boolean mIsOpen = false;

    private SerialPortConfig serialPortConfig;

    private SphThreads sphThreads;

    /**
     * 最大接收数据的长度
     */
    private int maxSize;

    /**
     * 是否需要返回最大数据接收长度
     */
    private boolean isReceiveMaxSize;

    /**
     * 数据回调
     */
    private SphResultCallback onResultCallback;

    /**
     * 数据处理
     */
    private SphDataProcess processingData;

    /**
     * 初始化串口操作
     *
     * @param maxSize 串口每次读取数据的最大长度
     */
    public SerialPortHelper(int maxSize) {
        this(maxSize, new SerialPortConfig());
    }

    /**
     * 初始化串口操作
     *
     * @param maxSize          串口每次读取数据的最大长度
     * @param serialPortConfig 串口数据
     */
    public SerialPortHelper(int maxSize, SerialPortConfig serialPortConfig) {
        this.maxSize = maxSize;
        this.serialPortConfig = serialPortConfig;
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setSphResultCallback(SphResultCallback onResultCallback) {
        if (sphThreads == null) {
            this.onResultCallback = onResultCallback;
            return;
        }
        processingData.setSphResultCallback(onResultCallback);
    }

    /**
     * 串口设置
     */
    public void setConfigInfo(SerialPortConfig serialPortConfig) {
        this.serialPortConfig = serialPortConfig;
    }

    /**
     * 打开串口设备
     *
     * @param path 串口地址
     */
    public boolean openDeviceAndRead(String path) {
        this.serialPortConfig.path = path;
        return openDeviceAndRead();
    }

    /**
     * 打开串口设备
     *
     * @param path     串口地址
     * @param baudRate 波特率
     */
    public boolean openDeviceAndRead(String path, int baudRate) {
        this.serialPortConfig.path = path;
        this.serialPortConfig.baudRate = baudRate;
        return openDeviceAndRead();
    }

    /**
     * 打开串口设备
     */
    public boolean openDeviceAndRead() {
        return openDevice(true);
    }

    /**
     * 打开串口设备
     */
    public boolean openDevice(boolean startRead) {
        if (serialPortConfig == null) {
            throw new IllegalArgumentException("'SerialPortConfig' must can not be null!!! ");
        }
        if (serialPortConfig.path == null) {
            throw new IllegalArgumentException("You not have setting the device path, " +
                    "you must 'new SerialPortHelper(String path)' or call 'openDevice(String path)' ");
        }
        boolean success = SerialPortJNI.openPort(
                this.serialPortConfig.path,
                this.serialPortConfig.baudRate,
                this.serialPortConfig.dataBits,
                this.serialPortConfig.stopBits,
                this.serialPortConfig.parity);

        // 是否设置原始模式(Raw Mode)方式来通讯
        if (serialPortConfig.mode != 0) {
            SerialPortJNI.setMode(serialPortConfig.path, serialPortConfig.mode);
        }

        // 打开串口成功
        if (success) {
            mIsOpen = true;
            // 创建数据处理
            processingData = new SphDataProcess(serialPortConfig, maxSize);
            processingData.setReceiveMaxSize(isReceiveMaxSize);
            processingData.setSphResultCallback(onResultCallback);
            // 读写线程
            sphThreads = new SphThreads(processingData, serialPortConfig);
            // 开启读数据线程
            sphThreads.startReadThread();
            // 开启写数据线程
            sphThreads.startWriteThread();
            if (startRead) {
                startRead();
            }
        } else {
            mIsOpen = false;
            LogUtils.e(TAG, "cannot open the device !!! " +
                    "path:" + serialPortConfig.path);
        }
        return mIsOpen;
    }

    public void startRead() {
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        sphThreads.startRead();
    }

    public void stopRead() {
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        sphThreads.stopRead();
    }

    /**
     * 发送串口命令（hex）
     *
     * @param hex 十六进制命令
     */
    public void addCommands(String hex) {
        addCommands(hex, 0);
    }

    /**
     * 发送串口命令（hex）
     *
     * @param hex  十六进制命令
     * @param flag 备用标识
     */
    public void addCommands(String hex, int flag) {
        byte[] bytes = new byte[0];
        try {
            bytes = DataConversion.decodeHexString(hex);
            addCommands(bytes, flag);
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送串口命令
     *
     * @param commands 串口命令
     */
    public void addCommands(byte[] commands) {
        addCommands(commands, 0);
    }

    /**
     * 发送串口命令
     *
     * @param commands 串口命令
     * @param flag     备用标识
     */
    public void addCommands(byte[] commands, int flag) {
        SphCmdEntity comEntry = new SphCmdEntity();
        comEntry.commands = commands;
        comEntry.commandsType = SerialConstants.COMMAND_TYPE_WRITE;
        comEntry.flag = flag;
        comEntry.commandsHex = DataConversion.encodeHexString(commands);
        addCommands(comEntry);
    }

    /**
     * 发送串口命令
     *
     * @param sphCmdEntity 串口命令数据
     */
    public void addCommands(SphCmdEntity sphCmdEntity) {
        if (sphCmdEntity == null) {
            LogUtils.e(TAG, "SphCmdEntity can't be null !!!");
            return;
        }
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        // 添加发送命令
        processingData.addCommands(sphCmdEntity);
    }

    /**
     * 关闭串口
     */
    public void closeDevice() {
        SerialPortJNI.closePort(serialPortConfig.path);
        if (sphThreads != null) {
            sphThreads.stop();
        }
    }

    /**
     * 判断串口是否打开
     */
    public boolean isOpenDevice() {
        return mIsOpen;
    }
}
