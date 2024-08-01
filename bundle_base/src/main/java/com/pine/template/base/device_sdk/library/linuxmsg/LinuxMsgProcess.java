package com.pine.template.base.device_sdk.library.linuxmsg;

import android.os.Message;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * 数据处理
 */
public class LinuxMsgProcess {
    /**
     * 当前处理中的实体
     */
    private LinuxMsgEntity currentEntity;

    /**
     * 数据回调
     */
    private LinuxMsgResultCallback onResultCallback;

    /**
     * 发送、接收entity超时控制
     */
    private EntityHandler entityHandler;

    private HashMap<String, Runnable> timeoutRunnableMap = new HashMap<>();

    /**
     * 数据同步控制
     */
    private LinuxMsgConcurrent concurrent = new LinuxMsgConcurrent();

    private static final int RESPONSE_CODE_ERR = 1;
    private static final int RESPONSE_CODE_TIMEOUT = 2;

    private static final int ON_SEND_WHAT = 1;
    /**
     * 发送响应超时
     */
    private static final int RESPONSE_TIMEOUT_WHAT = 2;
    /**
     * 发送响应实体
     */
    private static final int SEND_RESPONSE_WHAT = 3;
    /**
     * 发送实体失败
     */
    private static final int SEND_FAIL_WHAT = 4;
    /**
     * 接收实体
     */
    private static final int RECEIVE_WHAT = 5;

    private String senderTag;
    private LinuxMsgConfig config;

    public LinuxMsgProcess(String senderTag, LinuxMsgConfig config) {
        this.senderTag = senderTag;
        this.config = config;
        entityHandler = new EntityHandler(this);
    }

    /**
     * entity写入数据
     */
    public void writeData() {
        concurrent.checkEntityEmptyAndWait();
        if (concurrent.getCurrentEntity() == null && !concurrent.isEntityEmpty()) {
            currentEntity = concurrent.get();
            if (currentEntity == null) {
                return;
            }
            // 设置了超时时间
            boolean hasTimeout = config.timeout > 0;
            final LinuxMsgEntity entity = currentEntity;
            if (hasTimeout) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (timeoutRunnableMap) {
                            timeoutRunnableMap.remove(entity.getMsgCode());
                        }
                        if (concurrent != null) {
                            LinuxMsgResponseCallback callback =
                                    concurrent.getAndRemoveResponseCb(entity.getMsgCode());
                            if (callback != null) {
                                callback.onFail(RESPONSE_CODE_TIMEOUT, entity);
                            }
                        }
                    }
                };
                synchronized (timeoutRunnableMap) {
                    timeoutRunnableMap.put(entity.getMsgCode(), runnable);
                }
                entityHandler.postDelayed(runnable, config.timeout);
            }
            sendMessage(currentEntity, ON_SEND_WHAT);
            boolean success = LinuxMsgJNI.sendMsg(currentEntity.getMsgType(), currentEntity.toJson());
            concurrent.setStatus(true);
            if (!success) {
                Runnable runnable;
                synchronized (timeoutRunnableMap) {
                    runnable = timeoutRunnableMap.remove(entity.getMsgCode());
                }
                if (runnable != null) {
                    entityHandler.removeCallbacks(runnable);
                }
                sendMessage(currentEntity, SEND_FAIL_WHAT);
            }
            entityDone(currentEntity);
        }
    }

    /**
     * 根据配置对entity数据进行处理
     */
    public void processingRecData(String data) {
        resultCallback(data);
    }

    /**
     * 判断数据是否读取完成，通过回调输出读取数据
     */
    private void resultCallback(String data) {
        LinuxMsgEntity entity = LinuxMsgEntity.toEntity(data);
        if (entity == null || TextUtils.equals(entity.getSenderTag(), senderTag)) {
            reInit(null);
            return;
        }
        if (entity.getMsgClass() == 2) {
            Runnable runnable;
            synchronized (timeoutRunnableMap) {
                runnable = timeoutRunnableMap.remove(entity.getMsgCode());
            }
            if (runnable != null) {
                entityHandler.removeCallbacks(runnable);
            }
            sendMessage(entity, SEND_RESPONSE_WHAT);
        } else {
            if (onResultCallback == null) {
                reInit(null);
                return;
            }
            sendMessage(entity, RECEIVE_WHAT);
        }
        reInit(entity);
    }

    /**
     * 重置数据
     */
    private void reInit(LinuxMsgEntity entity) {
        if (currentEntity == null) {
            return;
        }
        entityDone(entity);
    }

    /**
     * 数据接收完成，恢复写数据线程
     */
    private void entityDone(LinuxMsgEntity entity) {
        if (currentEntity != null) {
            concurrent.doneEntity();
            concurrent.setStatus(false);
        }
    }

    /**
     * 添加entity发送实体
     *
     * @param entity
     */
    public void addEntity(LinuxMsgEntity entity) {
        concurrent.addEntity(entity, config.maxDelayCmdCount);
    }

    public void addEntity(LinuxMsgEntity entity, LinuxMsgResponseCallback callback) {
        concurrent.addEntity(entity, config, callback);
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setResultCallback(LinuxMsgResultCallback onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    /**
     * 发送entity数据到主线程
     *
     * @param entity entity数据
     * @param what   数据标识
     */
    private void sendMessage(LinuxMsgEntity entity, int what) {
        Message message = new Message();
        message.what = what;
        message.obj = entity;
        entityHandler.sendMessage(message);
    }

    /**
     * 数据通过Handler发送到主线程
     */
    private static class EntityHandler extends android.os.Handler {
        private LinuxMsgProcess processingRecData;

        public EntityHandler(LinuxMsgProcess processingRecData) {
            this.processingRecData = processingRecData;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            processingRecData.receiveData(msg);
        }
    }

    /**
     * 处理数据回调
     *
     * @param msg
     */
    private void receiveData(Message msg) {
        LinuxMsgEntity entity = (LinuxMsgEntity) msg.obj;
        LinuxMsgResponseCallback callback = null;
        switch (msg.what) {
            case ON_SEND_WHAT:
                callback = concurrent.getResponseCb(entity.getMsgCode());
                if (callback != null) {
                    callback.onSend(entity);
                }
                break;
            case RESPONSE_TIMEOUT_WHAT:
                entityDone(currentEntity);
                callback = concurrent.getAndRemoveResponseCb(entity.getMsgCode());
                if (callback != null) {
                    callback.onFail(RESPONSE_CODE_TIMEOUT, entity);
                }
                break;
            case SEND_FAIL_WHAT:
                entityDone(currentEntity);
                callback = concurrent.getAndRemoveResponseCb(entity.getMsgCode());
                if (callback != null) {
                    callback.onFail(RESPONSE_CODE_ERR, entity);
                }
                break;
            case SEND_RESPONSE_WHAT:
                callback = concurrent.getAndRemoveResponseCb(entity.getMsgCode());
                if (callback != null) {
                    callback.onResponse(entity);
                }
                break;
            case RECEIVE_WHAT:
                onResultCallback.onReceiveData(entity);
                break;
            default:
                break;
        }
    }

    public void close() {
        if (concurrent != null) {
            concurrent.clear();
        }
    }
}
