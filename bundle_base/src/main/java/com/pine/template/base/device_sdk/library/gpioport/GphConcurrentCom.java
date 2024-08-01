package com.pine.template.base.device_sdk.library.gpioport;

import com.pine.tool.util.LogUtils;

import java.util.ArrayList;

/**
 * gpio读写命令线程同步控制
 */
public class GphConcurrentCom {
    private final String TAG = this.getClass().getSimpleName();

    private boolean isGet;

    private GphCmdEntity currentCmdEntity;

    private Object writeWaitLock = new Object();

    /**
     * gpio发送命令集合
     */
    private ArrayList<GphCmdEntity> mEntryList = new ArrayList<GphCmdEntity>();

    /**
     * 添加gpio发送命令
     *
     * @param command 命令数据
     */
    public synchronized void addCommands(GphCmdEntity command, int maxDelayCmdCount) {
        if (this.mEntryList.size() > maxDelayCmdCount) {
            this.mEntryList.clear();
        }
        this.mEntryList.add(command);
        synchronized (writeWaitLock) {
            writeWaitLock.notifyAll();
        }
    }

    /**
     * 获取当前命令
     *
     * @return currentCmdEntity
     */
    public GphCmdEntity getCurrentCmdEntity() {
        return currentCmdEntity;
    }

    /**
     * 判断命令是否为空
     *
     * @return
     */
    public synchronized boolean isCmdEmpty() {
        return mEntryList.isEmpty();
    }

    public void checkEntityEmptyAndWait() {
        boolean empty = isCmdEmpty();
        synchronized (writeWaitLock) {
            if (empty) {
                try {
                    LogUtils.d(TAG, "writeThread wait for checkEntityEmptyAndWait");
                    writeWaitLock.wait();
                    LogUtils.d(TAG, "writeThread be notified for checkEntityEmptyAndWait");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LogUtils.e(TAG, "writeThread InterruptedException for checkEntityEmptyAndWait");
                }
            }
        }
    }

    /**
     * 从命令集合中取命令数据
     *
     * @return currentCmdEntity
     */
    public synchronized GphCmdEntity get() {
        while (isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentCmdEntity = mEntryList.remove(0);
        notify();
        return currentCmdEntity;
    }

    /**
     * 命令接收完成
     */
    public synchronized void doneCom() {
        if (isCmdEmpty()) {
            currentCmdEntity = null;
            return;
        }
        while (!isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentCmdEntity = null;
        notify();
    }

    /**
     * 设置读写同步状态
     *
     * @param status
     */
    public void setStatus(boolean status) {
        this.isGet = status;
    }
}
