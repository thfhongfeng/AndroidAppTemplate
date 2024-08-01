package com.pine.template.base.device_sdk.library.gpioport;


import com.pine.tool.util.LogUtils;

/**
 * gpio助手
 */
public class GpioPortHelper {

    private final static String TAG = GpioPortHelper.class.getSimpleName();

    private boolean mIsOpen = false;

    private GpioPortConfig gpioPortConfig;

    private GphThreads gphThreads;

    /**
     * 数据回调
     */
    private GphResultCallback onResultCallback;

    /**
     * 数据处理
     */
    private GphDataProcess processingData;


    /**
     * 初始化gpio操作
     */
    public GpioPortHelper() {
        this(new GpioPortConfig());
    }

    /**
     * 初始化gpio操作
     *
     * @param gpioPortConfig gpio配置数据
     */
    public GpioPortHelper(GpioPortConfig gpioPortConfig) {
        this.gpioPortConfig = gpioPortConfig;
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setGphResultCallback(GphResultCallback onResultCallback) {
        if (gphThreads == null) {
            this.onResultCallback = onResultCallback;
            return;
        }
        processingData.setGphResultCallback(onResultCallback);
    }

    /**
     * gpio设置
     */
    public void setConfigInfo(GpioPortConfig gpioPortConfig) {
        this.gpioPortConfig = gpioPortConfig;
    }

    /**
     * 打开gpio设备
     *
     * @param port gpio引脚端口号
     */
    public boolean openDevice(String port) {
        this.gpioPortConfig.port = port;
        return openDevice();
    }

    public boolean openDevice() {
        return openDevice(true);
    }

    /**
     * 打开gpio设备
     */
    public boolean openDevice(boolean tryCloseThenOpen) {
        if (gpioPortConfig == null) {
            throw new IllegalArgumentException("'GpioPortConfig' must can not be null!!! ");
        }
        if (gpioPortConfig.port == null) {
            throw new IllegalArgumentException("You not have setting the device port, " +
                    "you must 'new GpioPortHelper(String port)' or call 'openDevice(String port)' ");
        }
        boolean success = GpioPortJNI.openPort(this.gpioPortConfig.port);
        // 打开gpio成功
        if (success) {
            mIsOpen = true;
            // 创建数据处理
            processingData = new GphDataProcess(gpioPortConfig);
            processingData.setGphResultCallback(onResultCallback);
            // 读写线程
            gphThreads = new GphThreads(processingData, gpioPortConfig);
            // 开启读数据线程
            gphThreads.startReadThread();
            // 开启写数据线程
            gphThreads.startWriteThread();
        } else {
            if (tryCloseThenOpen) {
                LogUtils.e(TAG, "tryCloseThenOpen !!! " +
                        "port:" + gpioPortConfig.port);
                closeDevice();
                openDevice(false);
            } else {
                mIsOpen = false;
                LogUtils.e(TAG, "cannot open the device !!! " +
                        "port:" + gpioPortConfig.port);
            }
        }
        return mIsOpen;
    }

    public void startReadGpio() {
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        gphThreads.startRead();
    }

    public void stopReadGpio() {
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        gphThreads.stopRead();
    }

    public int readGpioDirect() {
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return -1;
        }
        return gphThreads.readDirect();
    }

    /**
     * 发送gpio命令
     *
     * @param commands     gpio命令
     * @param commandsType gpio命令类型：0-返回命令；1-写入状态；2-写入方向
     */
    public void addCommands(int commands, int commandsType) {
        GphCmdEntity comEntry = new GphCmdEntity();
        comEntry.commands = commands;
        comEntry.commandsType = commandsType;
        addCommands(comEntry);
    }

    /**
     * 发送gpio命令
     *
     * @param commands     gpio命令
     * @param commandsType gpio命令类型：0-返回命令；1-写入状态；2-写入方向
     * @param flag         备用标识
     */
    public void addCommands(int commands, int commandsType, int flag) {
        GphCmdEntity comEntry = new GphCmdEntity();
        comEntry.commands = commands;
        comEntry.commandsType = commandsType;
        comEntry.flag = flag;
        addCommands(comEntry);
    }

    /**
     * 发送gpio命令
     *
     * @param gphCmdEntity gpio命令数据
     */
    public void addCommands(GphCmdEntity gphCmdEntity) {
        if (gphCmdEntity == null) {
            LogUtils.e(TAG, "GphCmdEntity can't be null !!!");
            return;
        }
        if (!isOpenDevice()) {
            LogUtils.e(TAG, "You not open device !!! ");
            return;
        }
        // 添加发送命令
        processingData.addCommands(gphCmdEntity);

    }

    /**
     * 关闭gpio
     */
    public void closeDevice() {
        GpioPortJNI.closePort(gpioPortConfig.port);
        if (gphThreads != null) {
            gphThreads.stop();
        }
    }

    /**
     * 判断gpio是否打开
     */
    public boolean isOpenDevice() {
        return mIsOpen;
    }
}
