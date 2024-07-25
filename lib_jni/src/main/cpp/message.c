/**********************************************************************************************************************
 消息处理
**********************************************************************************************************************/


#include "include/includes.h"
#include "include/message.h"


/**************************************************************************************************
* 函数功能：创建消息队列
* 入口参数：关键字
* 返回数值：消息标识符
* 调用函数：
**************************************************************************************************/
int CreatMsgQueue(key_t key) {
    int msqid;

    msqid = msgget(key, (IPC_CREAT | 0777));
    if (msqid == -1) {
        LOGD("LinuxMsg>> cannot create message queue resource, key: %#X, error: %s\r\n", key,
             strerror(errno));
        return -1;
    }

    return msqid;
}

/**************************************************************************************************
* 函数功能：发送消息
* 入口参数：消息标识符、消息内容
* 返回数值：
* 调用函数：
**************************************************************************************************/
int SendMsg(int msqid, const char *msg, int iLen) {
    int iResult;
    msg_info buf;
    LOGE("LinuxMsg>> SendMsg msg length: %d, MAX_MESSAGE_LEN: %d", iLen, MAX_MESSAGE_LEN);
    if (strlen(msg) > MAX_MESSAGE_LEN - 500) {
        buf.mtype = 2;
        char filePath[100];
        // 获取当前时间
        int ts = time(NULL);   //ts=1719390902
        sprintf(filePath, "%s%d%d.txt", DATA_FILE_DIR, ts, 100 + rand() % 900);
        FILE *file = fopen(filePath, "w");
        if (file != NULL) {
            fprintf(file, "%s", msg);
            fclose(file);
        } else {
            // 错误处理，文件打开失败
            LOGE("LinuxMsg>> SendMsg fopen: Error opening file: %s, error:%s", filePath, strerror(errno));
        }
        iLen = strlen(filePath);
        memcpy(buf.mtext, filePath, iLen);
    } else {
        buf.mtype = 1;
        memcpy(buf.mtext, msg, iLen);
    }

    iResult = msgsnd(msqid, &buf, iLen, IPC_NOWAIT);
    if (iResult == -1) {
        LOGE("LinuxMsg>> cannot send message to the %d message queue error: %s\r\n", msqid,
             strerror(errno));
    }
    LOGD("LinuxMsg>> SendMsg iResult: %d, msqid: %d, msg: %s", iResult, msqid, msg);
    return iResult;
}

char *read_file_to_string(const char *filename, int *rlen) {
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        LOGE("LinuxMsg>> RecvMsg read_file_to_string: Error opening file: %s, error:%s", filename, strerror(errno));
        return NULL;
    }

    // 首先，尝试确定文件的大小
    fseek(file, 0, SEEK_END);
    long filesize = ftell(file);
    fseek(file, 0, SEEK_SET);

    // 为文件内容分配内存
    char *buffer = (char *) malloc(filesize + 1); // +1 为了末尾的空字符
    if (buffer == NULL) {
        LOGE("LinuxMsg>> RecvMsg read_file_to_string: Memory allocation file: %s, error:%s", filename, strerror(errno));
        fclose(file);
        // 删除文件
        if (remove(filename) != 0) {
            LOGE("LinuxMsg>> RecvMsg read_file_to_string: Error deleting file: %s, error:%s", filename, strerror(errno));
        }
        return NULL;
    }

    // 读取文件内容
    size_t n = fread(buffer, sizeof(char), filesize, file);
    if (n != filesize) {
        // 读取失败或文件大小在读取时发生了变化（不太可能）
        free(buffer);
        LOGE("LinuxMsg>> RecvMsg read_file_to_string: Failed to read file: %s, error:%s", filename, strerror(errno));
        fclose(file);
        // 删除文件
        if (remove(filename) != 0) {
            LOGE("LinuxMsg>> RecvMsg read_file_to_string: Error deleting file: %s, error:%s", filename, strerror(errno));
        }
        return NULL;
    }

    // 在字符串末尾添加空字符以确保它是一个C字符串
    buffer[filesize] = '\0';

    fclose(file);
    // 删除文件
    if (remove(filename) != 0) {
        LOGE("LinuxMsg>> RecvMsg read_file_to_string: Error deleting file: %s, error:%s", filename, strerror(errno));
    }

    *rlen = filesize + 1;

    return buffer;
}

/**************************************************************************************************
* 函数功能：接收消息
* 入口参数：消息标识符、消息内容
* 返回数值：
* 调用函数：
**************************************************************************************************/
int RecvMsg(int iQid, char *pcMsg) {
    int iResult;
    int iMsgType;

    msg_info buffer;

    iMsgType = 0;
    bzero(buffer.mtext, MAX_MESSAGE_LEN);

    iResult = msgrcv(iQid, &buffer, MAX_MESSAGE_LEN, iMsgType, IPC_NOWAIT);
    if (iResult == -1) {
//        LOGE("LinuxMsg>> cannot RecvMsg from the %d message queue error: %s\r\n", iQid,
//             strerror(errno));
    } else {
        if (buffer.mtype == 1) {
            memcpy(pcMsg, buffer.mtext, iResult);
        } else {
            char *msg = read_file_to_string(buffer.mtext, &iResult);
            if (msg != NULL) {
                memcpy(pcMsg, msg, iResult);
                free(msg);
                msg = NULL;
            } else {
                iResult = -1;
            }
        }
        LOGD("LinuxMsg>> RecvMsg iResult: %d, msqid: %d, msg: %s", iResult, iQid, pcMsg);
    }

    return iResult;
}

