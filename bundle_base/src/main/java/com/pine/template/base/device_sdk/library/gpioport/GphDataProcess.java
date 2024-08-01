package com.pine.template.base.device_sdk.library.gpioport;

import android.os.Message;

import com.pine.template.base.device_sdk.constants.GpioConstants;

/**
 * gpio数据处理
 */
public class GphDataProcess {
    /**
     * 当前处理中的命令
     */
    private GphCmdEntity currentCommand;

    /**
     * 数据回调
     */
    private GphResultCallback onResultCallback;

    /**
     * 发送、接收gpio超时控制
     */
    private GphHandler gphHandler;

    /**
     * 数据同步控制
     */
    private GphConcurrentCom concurrentCom = new GphConcurrentCom();

    /**
     * 超时handle what值
     */
    private static final int TIMEOUT_WHAT = 1;
    /**
     * 发送命令
     */
    private static final int SENDCMD_WHAT = 2;
    /**
     * 接收命令
     */
    private static final int RECEIVECMD_WHAT = 3;
    /**
     * 完成
     */
    private static final int COMPLETECMD_WHAT = 4;

    private GpioPortConfig config;

    public GphDataProcess(GpioPortConfig config) {
        this.config = config;
        gphHandler = new GphHandler(this);
    }

    /**
     * gpio写入数据
     */
    public void writeData() {
        concurrentCom.checkEntityEmptyAndWait();
        if (concurrentCom.getCurrentCmdEntity() == null && !concurrentCom.isCmdEmpty()) {
            currentCommand = concurrentCom.get();
            if (currentCommand == null) {
                return;
            }
            if (onResultCallback != null) {
                sendMessage(currentCommand, SENDCMD_WHAT);
            }
            // 设置了超时时间
            boolean hasTimeout = currentCommand.timeOut > 0;
            if (hasTimeout) {
                gphHandler.sendEmptyMessageDelayed(TIMEOUT_WHAT, currentCommand.timeOut);
            }
            switch (currentCommand.commandsType) {
                case 1:
                    currentCommand.success = GpioPortJNI.writePort(config.port, currentCommand.commands);
                    break;
                case 2:
                    currentCommand.success = GpioPortJNI.writeDirect(config.port, currentCommand.commands);
                    break;
                default:
                    return;
            }
            concurrentCom.setStatus(true);
            commandDone(currentCommand);
        }
    }

    /**
     * 根据配置对gpio数据进行处理
     */
    public void processingRecData(int status) {
        resultCallback(status);
    }

    /**
     * 判断数据是否读取完成，通过回调输出读取数据
     */
    private void resultCallback(int status) {
        if (onResultCallback == null) {
            reInit(null);
            return;
        }
        GphCmdEntity cmdEntity = new GphCmdEntity(status, GpioConstants.COMMAND_TYPE_READ_STATUS);
        if (status < 0) {
            cmdEntity.success = false;
        }
        sendMessage(cmdEntity, RECEIVECMD_WHAT);
        reInit(cmdEntity);
    }

    /**
     * 重置数据
     */
    private void reInit(GphCmdEntity command) {
        if (currentCommand == null) {
            return;
        }
        int receiveCount = currentCommand.receiveCount;
        if (receiveCount > 1) {
            currentCommand.receiveCount = receiveCount - 1;
            return;
        }
        commandDone(command);
    }

    /**
     * 数据接收完成，恢复写数据线程
     */
    private void commandDone(GphCmdEntity command) {
        gphHandler.removeMessages(TIMEOUT_WHAT);
        if (currentCommand != null) {
            concurrentCom.doneCom();
            concurrentCom.setStatus(false);
        }
        if (onResultCallback != null && command != null) {
            sendMessage(command, COMPLETECMD_WHAT);
        }
    }

    /**
     * 添加gpio发送命令
     *
     * @param command
     */
    public void addCommands(GphCmdEntity command) {
        concurrentCom.addCommands(command, config.maxDelayCmdCount);
    }

    /**
     * 写入命令
     */
    private void reWriteCmdOrExit() {
        if (currentCommand.reWriteCom) {
            // 重复次数
            int times = currentCommand.reWriteTimes;
            if (times > 0) {
                if (onResultCallback != null) {
                    onResultCallback.onSendData(currentCommand);
                }
                gphHandler.sendEmptyMessageDelayed(TIMEOUT_WHAT, currentCommand.timeOut);
                switch (currentCommand.commandsType) {
                    case 1:
                        currentCommand.success = GpioPortJNI.writePort(config.port, currentCommand.commands);
                        break;
                    case 2:
                        currentCommand.success = GpioPortJNI.writeDirect(config.port, currentCommand.commands);
                        break;
                    default:
                        return;
                }
                concurrentCom.setStatus(true);
                currentCommand.reWriteTimes = times - 1;
                if (currentCommand.success) {
                    commandDone(currentCommand);
                }
            } else {
                commandDone(currentCommand);
            }
        } else {
            commandDone(currentCommand);
        }
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    public void setGphResultCallback(GphResultCallback onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    /**
     * 发送gpio数据到主线程
     *
     * @param GphCmdEntity gpio数据
     * @param what         数据标识
     */
    private void sendMessage(GphCmdEntity GphCmdEntity, int what) {
        Message message = new Message();
        message.what = what;
        message.obj = GphCmdEntity;
        gphHandler.sendMessage(message);
    }

    /**
     * 数据通过Handler发送到主线程
     */
    private static class GphHandler extends android.os.Handler {
        private GphDataProcess processingRecData;

        public GphHandler(GphDataProcess processingRecData) {
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
        switch (msg.what) {
            case TIMEOUT_WHAT:
                reWriteCmdOrExit();
                break;
            case SENDCMD_WHAT:
                onResultCallback.onSendData((GphCmdEntity) msg.obj);
                break;
            case RECEIVECMD_WHAT:
                onResultCallback.onReceiveData((GphCmdEntity) msg.obj);
                break;
            case COMPLETECMD_WHAT:
                onResultCallback.onComplete((GphCmdEntity) msg.obj);
                break;
            default:
                break;
        }
    }
}
