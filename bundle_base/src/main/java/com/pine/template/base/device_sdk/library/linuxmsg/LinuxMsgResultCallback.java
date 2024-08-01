package com.pine.template.base.device_sdk.library.linuxmsg;

/**
 * entity数据回调
 */
public interface LinuxMsgResultCallback {

    /**
     * 收到的数据
     *
     * @param data entity收到的数据
     */
    void onReceiveData(LinuxMsgEntity data);
}
