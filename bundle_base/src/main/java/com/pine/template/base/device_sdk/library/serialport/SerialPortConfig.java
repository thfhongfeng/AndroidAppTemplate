package com.pine.template.base.device_sdk.library.serialport;

/**
 * 串口配置数据
 */
public class SerialPortConfig {

    /**
     * 串口地址
     */
    public String path;
    /**
     * 波特率
     */
    public int baudRate = 9600;
    /**
     * 数据位 取值 位 7或 8
     */
    public int dataBits = 8;
    /**
     * 停止位 取值 1 或者 2
     */
    public int stopBits = 1;
    /**
     * 校验类型 取值 N ,E, O,
     */
    public char parity = 'n';

    /**
     * 是否使用原始模式(Raw Mode)方式来通讯
     * 取值 0=nothing,
     * 1=Raw mode,
     * 2=no raw mode
     */
    public int mode = 0;
    /**
     * 读取间隔（毫秒）
     */
    public int readInterval = 100;
    /**
     * 最大等待写入命令数量
     */
    public int maxDelayCmdCount = 100;
}
