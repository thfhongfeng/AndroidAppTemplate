package com.pine.template.base.device_sdk.library;

public interface CmdType {
    /**
     * 串口发送或者返回的命令类型：0-返回命令
     */
    int RECEIVE = 0;
    /**
     * 串口发送或者返回的命令类型：1-发送命令
     */
    int SEND = 1;
}
