package com.pine.template.base.device_sdk.library.linuxmsg;

import com.pine.tool.util.LogUtils;

import java.util.Random;

/**
 * 助手
 */
public class LinuxMsgHelper {

    private final static String TAG = LinuxMsgHelper.class.getSimpleName();

    private String senderTag;

    private boolean mIsOpen = false;

    private LinuxMsgConfig config;

    private LinuxMsgThreads threads;

    /**
     * 数据回调
     */
    private LinuxMsgResultCallback onResultCallback;

    /**
     * 数据处理
     */
    private LinuxMsgProcess processingData;


    /**
     * 初始化操作
     */
    public LinuxMsgHelper() {
        this(new LinuxMsgConfig());
    }

    /**
     * 初始化操作
     *
     * @param config 配置数据
     */
    public LinuxMsgHelper(LinuxMsgConfig config) {
        this.config = config;
        this.senderTag = hashCode() + "_" + new Random().nextInt(1000);
        LogUtils.d(TAG, "senderTag:" + senderTag);
    }

    public LinuxMsgHelper(LinuxMsgConfig config, String senderTag) {
        this.config = config;
        this.senderTag = senderTag;
        LogUtils.d(TAG, "senderTag:" + senderTag);
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setResultCallback(LinuxMsgResultCallback onResultCallback) {
        if (threads == null) {
            this.onResultCallback = onResultCallback;
            return;
        }
        processingData.setResultCallback(onResultCallback);
    }

    /**
     * 设置
     */
    public void setConfig(LinuxMsgConfig config) {
        this.config = config;
    }

    /**
     * 打开
     *
     * @param readMsgType  1-g_iNetDriverMsgQid
     *                     2-g_iAvPlayMsgQid
     *                     3-g_iPeripheralMsgQid
     *                     4-g_iGpsMsgQid
     *                     5-g_iSystemMsgQid
     *                     6-g_iSchMsgQid
     *                     7-g_iStationMsgQid
     *                     8-g_iMonitorMsgQid
     *                     9-g_iAdtMsgQid
     *                     10-g_iUpdateMsgQid
     *                     11-g_iWdtMsgQid
     *                     12-g_iJniComMsgQid
     *                     99-g_iControllerMsgQid
     * @param writeMsgType as readMsgType
     */
    public boolean open(int readMsgType, int writeMsgType) {
        this.config.readMsgType = readMsgType;
        this.config.writeMsgType = writeMsgType;
        return open();
    }

    /**
     * 打开
     */
    public boolean open() {
        if (config == null) {
            throw new IllegalArgumentException("LinuxMsgConfig must can not be null!!! ");
        }
        // 创建数据处理
        processingData = new LinuxMsgProcess(senderTag, config);
        processingData.setResultCallback(onResultCallback);
        // 读写线程
        threads = new LinuxMsgThreads(processingData, config);
        // 开启读数据线程
        threads.startReadThread();
        // 开启写数据线程
        threads.startWriteThread();
        mIsOpen = true;
        LogUtils.e(TAG, "opened !!! ");
        return true;
    }

    public void startRead() {
        if (!isOpen()) {
            LogUtils.e(TAG, "You not open !!! ");
            return;
        }
        threads.startRead();
    }

    public void stopRead() {
        if (!isOpen()) {
            LogUtils.e(TAG, "You not open !!! ");
            return;
        }
        threads.stopRead();
    }

    public void sendMsg(String msg, LinuxMsgResponseCallback callback) {
        sendMsg(msg, 0, callback);
    }

    public void sendMsg(String msg, int timeout, LinuxMsgResponseCallback callback) {
        LinuxMsgEntity entity = new LinuxMsgEntity(senderTag, msg);
        entity.setTimeout(timeout);
        entity.setMsgType(config.writeMsgType);
        entity.setMsgClass(1);
        // 添加发送
        processingData.addEntity(entity, callback);
    }

    public void sendReplyMsg(String replayCode, String msg) {
        LinuxMsgEntity entity = new LinuxMsgEntity(senderTag, msg);
        entity.setMsgCode(replayCode);
        entity.setMsgType(config.writeMsgType);
        entity.setMsgClass(2);
        // 添加发送
        processingData.addEntity(entity);
    }

    /**
     * 关闭
     */
    public void close() {
        if (threads != null) {
            threads.stop();
        }
        if (processingData != null) {
            processingData.close();
        }
    }

    /**
     * 判断是否打开
     */
    public boolean isOpen() {
        return mIsOpen;
    }
}
