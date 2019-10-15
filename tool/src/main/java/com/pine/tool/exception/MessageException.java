package com.pine.tool.exception;

/**
 * Created by tanghongfeng on 2018/10/12
 */

// 提示信息异常
public class MessageException extends Exception {
    public MessageException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
