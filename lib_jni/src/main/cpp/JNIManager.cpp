//
// Created by tanghongfeng on 5/18/22.
//
#include "jni.h"
#include <string>
#include "include/JniLog.h"
#include "include/PublicType.h"
#include <map>

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

using namespace std;

JavaVM *g_jvm = nullptr; // 全局变量，用于保存JavaVM指针

// 多线程异步时不能直接用findClass，jni不允许这么做。只能保存全局的jobject，再使用GetObjectClass找到对应的类
jobject jniManagerCbObj = nullptr;

extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeInitJniManager
        (JNIEnv *env, jobject objInstance) {
    int ret = 0;
    jniManagerCbObj = env->NewGlobalRef(objInstance);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeReleaseJniManager
        (JNIEnv *env, jobject objInstance) {
    int ret = 0;
    if (jniManagerCbObj != nullptr) {
        env->DeleteGlobalRef(jniManagerCbObj);
        jniManagerCbObj = nullptr;
    }
    return ret;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeSyncRequest
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
Java_com_minicreate_app_jni_JNIManager_nativeAsyncRequest
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
Java_com_minicreate_app_jni_JNIManager_nativeCameraFrameRequest
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

///////////////////////////////////////////////////////////////
///////////////////////// 异步回调示例  /////////////////////////
///////////////////////////////////////////////////////////////

extern "C" void
camera_frame_callback(int ch, unsigned char *ptr, int size, int mtype, int fn, unsigned long ts) {
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
Java_com_minicreate_app_jni_JNIManager_nativeInitCCamera
        (JNIEnv *env, jobject objInstance /* this */) {
    int ret = 0;
    LOGD("c_camera => nativeInitCCamera");

    // todo
    /**
     * 传入camera_frame_callback到多线程的本地（Native）处理代码中
     * 如：
     * camera_frame_cb T_cb = camera_frame_callback;
     * ret = camera_vbuf_init(T_cb);
     */

    LOGD("c_camera => nativeInitCCamera ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeStartCCameraFrame
        (JNIEnv *env, jobject /* this */, jint cameraIndex) {
    int ret = 0;
    LOGD("c_camera => nativeStartCCameraFrame -> %d", cameraIndex);

    // todo

    LOGD("c_camera => nativeStartCCameraFrame ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeStopCCameraFrame
        (JNIEnv *env, jobject /* this */) {
    int ret = 0;
    LOGD("c_camera => mids_vbuf_stop_frame");

    // todo

    LOGD("c_camera => mids_vbuf_stop_frame ret:%d", ret);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeReleaseCCamera
        (JNIEnv *env, jobject /* this */) {
    int ret = 0;
    LOGD("mids_vbuf => mids_vbuf_deinit");

    // todo

    LOGD("mids_vbuf => mids_vbuf_deinit ret:%d", ret);
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