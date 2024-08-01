package com.pine.template.base.device_sdk.library.linuxmsg;

/**
 * entity数据回调
 */
public abstract class LinuxMsgResponseCallback {
    protected long sendTimeStamp = 0;

    public abstract void onSend(LinuxMsgEntity data);

    /**
     * 响应
     *
     * @param data entity收到的数据
     */
    public abstract void onResponse(LinuxMsgEntity data);

    /**
     * 失败
     */
    public abstract void onFail(int errCode, LinuxMsgEntity data);
}
