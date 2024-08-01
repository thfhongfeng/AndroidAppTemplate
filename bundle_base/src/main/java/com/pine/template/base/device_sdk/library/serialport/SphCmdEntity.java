package com.pine.template.base.device_sdk.library.serialport;

import com.pine.tool.util.DataConversion;

/**
 * 串口命令
 */
public class SphCmdEntity {

    public SphCmdEntity() {
    }

    public SphCmdEntity(byte[] commands, int commandsType) {
        this.commands = commands;
        this.commandsHex = DataConversion.encodeHexString(commands);
        this.commandsType = commandsType;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * 串口发送或者返回的命令
     */
    public byte[] commands;

    /**
     * 串口发送或者返回的命令(hex)
     */
    public String commandsHex;

    /**
     * 串口发送或者返回的命令类型：0-返回命令；1-发送命令
     */
    public int commandsType;

    /**
     * 命令执行结果
     */
    protected boolean success = true;

    /**
     * 发送命令超时时间
     */
    public long timeOut = 0;

    /**
     * 是否重复发送命令
     */
    public boolean reWriteCom = false;

    /**
     * 数据重发次数
     */
    public int reWriteTimes = 0;

    /**
     * 备用标识
     */
    public int flag;

    /**
     * 串口回复数据条数
     */
    public int receiveCount = 1;

}
