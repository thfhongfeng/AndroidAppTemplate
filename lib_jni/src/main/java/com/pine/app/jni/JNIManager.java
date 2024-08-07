package com.pine.app.jni;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pine.app.jni.listener.ICameraFrameListener;
import com.pine.app.jni.listener.IJniListener;
import com.pine.app.jni.listener.IRequestListener;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

public class JNIManager {
    private final String TAG = this.getClass().getSimpleName();

    static {
        System.loadLibrary("RequestJni");
    }

    private volatile static boolean mIsInit;

    public static synchronized boolean isInit() {
        return mIsInit;
    }

    public native int nativeInitJniManager();

    public native int nativeReleaseJniManager();

    public synchronized boolean checkAndTryInit() {
        if (!isInit()) {
            mIsInit = nativeInitJniManager() == 0;
        }
        return isInit();
    }

    public void init() {
        synchronized (JNIManager.class) {
            if (!isInit()) {
                mIsInit = nativeInitJniManager() == 0;
            }
            if (isInit()) {
                nativeInitMsgQueue(1);
            }
        }
    }

    public void release() {
        synchronized (JNIManager.class) {
            if (isInit()) {
                nativeReleaseJniManager();
                mIsInit = false;
            }
        }
    }

    public native int nativeOpenGpioPort(String port, int len);

    public native int nativeCloseGpioPort(String port);

    public native int nativeWriteGpioStatus(String port, int status);

    public native int nativeReadGpioStatus(String port);

    public native int nativeWriteGpioDirect(String port, int direct);

    public native int nativeReadGpioDirect(String port);

    public native int nativeOpenSerialPort(String path, int baudRate, int dataBits,
                                           int stopBits, char parity);

    public native byte[] nativeReadSerialPort(String path, int maxSize);

    public native int nativeWriteSerialPort(String path, byte[] data);

    public native int nativeSetSerialMode(String path, int mode);

    public native int nativeCloseSerialPort(String path);

    private HashMap<String, Object> mLockMap = new HashMap<>();

    private Object getLock(String port) {
        synchronized (mLockMap) {
            if (mLockMap.containsKey(port) && mLockMap.get(port) != null) {
                return mLockMap.get(port);
            } else {
                Object lock = new Object();
                mLockMap.put(port, lock);
                return lock;
            }
        }
    }

