package com.pine.template.base.device_sdk.library;

public interface MsgQidType {
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
    int NET_DRIVER = 1;
    int AV_PLAY = 2;
    int PERIPHERAL = 3;
    int GPS = 4;
    int SYSTEM = 5;
    int SCH = 6;
    int STATION = 7;
    int MONITOR = 8;
    int ADT = 9;
    int UPDATE = 10;
    int WDT = 11;
    int JNI_COM = 12;
    int CONTROLLER = 99;
}
