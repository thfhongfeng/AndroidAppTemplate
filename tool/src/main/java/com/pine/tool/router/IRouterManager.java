package com.pine.tool.router;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public interface IRouterManager {
    int FAIL_CODE_INVALID = 1001; // 无效的请求，比如请求的模块没有权限等。
    int FAIL_CODE_LOST = 1002; // 请求丢失，比如对应模块并没有开放对应的服务接口。
    int FAIL_CODE_INTERRUPT = 1003; // 请求被打断。
    int FAIL_CODE_ERROR = 1004; // 请求返回出错，被请求的服务出错。

    /**
     * 初始化
     *
     * @param application
     * @param commandClassNameList
     */
    void init(Application application, List<String> commandClassNameList);

    void callCommand(Context context, final String bundleKey, final String commandType,
                     final String commandName, final Bundle args, final IRouterCallback callback);

    <R> R callCommandDirect(Context context, final String bundleKey, final String commandType,
                            final String commandName, final Bundle args);
}
