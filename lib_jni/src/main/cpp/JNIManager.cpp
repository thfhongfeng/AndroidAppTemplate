//
// Created by tanghongfeng on 5/18/22.
//
#include "jni.h"
#include <string>
#include "include/JniLog.h"
#include "include/PublicType.h"
#include "include/SerialPort.h"
#include "include/GpioPort.h"
#include <map>

extern "C" {
#include "videobuffer.h"
}
extern "C" {
#include "include/message.h"
}

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

using namespace std;

JavaVM *g_jvm = nullptr; // 全局变量，用于保存JavaVM指针

// 多线程异步时不能直接用findClass，jni不允许这么做。只能保存全局的jobject，再使用GetObjectClass找到对应的类
jobject jniManagerCbObj = nullptr;

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeInitJniManager
        (JNIEnv *env, jobject objInstance) {
    int ret = 0;
    jniManagerCbObj = env->NewGlobalRef(objInstance);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeReleaseJniManager
        (JNIEnv *env, jobject objInstance) {
    int ret = 0;
    if (jniManagerCbObj != nullptr) {
        env->DeleteGlobalRef(jniManagerCbObj);
        jniManagerCbObj = nullptr;
    }
    return ret;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_pine_app_jni_JNIManager_nativeSyncRequest
        (JNIEnv *env, jobject /* this */, jstring action, jstring data,
         jint maxSize) {
    char *nativeBuffer;
    int len = 0;
    const char *actionChars = env->GetStringUTFChars(action, 0);
    const char *dataChars = env->GetStringUTFChars(data, 0);

    // todo
    nativeBuffer = (char *) malloc(maxSize);
    len = maxSize;

    env->ReleaseStringUTFChars(action, actionChars);
    env->ReleaseStringUTFChars(data, dataChars);
    if (len < 1) return NULL;
    jstring str = env->NewStringUTF(nativeBuffer);
    free(nativeBuffer);
    return str;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeAsyncRequest
        (JNIEnv *env, jobject /* this */, jstring action, jstring callTag, jstring data) {
    int ret = 0;
    const char *actionChars = env->GetStringUTFChars(action, 0);
    const char *callTagChars = env->GetStringUTFChars(callTag, 0);
    const char *dataChars = env->GetStringUTFChars(data, 0);

    // todo
    // test code begin
    jclass cls = env->GetObjectClass(jniManagerCbObj);
    // 获取静态方法的方法ID
    jmethodID methodId = env->GetStaticMethodID(cls, "onResponse",
                                                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    // 调用静态方法
    env->CallStaticVoidMethod(cls, methodId, action, callTag, data);
    // test code end

    env->ReleaseStringUTFChars(action, actionChars);
    env->ReleaseStringUTFChars(callTag, callTagChars);
    env->ReleaseStringUTFChars(data, dataChars);
    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_pine_app_jni_JNIManager_nativeCameraFrameRequest
        (JNIEnv *env, jobject /* this */, jint cameraIndex, jint width, jint height, jint maxSize) {
    unsigned char *nativeBuffer;
    int len = 0;

    // todo
    // test code begin
    nativeBuffer = (unsigned char *) malloc(maxSize);
    nativeBuffer[0] = 0xA3;
    nativeBuffer[1] = 0x9b;
    nativeBuffer[2] = 0xd1;
    nativeBuffer[3] = 0xe5;
    for (int i = 4; i < maxSize; i++) {
        nativeBuffer[i] = 0x01;
        len++;
    }
    // test code end

    if (len < 1) return NULL;
    jbyteArray byteArray;
    jbyte *bytes = reinterpret_cast<jbyte *>(nativeBuffer);
    byteArray = env->NewByteArray(len);
    env->SetByteArrayRegion(byteArray, 0, len, bytes);
    free(nativeBuffer);
    return byteArray;
}

extern "C" void
camera_frame_callback(int ch, unsigned char *ptr, int size, int mtype, int fn, unsigned long ts) {
    LOGD("mids_vbuf => camera_frame_callback ch:%d, mtype:%d, fn:%d,ts:%d, size:%d",
         ch, mtype, fn, ts, size);
    if (g_jvm == nullptr) {
        return;
    }
    JNIEnv *env;
    bool mNeedDetach = false;
    //获取当前native线程是否有没有被附加到jvm环境中
    int getEnvStat = (*g_jvm).GetEnv((void **) &env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        //如果没有， 主动附加到jvm环境中，获取到env
        if (g_jvm->AttachCurrentThread(&env, NULL) != JNI_OK) {
            g_jvm->DetachCurrentThread();
        }
        mNeedDetach = JNI_TRUE;
    }
    if (env == nullptr) {
        return;
    }
    if (jniManagerCbObj == nullptr) {
        return;
    }
    // 创建一个Java层的byte数组来接收数据
    jbyteArray javaByteArray = env->NewByteArray(size);
    if (javaByteArray == nullptr) {
        return;
    }
    // 将C/C++层的数据复制到Java层的byte数组中
    env->SetByteArrayRegion(javaByteArray, 0, size, reinterpret_cast<jbyte *>(ptr));
    // 调用Java层的静态方法，并传递byte数组
    jclass javaClass = env->GetObjectClass(jniManagerCbObj);
    if (javaClass == nullptr) {
        return;
    }
    jmethodID javaMethodId = env->GetStaticMethodID(javaClass, "cameraFrameCallback", "(I[BIIIJ)V");
    if (javaMethodId == nullptr) {
        return;
    }
    env->CallStaticVoidMethod(javaClass, javaMethodId, ch, javaByteArray, size,
                              mtype, fn, (jlong) ts);
    // 清理本地引用
    env->DeleteLocalRef(javaByteArray);
    //释放当前线程
    if (mNeedDetach) {
        g_jvm->DetachCurrentThread();
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeInitCCamera
        (JNIEnv *env, jobject objInstance /* this */) {
    int ret = 0;
    mids_frame_cb T_cb = camera_frame_callback;
    LOGD("mids_vbuf => mids_vbuf_init");
    ret = mids_vbuf_init(T_cb);
    LOGD("mids_vbuf => mids_vbuf_init ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeStartCCameraFrame
        (JNIEnv *env, jobject /* this */, jint cameraIndex) {
    int ret = 0;
    LOGD("mids_vbuf => mids_vbuf_start_frame -> %d", cameraIndex);
    ret = mids_vbuf_start_frame(cameraIndex);
    LOGD("mids_vbuf => mids_vbuf_start_frame ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeStopCCameraFrame
        (JNIEnv *env, jobject /* this */) {
    int ret = 0;
    LOGD("mids_vbuf => mids_vbuf_stop_frame");
    ret = mids_vbuf_stop_frame();
    LOGD("mids_vbuf => mids_vbuf_stop_frame ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_pine_app_jni_JNIManager_nativeReleaseCCamera
        (JNIEnv *env, jobject /* this */) {
    int ret = 0;
    LOGD("mids_vbuf => mids_vbuf_deinit");
    ret = mids_vbuf_deinit();
    LOGD("mids_vbuf => mids_vbuf_deinit ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeInitMsgQueue
        (JNIEnv *env, jobject jclazz, jint clearQueueFlag) {
    return InitMsgQueue(clearQueueFlag);
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeReInitMsgQueue
        (JNIEnv *env, jobject jclazz, jint msgType, jint clearQueueFlag) {
    if (msgType < 0) {
        return InitMsgQueue(clearQueueFlag);
    } else if (msgType == 1) {
        return InitNetDriverMsgQueue(clearQueueFlag);
    } else if (msgType == 2) {
        return InitAdtMsgQueue(clearQueueFlag);
    } else if (msgType == 3) {
        return InitPeripheralMsgQueue(clearQueueFlag);
    } else if (msgType == 4) {
        return InitGpsMsgQueue(clearQueueFlag);
    } else if (msgType == 5) {
        return InitSystemMsgQueue(clearQueueFlag);
    } else if (msgType == 6) {
        return InitSchMsgQueue(clearQueueFlag);
    } else if (msgType == 7) {
        return InitStationMsgQueue(clearQueueFlag);
    } else if (msgType == 8) {
        return InitMonitorMsgQueue(clearQueueFlag);
    } else if (msgType == 9) {
        return InitAdtMsgQueue(clearQueueFlag);
    } else if (msgType == 10) {
        return InitUpdateMsgQueue(clearQueueFlag);
    } else if (msgType == 11) {
        return InitWdtMsgQueue(clearQueueFlag);
    } else if (msgType == 12) {
        return InitJniComMsgQueue(clearQueueFlag);
    } else if (msgType == 99) {
        return InitControllerMsgQueue(clearQueueFlag);
    }
    return -1;
}

extern "C" JNIEXPORT jint JNICALL Java_com_minicreate_app_jni_JNIManager_nativeSendMsg
        (JNIEnv *env, jobject jclazz, jint msgType, jstring data, jint size) {
    const char *dataChars = env->GetStringUTFChars(data, 0);
    int ret = -1;
    if (msgType == 1) {
        ret = SendMsgToNetDriver(dataChars, size);
    } else if (msgType == 2) {
        ret = SendMsgToAvPlay(dataChars, size);
    } else if (msgType == 3) {
        ret = SendMsgToPeripheral(dataChars, size);
    } else if (msgType == 4) {
        ret = SendMsgToGps(dataChars, size);
    } else if (msgType == 5) {
        ret = SendMsgToSystem(dataChars, size);
    } else if (msgType == 6) {
        ret = SendMsgToSch(dataChars, size);
    } else if (msgType == 7) {
        ret = SendMsgToStation(dataChars, size);
    } else if (msgType == 8) {
        ret = SendMsgToMonitor(dataChars, size);
    } else if (msgType == 9) {
        ret = SendMsgToAdt(dataChars, size);
    } else if (msgType == 10) {
        ret = SendMsgToUpdate(dataChars, size);
    } else if (msgType == 11) {
        ret = SendMsgToWdt(dataChars, size);
    } else if (msgType == 12) {
        ret = SendMsgToJniCom(dataChars, size);
    } else if (msgType == 99) {
        ret = SendMsgToController(dataChars, size);
    }
    env->ReleaseStringUTFChars(data, dataChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeSendMsg
        (JNIEnv *env, jobject jclazz, jint msgType, jstring data, jint size) {
    const char *dataChars = env->GetStringUTFChars(data, 0);
    int ret = -1;
    if (msgType == 1) {
        ret = SendMsgToNetDriver(dataChars, size);
    } else if (msgType == 2) {
        ret = SendMsgToAvPlay(dataChars, size);
    } else if (msgType == 3) {
        ret = SendMsgToPeripheral(dataChars, size);
    } else if (msgType == 4) {
        ret = SendMsgToGps(dataChars, size);
    } else if (msgType == 5) {
        ret = SendMsgToSystem(dataChars, size);
    } else if (msgType == 6) {
        ret = SendMsgToSch(dataChars, size);
    } else if (msgType == 7) {
        ret = SendMsgToStation(dataChars, size);
    } else if (msgType == 8) {
        ret = SendMsgToMonitor(dataChars, size);
    } else if (msgType == 9) {
        ret = SendMsgToAdt(dataChars, size);
    } else if (msgType == 10) {
        ret = SendMsgToUpdate(dataChars, size);
    } else if (msgType == 11) {
        ret = SendMsgToWdt(dataChars, size);
    } else if (msgType == 12) {
        ret = SendMsgToJniCom(dataChars, size);
    } else if (msgType == 99) {
        ret = SendMsgToController(dataChars, size);
    }
    env->ReleaseStringUTFChars(data, dataChars);
    return ret;
}

extern "C" JNIEXPORT jstring JNICALL Java_com_pine_app_jni_JNIManager_nativeGetMsg
        (JNIEnv *env, jobject jclazz, jint msgType) {
    int ret = -1;
    char data[2 * MAX_MESSAGE_LEN + 1];
    if (msgType == 1) {
        ret = RecvMsg(g_iNetDriverMsgQid, data);
    } else if (msgType == 2) {
        ret = RecvMsg(g_iAvPlayMsgQid, data);
    } else if (msgType == 3) {
        ret = RecvMsg(g_iPeripheralMsgQid, data);
    } else if (msgType == 4) {
        ret = RecvMsg(g_iGpsMsgQid, data);
    } else if (msgType == 5) {
        ret = RecvMsg(g_iSystemMsgQid, data);
    } else if (msgType == 6) {
        ret = RecvMsg(g_iSchMsgQid, data);
    } else if (msgType == 7) {
        ret = RecvMsg(g_iStationMsgQid, data);
    } else if (msgType == 8) {
        ret = RecvMsg(g_iMonitorMsgQid, data);
    } else if (msgType == 9) {
        ret = RecvMsg(g_iAdtMsgQid, data);
    } else if (msgType == 10) {
        ret = RecvMsg(g_iUpdateMsgQid, data);
    } else if (msgType == 11) {
        ret = RecvMsg(g_iWdtMsgQid, data);
    } else if (msgType == 12) {
        ret = RecvMsg(g_iJniComMsgQid, data);
    } else if (msgType == 99) {
        ret = RecvMsg(g_iControllerMsgQid, data);
    }
    if (ret != -1) {
        jstring result = (env)->NewStringUTF(data);
        return result;
    } else {
        return NULL;
    }
}

static map<string, SerialPort *> serialPortMap;

static SerialPort *getSerialPort(const char *path, bool remove) {
    string pathStr = path;
    map<string, SerialPort *>::iterator it = serialPortMap.find(pathStr);
    if (it != serialPortMap.end()) {
        string key = it->first;
        SerialPort *serialPort = it->second;
        if (remove) {
            serialPortMap.erase(it);
        }
        return serialPort;
    }
    return NULL;
}

static SerialPort *createSerialPort(const char *path) {
    SerialPort *serialPort = getSerialPort(path, false);
    if (serialPort == NULL) {
        serialPort = new SerialPort(path);
        string pathStr = path;
        serialPortMap[pathStr] = serialPort;
    }
    return serialPort;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeOpenSerialPort
        (JNIEnv *env, jobject /* this */, jstring path, jint baudRate, jint dataBits, jint stopBits,
         jchar parity) {
    SerialPortConfig config;
    config = SerialPortConfig();
    config.baudrate = baudRate;
    config.databits = dataBits;
    config.stopbits = stopBits;
    config.parity = parity;
    const char *pathChars = env->GetStringUTFChars(path, 0);
    SerialPort *serialPort = createSerialPort(pathChars);
    int ret = serialPort->openSerialPort(config);
    env->ReleaseStringUTFChars(path, pathChars);
    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_pine_app_jni_JNIManager_nativeReadSerialPort
        (JNIEnv *env, jobject /* this */, jstring path, jint maxSize) {
    BYTE buf[maxSize];
    int len = 0;
    const char *pathChars = env->GetStringUTFChars(path, 0);
    SerialPort *serialPort = getSerialPort(pathChars, false);
    if (serialPort != NULL) {
        len = serialPort->readData(buf, maxSize);
    }
    env->ReleaseStringUTFChars(path, pathChars);
    if (len < 1) return NULL;
    jbyteArray byteArray;
    jbyte *bytes = reinterpret_cast<jbyte *>(buf);
    byteArray = env->NewByteArray(len);
    env->SetByteArrayRegion(byteArray, 0, len, bytes);
    return byteArray;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeWriteSerialPort
        (JNIEnv *env, jobject /* this */, jstring path, jbyteArray data) {
    jbyte *array = env->GetByteArrayElements(data, 0);
    BYTE *bytes = reinterpret_cast<BYTE *>(array);
    int arrayLength = env->GetArrayLength(data);
    const char *pathChars = env->GetStringUTFChars(path, 0);
    SerialPort *serialPort = getSerialPort(pathChars, false);
    int ret = FALSE;
    if (serialPort != NULL) {
        ret = serialPort->writeData(bytes, arrayLength);
    }
    env->ReleaseStringUTFChars(path, pathChars);
    env->ReleaseByteArrayElements(data, array, 0);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeSetSerialMode
        (JNIEnv *env, jobject /* this */, jstring path, jint mode) {
    const char *pathChars = env->GetStringUTFChars(path, 0);
    string pathStr = pathChars;
    SerialPort *serialPort = getSerialPort(pathChars, false);
    int ret = FALSE;
    if (serialPort != NULL) {
        ret = serialPort->setMode(mode);
    }
    env->ReleaseStringUTFChars(path, pathChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeCloseSerialPort
        (JNIEnv *env, jobject /* this */, jstring path) {
    const char *pathChars = env->GetStringUTFChars(path, 0);
    SerialPort *serialPort = getSerialPort(pathChars, true);
    int ret = TRUE;
    if (serialPort != NULL) {
        ret = serialPort->closePort();
    }
    env->ReleaseStringUTFChars(path, pathChars);
    delete serialPort;
    return ret;
}


static map<string, GpioPort *> gpioPortMap;

static GpioPort *getGpioPort(const char *port, bool remove) {
    string portStr = port;
    map<string, GpioPort *>::iterator it = gpioPortMap.find(portStr);
    if (it != gpioPortMap.end()) {
        string key = it->first;
        GpioPort *gpioPort = it->second;
        if (remove) {
            gpioPortMap.erase(it);
        }
        return gpioPort;
    }
    return NULL;
}

static GpioPort *createGpioPort(const char *port, int len) {
    GpioPort *gpioPort = getGpioPort(port, false);
    if (gpioPort == NULL) {
        gpioPort = new GpioPort(port, len);
        string portStr = port;
        gpioPortMap[portStr] = gpioPort;
    }
    return gpioPort;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeOpenGpioPort
        (JNIEnv *env, jobject /* this */, jstring port, jint len) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = createGpioPort(portChars, len);
    int ret = gpioPort->openPort();
    env->ReleaseStringUTFChars(port, portChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeCloseGpioPort
        (JNIEnv *env, jobject /* this */, jstring port) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = getGpioPort(portChars, true);
    int ret = TRUE;
    if (gpioPort != NULL) {
        ret = gpioPort->closePort();
    }
    env->ReleaseStringUTFChars(port, portChars);
    delete gpioPort;
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeWriteGpioStatus
        (JNIEnv *env, jobject /* this */, jstring port, jint data) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = getGpioPort(portChars, false);
    int ret = FALSE;
    if (gpioPort != NULL) {
        ret = gpioPort->writeStatus(data);
    }
    env->ReleaseStringUTFChars(port, portChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeReadGpioStatus
        (JNIEnv *env, jobject /* this */, jstring port) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = getGpioPort(portChars, false);
    int ret = -1;
    if (gpioPort != NULL) {
        ret = gpioPort->readStatus();
    }
    env->ReleaseStringUTFChars(port, portChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeWriteGpioDirect
        (JNIEnv *env, jobject /* this */, jstring port, jint data) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = getGpioPort(portChars, false);
    int ret = FALSE;
    if (gpioPort != NULL) {
        ret = gpioPort->setDirection(data);
    }
    env->ReleaseStringUTFChars(port, portChars);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL Java_com_pine_app_jni_JNIManager_nativeReadGpioDirect
        (JNIEnv *env, jobject /* this */, jstring port) {
    const char *portChars = env->GetStringUTFChars(port, 0);
    GpioPort *gpioPort = getGpioPort(portChars, false);
    int ret = -1;
    if (gpioPort != NULL) {
        ret = gpioPort->getDirection();
    }
    env->ReleaseStringUTFChars(port, portChars);
    return ret;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    LOGD("JNI_OnLoad");
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_OK) {
        //JavaVM是虚拟机在JNI中的表示，等下再其他线程回调java层需要用到
        env->GetJavaVM(&g_jvm);
    }
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
    LOGD("JNI_OnUnload");
}