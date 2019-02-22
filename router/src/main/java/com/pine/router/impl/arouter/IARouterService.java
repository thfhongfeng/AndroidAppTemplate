package com.pine.router.impl.arouter;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.pine.router.IRouterCallback;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public interface IARouterService extends IProvider {
    void callCommand(Activity activity, final String commandName,
                     final Bundle args, final IRouterCallback callback);
}
