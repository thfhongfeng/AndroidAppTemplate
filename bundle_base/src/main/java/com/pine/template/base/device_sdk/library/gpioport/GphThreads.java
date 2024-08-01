package com.pine.template.base.device_sdk.library.gpioport;

import com.pine.tool.util.LogUtils;

/**
 * gpio读写线程
 */
public class GphThreads {
    private final String TAG = "GphThreads";

    /**
     * 读写线程
     */
    private Thread readThread, writeThread;

    private Object readWaitLock = new Object();

    /**
     * 数据处理
     */
    private GphDataProcess processingData;

    private GpioPortConfig config;

    private boolean readRunning;

    public GphThreads(GphDataProcess processingData, GpioPortConfig config) {
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
        long lasTimeStamp = System.currentTimeMillis();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (readRunning) {
                    if (config.readInterval > 0) {
                        long interval = System.currentTimeMillis() - lasTimeStamp;
                        if (interval < config.readInterval) {
                            try {
                                Thread.sleep(config.readInterval - interval);
                            } catch (InterruptedException e) {
                                LogUtils.e(TAG, "readThread InterruptedException");
                                Thread.currentThread().interrupt();
                            }
                        }
                        lasTimeStamp = System.currentTimeMillis();
                    }
                    // 读取数据
                    int status = GpioPortJNI.readPort(config.port);
                    if (readRunning && status > -1) {
                        processingData.processingRecData(status);
                    }
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
                if (!readRunning) {
                    processingData.writeData();
                }
            }
        }
    }

    public int readDirect() {
        return GpioPortJNI.readDirect(config.port);
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
