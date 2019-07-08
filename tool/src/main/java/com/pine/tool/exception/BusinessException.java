package com.pine.tool.exception;

/**
 * Created by tanghongfeng on 2018/10/12
 */

// 业务异常（即请求过程是成功，但业务处理结果异常）
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
}
