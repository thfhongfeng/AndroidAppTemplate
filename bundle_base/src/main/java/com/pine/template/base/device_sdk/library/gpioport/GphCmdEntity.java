package com.pine.template.base.device_sdk.library.gpioport;

/**
 * gpio命令
 */
public class GphCmdEntity {

    public GphCmdEntity() {
    }

    public GphCmdEntity(int commands, int commandsType) {
        this.commands = commands;
        this.commandsType = commandsType;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * gpio发送的命令或者返回的数据
     */
    public int commands;

    /**
     * gpio发送或者返回的命令类型：0-返回命令；1-写入状态；2-写入方向
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
     * gpio回复数据条数
     */
    public int receiveCount = 1;

}
