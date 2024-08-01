package com.pine.template.base.device_sdk.library.gpioport;

/**
 * gpio配置数据
 */
public class GpioPortConfig {

    /**
     * 引脚端口号
     */
    public String port;
    /**
     * 读取间隔（毫秒）
     */
    public int readInterval = 50;
    /**
     * 最大等待写入命令数量
     */
    public int maxDelayCmdCount = 100;
}
