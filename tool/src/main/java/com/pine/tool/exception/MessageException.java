package com.pine.tool.exception;

/**
 * Created by tanghongfeng on 2018/10/12
 */

/**
 * 用于错误信息的封装
 */
public class MessageException extends Exception {
    public MessageException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