    public int openGpioPort(String port) {
        Log.d(TAG, "openGpioPort port:" + port);
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeOpenGpioPort(port, port.length());
        }
    }

    public int closeGpioPort(String port) {
        Log.d(TAG, "closeGpioPort port:" + port);
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeCloseGpioPort(port);
        }
    }

    public int writeGpioStatus(String port, int status) {
        Log.d(TAG, "writeGpioStatus port:" + port + " status:" + status);
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeWriteGpioStatus(port, status);
        }
    }

    public int readGpioStatus(String port) {
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeReadGpioStatus(port);
        }
    }

    public int writeGpioDirect(String port, int direct) {
        Log.d(TAG, "writeGpioDirect port:" + port + " direct:" + direct);
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeWriteGpioDirect(port, direct);
        }
    }

    public int readGpioDirect(String port) {
        Log.d(TAG, "readGpioDirect port:" + port);
        Object lock = getLock(port);
        synchronized (lock) {
            return nativeReadGpioDirect(port);
        }
    }

    public int openSerialPort(String path, int baudRate, int dataBits,
                              int stopBits, char parity) {
        Log.d(TAG, "device:" + path + " prepare to open device");
        File device = new File(path);
        if (device == null || !device.exists()) {
            Log.e(TAG, "device:" + path + " do not exist!");
            return 0;
        }
        /* Check access permission */
        boolean success = device.canRead() && device.canWrite();
        if (!success) {
            return 0;
        }
        int ret = -1;
        Object lock = getLock(path);
        synchronized (lock) {
            ret = nativeOpenSerialPort(path, baudRate, dataBits, stopBits, parity);
        }
        Log.d(TAG, "open device:" + path + ", ret:" + ret);
        return ret;
    }

    public int readSerialPort(String path, int maxSize, byte[] data) {
        int returnValue = -1;
        byte[] responseData = null;
        Object lock = getLock(path);
        synchronized (lock) {
            responseData = nativeReadSerialPort(path, maxSize);
        }

        if (responseData != null) {
            if (responseData.length > data.length) {
                Log.w(TAG, "Buffer to copy response too small: Response length is " +
                        responseData.length + "bytes. Buffer Size is " +
                        data.length + "bytes.");
            }
            System.arraycopy(responseData, 0, data, 0, responseData.length);
            returnValue = responseData.length;
        }
        return returnValue;
    }

    public int writeSerialPort(String path, byte[] data) {
        int ret = 0;
        Object lock = getLock(path);
        synchronized (lock) {
            ret = nativeWriteSerialPort(path, data);
        }
        return ret;
    }

    public int setSerialPortMode(String path, int mode) {
        int ret = 0;
        Object lock = getLock(path);
        synchronized (lock) {
            ret = nativeSetSerialMode(path, mode);
        }
        Log.d(TAG, "set device mode:" + path + ", ret:" + ret);
        return ret;
    }

    public int closeSerialPort(String path) {
        int ret = 0;
        Object lock = getLock(path);
        synchronized (lock) {
            ret = nativeCloseSerialPort(path);
        }
        Log.d(TAG, "close device:" + path + ", ret:" + ret);
        return ret;
    }

    public native int nativeInitMsgQueue(int clearQueueFlag);

    public native int nativeReInitMsgQueue(int msgType, int clearQueueFlag);

    /**
     * @param msgType 1-g_iNetDriverMsgQid
     *                2-g_iAvPlayMsgQid
     *                3-g_iPeripheralMsgQid
     *                4-g_iGpsMsgQid
     *                5-g_iSystemMsgQid
     *                6-g_iSchMsgQid
     *                7-g_iStationMsgQid
     *                8-g_iMonitorMsgQid
     *                9-g_iAdtMsgQid
     *                10-g_iUpdateMsgQid
     *                11-g_iWdtMsgQid
     *                12-g_iJniComMsgQid
     *                99-g_iControllerMsgQid
     * @param msg
     * @param size
     * @return
     */
    public native int nativeSendMsg(int msgType, String msg, int size);

    /**
     * @param msgType 1-g_iNetDriverMsgQid
     *                2-g_iAvPlayMsgQid
     *                3-g_iPeripheralMsgQid
     *                4-g_iGpsMsgQid
     *                5-g_iSystemMsgQid
     *                6-g_iSchMsgQid
     *                7-g_iStationMsgQid
     *                8-g_iMonitorMsgQid
     *                9-g_iAdtMsgQid
     *                10-g_iUpdateMsgQid
     *                11-g_iWdtMsgQid
     *                12-g_iJniComMsgQid
     *                99-g_iControllerMsgQid
     * @return
     */
    public native byte[] nativeGetMsg(int msgType);

    private volatile static boolean mIsMsgQueueInit;

    private Object mMsgLock = new Object();

    public boolean checkAndTryInitMsgQueue(boolean force) {
        synchronized (mMsgLock) {
            if (force) {
                mIsMsgQueueInit = nativeInitMsgQueue(1) == 0;
            } else {
                if (!mIsMsgQueueInit) {
                    mIsMsgQueueInit = nativeInitMsgQueue(1) == 0;
                }
            }
            return mIsMsgQueueInit;
        }
    }

    public boolean reInitMsgQueue(int msgType, boolean clearQueue) {
        synchronized (mMsgLock) {
            nativeReInitMsgQueue(msgType, clearQueue ? 1 : 0);
            return true;
        }
    }

    public boolean sendMessage(int msgType, String msg) throws JniException {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        if (!checkAndTryInitMsgQueue(false)) {
            throw new JniException("not init");
        }
        synchronized (mMsgLock) {
            return nativeSendMsg(msgType, msg, msg.getBytes().length) != -1;
        }
    }

    public String getMessage(int msgType) throws JniException {
        if (!checkAndTryInitMsgQueue(false)) {
            throw new JniException("not init");
        }
        synchronized (mMsgLock) {
            byte[] result = nativeGetMsg(msgType);
            return result == null ? null : new String(result, Charset.forName("UTF-8"));
        }
    }


    public native byte[] nativeSyncRequest(String action, String data, int maxSize);

    public native int nativeAsyncRequest(String action, String callTag, String data);

    public native byte[] nativeCameraFrameRequest(int cameraIndex, int width, int height, int maxSize);

    public static void onReceive(String action, String data) {
        JniObserver.onReceive(action, data);
    }

    public static void onResponse(String action, String callTag, String data) {
        JniObserver.onResponse(action, callTag, data);
    }

    public static void onFail(String action, String callTag, int errCode) {
        JniObserver.onFail(action, callTag, errCode);
    }

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
        Log.d(TAG, "syncRequest action:" + action + ", data:" + data);
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        byte[] result = nativeSyncRequest(action, data, maxSize);
        return result == null ? null : new String(result, Charset.forName("UTF-8"));
    }

    /**
     * 异步请求
     *
     * @param action   请求的业务行为标识
     * @param callTag  本次请求者的标识
     * @param data     请求参数数据
     * @param listener 请求回调
     * @return
     * @throws JniException
     */
    public boolean asyncRequest(@NonNull String action, @NonNull String callTag, String data,
                                IRequestListener listener) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        Log.d(TAG, "asyncRequest action:" + action + ", callTag:" + callTag + ", data:" + data);
        JniObserver.addCallback(action, callTag, data, listener);
        int ret = nativeAsyncRequest(action, callTag, data);
        return ret == 1;
    }

    /**
     * 监听信息（会先返回最近一条信息）
     *
     * @param action   请求的业务行为标识
     * @param callTag  本次请求者的标识
     * @param listener
     * @throws JniException
     */
    public void listenPersist(@NonNull String action, @NonNull String callTag, IJniListener listener) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        JniObserver.listenPersist(action, callTag, listener);
    }

    /**
     * 监听信息
     *
     * @param action   请求的业务行为标识
     * @param callTag  本次请求者的标识
     * @param listener
     * @throws JniException
     */
    public void listen(@NonNull String action, @NonNull String callTag, IJniListener listener) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        JniObserver.listen(action, callTag, listener);
    }

    public void unListen(@NonNull String callTag) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        JniObserver.unListen(callTag);
    }

    public void unListen(@NonNull String action, @NonNull String callTag) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        JniObserver.unListen(action, callTag);
    }

    public byte[] cameraFrameRequest(int cameraIndex, int width, int height, int maxSize) throws JniException {
        if (!checkAndTryInit()) {
            throw new JniException("not init");
        }
        return nativeCameraFrameRequest(cameraIndex, width, height, maxSize);
    }


    public native int nativeInitCCamera();

    public native int nativeStartCCameraFrame(int cameraIndex);

    public native int nativeStopCCameraFrame();

    public native int nativeReleaseCCamera();

    private boolean mCCameraInit;

    private static ICameraFrameListener mCameraFrameListener;

    private static Object mCameraFrameLock = new Object();

    public synchronized boolean initCCamera(ICameraFrameListener listener) {
        if (!checkAndTryInit()) {
            return false;
        }
        mCCameraInit = nativeInitCCamera() == 0;
        if (mCCameraInit) {
            synchronized (mCameraFrameLock) {
                mCameraFrameListener = listener;
            }
        }
        return mCCameraInit;
    }

    public synchronized boolean startCCameraFrame(int cameraIndex) {
        if (!checkAndTryInit()) {
            return false;
        }
        if (!mCCameraInit) {
            return false;
        }
        return nativeStartCCameraFrame(cameraIndex) == 0;
    }

    public synchronized boolean stopCCameraFrame() {
        if (!checkAndTryInit()) {
            return false;
        }
        if (!mCCameraInit) {
            return false;
        }
        return nativeStopCCameraFrame() == 0;
    }

    public synchronized boolean releaseCCamera() {
        if (!checkAndTryInit()) {
            return false;
        }
        int ret = nativeReleaseCCamera();
        synchronized (mCameraFrameLock) {
            mCameraFrameListener = null;
        }
        mCCameraInit = false;
        return ret == 0;
    }

    public static void cameraFrameCallback(int cameraIndex, byte[] ptr, int size, int mtype, int frameIndex, long ts) {
        ICameraFrameListener listener = null;
        synchronized (mCameraFrameLock) {
            listener = mCameraFrameListener;
        }
        if (listener != null) {
            mCameraFrameListener.onFrame(cameraIndex, ptr, size, mtype, frameIndex, ts);
        }
    }
}
