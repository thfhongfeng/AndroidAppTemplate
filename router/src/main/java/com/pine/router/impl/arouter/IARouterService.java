package com.pine.router.impl.arouter;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.pine.router.IRouterCallback;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public interface IARouterService extends IProvider {
    void callCommand(Context context, final String commandName,
                     final Bundle args, final IRouterCallback callback);

    <R> R callCommandDirect(Context context, final String commandName,
                            final Bundle args);
}
