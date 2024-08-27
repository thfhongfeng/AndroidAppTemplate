package com.pine.template.base.device_sdk.library.linuxmsg;

import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * 读写命令线程同步控制
 */
public class LinuxMsgConcurrent {
    private final String TAG = this.getClass().getSimpleName();

    private boolean isGet;

    private LinuxMsgEntity currentEntity;

    private Object writeWaitLock = new Object();

    /**
     * 发送实体集合
     */
    private ArrayList<LinuxMsgEntity> mEntryList = new ArrayList<>();
    private HashMap<String, LinuxMsgResponseCallback> mResponseMap = new HashMap<>();

    /**
     * 添加发送实体
     *
     * @param entity
     */
    public synchronized void addEntity(LinuxMsgEntity entity, int maxDelayCmdCount) {
        if (this.mEntryList.size() > maxDelayCmdCount) {
            this.mEntryList.clear();
        }
        mEntryList.add(entity);
        LogUtils.d(TAG, "addEntity entity:" + entity);
        synchronized (writeWaitLock) {
            writeWaitLock.notifyAll();
        }
    }

    public void addEntity(LinuxMsgEntity entity, LinuxMsgConfig config, LinuxMsgResponseCallback callback) {
        addEntity(entity, config.maxDelayCmdCount);
        if (callback != null) {
            callback.sendTimeStamp = System.currentTimeMillis();
            synchronized (mResponseMap) {
                mResponseMap.put(entity.getMsgCode(), callback);
                if (mResponseMap.size() > 100) {
                    Set<String> keySet = mResponseMap.keySet();
                    for (String key : keySet) {
                        LinuxMsgResponseCallback value = mResponseMap.get(key);
                        if (System.currentTimeMillis() - value.sendTimeStamp > 6 * 60 * 60 * 1000) {
                            mResponseMap.remove(key);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取当前实体
     *
     * @return currentEntity
     */
    public LinuxMsgEntity getCurrentEntity() {
        return currentEntity;
    }

    /**
     * 判断实体是否为空
     *
     * @return
     */
    public synchronized boolean isEntityEmpty() {
        return mEntryList.isEmpty();
    }

    public void checkEntityEmptyAndWait() {
        boolean empty = isEntityEmpty();
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
     * 从实体集合中取实体数据
     *
     * @return currentEntity
     */
    public synchronized LinuxMsgEntity get() {
        while (isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentEntity = mEntryList.remove(0);
        notify();
        return currentEntity;
    }

    /**
     * 实体接收完成
     */
    public synchronized void doneEntity() {
        if (isEntityEmpty()) {
            currentEntity = null;
            return;
        }
        while (!isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentEntity = null;
        notify();
    }

    public synchronized LinuxMsgResponseCallback getResponseCb(String msgCode) {
        synchronized (mResponseMap) {
            return mResponseMap.get(msgCode);
        }
    }

    public synchronized LinuxMsgResponseCallback getAndRemoveResponseCb(String msgCode) {
        synchronized (mResponseMap) {
            return mResponseMap.remove(msgCode);
        }
    }

    /**
     * 设置读写同步状态
     *
     * @param status
     */
    public void setStatus(boolean status) {
        this.isGet = status;
    }

    public void clear() {
        synchronized (mResponseMap) {
            mResponseMap.clear();
        }
    }
}
