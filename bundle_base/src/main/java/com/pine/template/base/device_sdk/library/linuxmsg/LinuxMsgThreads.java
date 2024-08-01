package com.pine.template.base.device_sdk.library.linuxmsg;

import android.text.TextUtils;

import com.pine.tool.util.LogUtils;

/**
 * entity读写线程
 */
public class LinuxMsgThreads {
    private final String TAG = "LinuxMsgThreads";

    /**
     * 读写线程
     */
    private Thread readThread, writeThread;

    private Object readWaitLock = new Object();

    /**
     * 数据处理
     */
    private LinuxMsgProcess processingData;

    private LinuxMsgConfig config;

    private boolean readRunning;

    public LinuxMsgThreads(LinuxMsgProcess processingData, LinuxMsgConfig config) {
        this.processingData = processingData;
        this.config = config;
    }

    /**
     * 开启读取数据线程
     */
    public synchronized void startReadThread() {
        if (readThread == null) {
            readThread = new Thread(new ReadThread());
            readThread.start();
        }
    }

    /**
     * 开启发送数据线程
     */
    public synchronized void startWriteThread() {
        if (writeThread == null) {
            writeThread = new Thread(new WriteThread());
            writeThread.start();
        }
    }

    /**
     * 数据读取线程
     */
    public class ReadThread implements Runnable {
        long lastTimeStamp = System.currentTimeMillis();
        String lastData = "";

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (readRunning || !TextUtils.isEmpty(lastData)) {
                    if (config.readInterval > 0) {
                        long interval = System.currentTimeMillis() - lastTimeStamp;
                        if (interval <= config.readInterval) {
                            try {
                                Thread.sleep(config.readInterval - interval);
                            } catch (InterruptedException e) {
                                LogUtils.e(TAG, "readThread InterruptedException");
                                Thread.currentThread().interrupt();
                            }
                        }
                        lastTimeStamp = System.currentTimeMillis();
                    }
                    // 读取数据
                    String data = LinuxMsgJNI.getMsg(config.readMsgType);
                    if (readRunning && !TextUtils.isEmpty(data)) {
                        processingData.processingRecData(data);
                    }
                    lastData = data;
                } else {
                    synchronized (readWaitLock) {
                        try {
                            LogUtils.d(TAG, "readThread wait for read stop");
                            readWaitLock.wait();
                            LogUtils.d(TAG, "readThread be notified for read stop");
                        } catch (InterruptedException e) {
                            LogUtils.e(TAG, "readThread wait InterruptedException for read stop");
                            Thread.currentThread().interrupt();
                            continue;
                        }
                    }
                }
            }
        }
    }

    /**
     * 数据写入线程
     */
    public class WriteThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                processingData.writeData();
            }
        }
    }

    /**
     * 开始读
     */
    public void startRead() {
        readRunning = true;
        synchronized (readWaitLock) {
            readWaitLock.notifyAll();
        }
    }

    /**
     * 停止读
     */
    public void stopRead() {
        readRunning = false;
    }

    /**
     * 停止线程
     */
    public synchronized void stop() {
        if (readThread != null) {
            readThread.interrupt();
            stopRead();
        }
        if (writeThread != null) {
            writeThread.interrupt();
        }
    }
}
