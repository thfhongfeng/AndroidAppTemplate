package com.pine.template.base.device_sdk.library.linuxmsg;

/**
 * 配置数据
 */
public class LinuxMsgConfig {
    /**
     * 1-g_iNetDriverMsgQid
     * 2-g_iAvPlayMsgQid
     * 3-g_iPeripheralMsgQid
     * 4-g_iGpsMsgQid
     * 5-g_iSystemMsgQid
     * 6-g_iSchMsgQid
     * 7-g_iStationMsgQid
     * 8-g_iMonitorMsgQid
     * 9-g_iAdtMsgQid
     * 10-g_iUpdateMsgQid
     * 11-g_iWdtMsgQid
     * 12-g_iJniComMsgQid
     * 99-g_iControllerMsgQid
     */
    public int readMsgType = 12;
    /**
     * 1-g_iNetDriverMsgQid
     * 2-g_iAvPlayMsgQid
     * 3-g_iPeripheralMsgQid
     * 4-g_iGpsMsgQid
     * 5-g_iSystemMsgQid
     * 6-g_iSchMsgQid
     * 7-g_iStationMsgQid
     * 8-g_iMonitorMsgQid
     * 9-g_iAdtMsgQid
     * 10-g_iUpdateMsgQid
     * 11-g_iWdtMsgQid
     * 12-g_iJniComMsgQid
     * 99-g_iControllerMsgQid
     */
    public int writeMsgType = 5;
    /**
     * 读取间隔（毫秒）
     */
    public int readInterval = 200;

    /**
     * 发送超时时间
     */
    public int timeout = 0;
    /**
     * 最大等待写入命令数量
     */
    public int maxDelayCmdCount = 100;
}
