package com.pine.template.base.device_sdk.library.serialport;

/**
 * 串口数据回调
 */
public interface SphResultCallback {

    /**
     * 发送命令
     *
     * @param sendCom 串口发送的命令
     */
    void onSendData(SphCmdEntity sendCom);

    /**
     * 收到的数据
     *
     * @param data 串口收到的数据
     */
    void onReceiveData(SphCmdEntity data);

    /**
     * 发送，收取完成
     *
     * @param data 串口收到的数据
     */
    void onComplete(SphCmdEntity data);
}