//--------------------------------------------------------------------------------------
// 消息队列相关
int g_iNetDriverMsgQid;  // GPRS/CDMA/handle 驱动的消息队列
int g_iAvPlayMsgQid;     // 音视频播放驱动的消息队列
int g_iPeripheralMsgQid; // 接口管理驱动的消息队列
int g_iGpsMsgQid;        // GPS驱动的消息队列
int g_iSystemMsgQid;     // 系统进程的消息队列
int g_iSchMsgQid;        // 调度进程的消息队列
int g_iStationMsgQid;    // 报站进程的消息队列
int g_iMonitorMsgQid;    // 监控进程的消息队列
int g_iAdtMsgQid;        // 广告进程的消息队列
int g_iUpdateMsgQid;     // 升级进程的消息队列
int g_iWdtMsgQid;        // 看门狗进程的消息队列
int g_iControllerMsgQid; // 安卓控制器消息队列
int g_iJniComMsgQid;     // 安卓JNI串口消息队列

//--------------------------------------------------------------------------------------


void InitMsgQueue(void) {
    // 建立网络驱动进程的消息标识符
    g_iNetDriverMsgQid = CreatMsgQueue(KEY_MSG_NET_DRIVER);

    // 建立系统级驱动进程的消息标识符
    g_iAvPlayMsgQid = CreatMsgQueue(KEY_MSG_AV_PLAY);

    // 建立系统级驱动进程的消息标识符
    g_iPeripheralMsgQid = CreatMsgQueue(KEY_MSG_PERIPHERAL);

    // 建立系统级驱动进程的消息标识符
    g_iGpsMsgQid = CreatMsgQueue(KEY_MSG_GPS);

    // 系统进程的消息标识符
    g_iSystemMsgQid = CreatMsgQueue(KEY_MSG_SYSTEM);

    // 调度进程的消息队列
    g_iSchMsgQid = CreatMsgQueue(KEY_MSG_SCH);

    // 报站进程的消息队列
    g_iStationMsgQid = CreatMsgQueue(KEY_MSG_STATION);

    // 监控进程的消息队列
    g_iMonitorMsgQid = CreatMsgQueue(KEY_MSG_MONITOR);

    // 广告进程的消息队列
    g_iAdtMsgQid = CreatMsgQueue(KEY_MSG_ADVERTISE);

    // 广告进程的消息队列
    g_iUpdateMsgQid = CreatMsgQueue(KEY_MSG_UPDATE);

    // 看门狗清零的消息队列
    g_iWdtMsgQid = CreatMsgQueue(KEY_MSG_WATCHDOG);

    // 安卓控制器消息队列
    g_iControllerMsgQid = CreatMsgQueue(KEY_MSG_CONTROLLER);

    // 安卓JNI串口消息队列
    g_iJniComMsgQid = CreatMsgQueue(KEY_MSG_JNI_COM);

    LOGD("LinuxMsg>> InitMsgQueue NetDriverMsg: %d, AvPlayMsg: %d", g_iNetDriverMsgQid,
         g_iAvPlayMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue PeripheralMsg: %d, GpsMsg: %d", g_iPeripheralMsgQid,
         g_iGpsMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue SystemMsg: %d, SchMsg: %d", g_iSystemMsgQid, g_iSchMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue AdtMsg: %d, UpdateMsg: %d", g_iAdtMsgQid, g_iUpdateMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue StationMsg: %d, MonitorMsg: %d", g_iStationMsgQid,
         g_iMonitorMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue WdtMsg: %d, ControllerMsg: %d", g_iWdtMsgQid,
         g_iControllerMsgQid);
    LOGD("LinuxMsg>> InitMsgQueue JniComMsg: %d", g_iJniComMsgQid);
}


int SendMsgToNetDriver(const char *pcMsg, int iLen) {
    return SendMsg(g_iNetDriverMsgQid, pcMsg, iLen);
}

int SendMsgToAvPlay(const char *pcMsg, int iLen) {
    return SendMsg(g_iAvPlayMsgQid, pcMsg, iLen);
}

int SendMsgToPeripheral(const char *pcMsg, int iLen) {
    return SendMsg(g_iPeripheralMsgQid, pcMsg, iLen);
}

int SendMsgToGps(const char *pcMsg, int iLen) {
    return SendMsg(g_iGpsMsgQid, pcMsg, iLen);
}

int SendMsgToSystem(const char *pcMsg, int iLen) {
    return SendMsg(g_iSystemMsgQid, pcMsg, iLen);
}

int SendMsgToSch(const char *pcMsg, int iLen) {
    return SendMsg(g_iSchMsgQid, pcMsg, iLen);
}

int SendMsgToStation(const char *pcMsg, int iLen) {
    return SendMsg(g_iStationMsgQid, pcMsg, iLen);
}

int SendMsgToMonitor(const char *pcMsg, int iLen) {
    return SendMsg(g_iMonitorMsgQid, pcMsg, iLen);
}

int SendMsgToAdt(const char *pcMsg, int iLen) {
    return SendMsg(g_iAdtMsgQid, pcMsg, iLen);
}

int SendMsgToUpdate(const char *pcMsg, int iLen) {
    return SendMsg(g_iUpdateMsgQid, pcMsg, iLen);
}

int SendMsgToWdt(const char *pcMsg, int iLen) {
    return SendMsg(g_iWdtMsgQid, pcMsg, iLen);
}

int SendMsgToController(const char *pcMsg, int iLen) {
    return SendMsg(g_iControllerMsgQid, pcMsg, iLen);
}

int SendMsgToJniCom(const char *pcMsg, int iLen) {
    return SendMsg(g_iJniComMsgQid, pcMsg, iLen);
}