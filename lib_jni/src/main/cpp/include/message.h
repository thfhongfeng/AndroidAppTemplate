
#ifndef __MESSAGE_H__
#define __MESSAGE_H__

#include "includes.h"
#include "JniLog.h"

// 消息的最大长度
//#define MAX_MESSAGE_LEN   1500
// 不要超过256K
#define MAX_MESSAGE_LEN   131072
#define DATA_FILE_DIR "/tmp/msgData/"



//--------------------------------------------------------------------------------------
// 消息队列关键字
//--------------------------------------------------------------------------------------
#define KEY_MSG_NET_DRIVER  0x2010     // 系统驱动消息队列
#define KEY_MSG_AV_PLAY     0x2020     // 系统驱动消息队列
#define KEY_MSG_PERIPHERAL  0x2030     // 系统驱动消息队列
#define KEY_MSG_GPS         0x2040     // 系统驱动消息队列
#define KEY_MSG_SYSTEM      0x2050     // 系统消息队列
#define KEY_MSG_SCH         0x2060     // GPRS消息队列
#define KEY_MSG_STATION     0x2070     // 报站消息队列
#define KEY_MSG_MONITOR     0x2080     // 监控消息队列
#define KEY_MSG_ADVERTISE   0x2090     // 广告消息队列
#define KEY_MSG_UPDATE      0x20A0     // 升级消息队列
#define KEY_MSG_WATCHDOG    0x20B0     // 看门狗消息队列
#define KEY_MSG_CONTROLLER  0x20C0     // 安卓控制器消息队列
#define KEY_MSG_JNI_COM     0x20C1     // 安卓JNI串口消息队列

extern int g_iNetDriverMsgQid;
extern int g_iAvPlayMsgQid;
extern int g_iPeripheralMsgQid;
extern int g_iGpsMsgQid;
extern int g_iSystemMsgQid;
extern int g_iSchMsgQid;
extern int g_iStationMsgQid; 
extern int g_iMonitorMsgQid;
extern int g_iAdtMsgQid;
extern int g_iUpdateMsgQid;
extern int g_iWdtMsgQid;
extern int g_iControllerMsgQid;
extern int g_iJniComMsgQid;
//--------------------------------------------------------------------------------------




// 消息
typedef struct {
	long mtype;
	char mtext[MAX_MESSAGE_LEN];
}msg_info;


extern int CreatMsgQueue(key_t key);
extern int SendMsg(int msqid, const char* msg, int iLen);
extern int RecvMsg(int iQid, char* pcMsg);



extern void InitMsgQueue(void);



extern int SendMsgToNetDriver(const char * pcMsg, int iLen);

extern int SendMsgToAvPlay(const char * pcMsg, int iLen);

extern int SendMsgToPeripheral(const char * pcMsg, int iLen);

extern int SendMsgToGps(const char * pcMsg, int iLen);

extern int SendMsgToSystem(const char * pcMsg, int iLen);

extern int SendMsgToSch(const char * pcMsg, int iLen);

extern int SendMsgToStation(const char * pcMsg, int iLen);

extern int SendMsgToMonitor(const char * pcMsg, int iLen);

extern int SendMsgToAdt(const char * pcMsg, int iLen);

extern int SendMsgToUpdate(const char * pcMsg, int iLen);

extern int SendMsgToWdt(const char * pcMsg, int iLen);

extern int SendMsgToController(const char * pcMsg, int iLen);

extern int SendMsgToJniCom(const char * pcMsg, int iLen);

#endif





