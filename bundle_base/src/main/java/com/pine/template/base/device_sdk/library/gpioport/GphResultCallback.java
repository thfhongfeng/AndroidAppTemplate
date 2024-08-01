package com.pine.template.base.device_sdk.library.gpioport;

/**
 * gpio数据回调
 */
public interface GphResultCallback {

    /**
     * 发送命令
     *
     * @param sendCom gpio发送的命令
     */
    void onSendData(GphCmdEntity sendCom);

    /**
     * 收到的数据
     *
     * @param data gpio收到的数据
     */
    void onReceiveData(GphCmdEntity data);

    /**
     * 发送，收取完成
     *
     * @param data gpio收到的数据
     */
    void onComplete(GphCmdEntity data);
}
