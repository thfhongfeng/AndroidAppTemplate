#ifndef MINICREATEHELPER_MINICREATE_LOG
#define MINICREATEHELPER_MINICREATE_LOG
#include <android/log.h>
// Log标记
#define  MINICREATE_LOG_TAG "MinicreateJNI"
// 各个优先级的宏定义
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, MINICREATE_LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, MINICREATE_LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, MINICREATE_LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_write(ANDROID_LOG_WARN, MINICREATE_LOG_TAG, __VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, MINICREATE_LOG_TAG, __VA_ARGS__)
#endif //MINICREATEHELPER_MINICREATE_LOG