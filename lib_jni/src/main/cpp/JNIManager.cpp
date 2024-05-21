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


extern "C" JNIEXPORT jint JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeInitRequest
        (JNIEnv *env, jclass clazz, jstring callbackClass, jstring responseMethod,
         jstring failMethod, jstring listenerMethod) {
    int ret = 0;
    const char *callbackClassChars = env->GetStringUTFChars(callbackClass, 0);
    const char *responseMethodChars = env->GetStringUTFChars(responseMethod, 0);
    const char *failMethodChars = env->GetStringUTFChars(failMethod, 0);
    const char *listenerMethodChars = env->GetStringUTFChars(listenerMethod, 0);

    // todo
    ret = 1;

    env->ReleaseStringUTFChars(callbackClass, callbackClassChars);
    env->ReleaseStringUTFChars(responseMethod, responseMethodChars);
    env->ReleaseStringUTFChars(failMethod, failMethodChars);
    env->ReleaseStringUTFChars(listenerMethod, listenerMethodChars);
    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_minicreate_app_jni_JNIManager_nativeSyncRequest
        (JNIEnv *env, jobject /* this */, jstring action, jstring data,
         jint maxSize) {
    BYTE buf[maxSize];
    int len = 0;
    const char *actionChars = env->GetStringUTFChars(action, 0);
    const char *dataChars = env->GetStringUTFChars(data, 0);

    // todo

    env->ReleaseStringUTFChars(action, actionChars);
    env->ReleaseStringUTFChars(data, dataChars);
    if (len < 1) return NULL;
    jbyteArray byteArray;
    jbyte *bytes = reinterpret_cast<jbyte *>(buf);
    byteArray = env->NewByteArray(len);
    env->SetByteArrayRegion(byteArray, 0, len, bytes);
    return byteArray;
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
    // 获取MyJNIClass类
    jclass cls = env->FindClass("com/minicreate/app/jni/JniObserver");
    // 获取静态方法的方法ID
    jmethodID methodId = env->GetStaticMethodID(cls, "onResponse",
                                                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    // test code end
    // 调用静态方法
    env->CallStaticVoidMethod(cls, methodId, action, callTag, data);

    env->ReleaseStringUTFChars(action, actionChars);
    env->ReleaseStringUTFChars(callTag, callTagChars);
    env->ReleaseStringUTFChars(data, dataChars);
    return ret;
}