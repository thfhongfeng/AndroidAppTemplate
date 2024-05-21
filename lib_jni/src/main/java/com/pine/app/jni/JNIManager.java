package com.pine.app.jni;

import androidx.annotation.NonNull;

import com.pine.app.jni.listener.IJniListener;
import com.pine.app.jni.listener.IRequestListener;

import java.nio.charset.StandardCharsets;

public class JNIManager {
    private final String TAG = this.getClass().getSimpleName();

    static {
        System.loadLibrary("jniLib");
    }

    private volatile static boolean mIsInit;

    public synchronized static void init() {
        init("com.minicreate.app.jni.JniObserver", "onResponse", "onFail", "onReceive");
    }

    /**
     * @param jniObserverClass native代码回调的类（全限定名）
     * @param responseMethod   异步请求（asyncRequest）中正常响应回调方法
     * @param failMethod       异步请求（asyncRequest）中失败响应回调方法
     * @param listenerMethod   监听（listen）某些行为回调方法
     */
    public synchronized static void init(String jniObserverClass, String responseMethod,
                                         String failMethod, String listenerMethod) {
        mIsInit = nativeInitRequest(jniObserverClass, responseMethod, failMethod, listenerMethod) == 1;
    }

    public synchronized static boolean isInit() {
        return mIsInit;
    }

    public static native int nativeInitRequest(String callbackClass, String responseMethod,
                                               String failMethod, String listenerMethod);

    public native byte[] nativeSyncRequest(String action, String data, int maxSize);

    public native int nativeAsyncRequest(String action, String callTag, String data);

    /**
     * 同步请求
     *
     * @param action  请求的业务行为标识
     * @param data    请求参数数据
     * @param maxSize 返回的数据最大字节数
     * @return
     * @throws JniException
     */
    public String syncRequest(@NonNull String action, @NonNull String data, int maxSize) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        byte[] bytes = nativeSyncRequest(action, data, maxSize);
        String str = null;
        if (bytes != null) {
            str = new String(bytes, StandardCharsets.UTF_8);
        }
        return str;
    }

    /**
     * 异步请求
     *
     * @param action   请求的业务行为标识
     * @param callTag  请求者的标识
     * @param data     请求参数数据
     * @param listener 请求回调
     * @return
     * @throws JniException
     */
    public boolean asyncRequest(@NonNull String action, @NonNull String callTag, String data,
                                IRequestListener listener) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        JniObserver.addCallback(action, callTag, listener);
        int ret = nativeAsyncRequest(action, callTag, data);
        return ret == 1;
    }

    /**
     * 监听信息（会先返回最近一条信息）
     *
     * @param action   请求的业务行为标识
     * @param callTag  请求者的标识
     * @param listener
     * @throws JniException
     */
    public void listenPersist(@NonNull String action, @NonNull String callTag, IJniListener listener) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        JniObserver.listenPersist(action, callTag, listener);
    }

    /**
     * 监听信息
     *
     * @param action   请求的业务行为标识
     * @param callTag  请求者的标识
     * @param listener
     * @throws JniException
     */
    public void listen(@NonNull String action, @NonNull String callTag, IJniListener listener) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        JniObserver.listen(action, callTag, listener);
    }

    public void unListen(@NonNull String callTag) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        JniObserver.unListen(callTag);
    }

    public void unListen(@NonNull String action, @NonNull String callTag) throws JniException {
        if (!isInit()) {
            throw new JniException("not init");
        }
        JniObserver.unListen(action, callTag);
    }
}
