package com.pine.router.impl.arouter.manager;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pine.config.switcher.ConfigBundleSwitcher;
import com.pine.router.IRouterCallback;
import com.pine.router.R;
import com.pine.router.impl.IRouterManager;
import com.pine.router.impl.arouter.IARouterService;
import com.pine.tool.util.LogUtils;

import static com.pine.router.RouterConstants.TYPE_DATA_COMMAND;
import static com.pine.router.RouterConstants.TYPE_OP_COMMAND;
import static com.pine.router.RouterConstants.TYPE_UI_COMMAND;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public abstract class ARouterManager implements IRouterManager {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    @Override
    public void callUiCommand(final Context context, String commandName,
                              Bundle args, final IRouterCallback callback) {
        if (!checkBundleValidity(TYPE_UI_COMMAND, context, callback)) {
            return;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getUiCommandPath())
                .navigation(context, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        LogUtils.d(TAG, "callUiCommand path:'" + postcard.getPath() + "'onFound");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        LogUtils.d(TAG, "callUiCommand path:'" + postcard.getPath() + "'onLost");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_LOST, "onLost")) {
                            onCommandFail(TYPE_UI_COMMAND, context, IRouterManager.FAIL_CODE_LOST, "onLost");
                        }
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        LogUtils.d(TAG, "callUiCommand path:'" + postcard.getPath() + "'onArrival");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        LogUtils.d(TAG, "callUiCommand path:'" + postcard.getPath() + "'onInterrupt");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt")) {
                            onCommandFail(TYPE_UI_COMMAND, context, IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt");
                        }
                    }
                }));
        if (routerService != null) {
            routerService.callCommand(context, commandName, args, callback);
        }
    }

    @Override
    public void callDataCommand(final Context context, String commandName, Bundle args, final IRouterCallback callback) {
        if (!checkBundleValidity(TYPE_DATA_COMMAND, context, callback)) {
            return;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getDataCommandPath())
                .navigation(context, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        LogUtils.d(TAG, "callDataCommand path:'" + postcard.getPath() + "'onFound");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        LogUtils.d(TAG, "callDataCommand path:'" + postcard.getPath() + "'onLost");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_LOST, "onLost")) {
                            onCommandFail(TYPE_DATA_COMMAND, context, IRouterManager.FAIL_CODE_LOST, "onLost");
                        }
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        LogUtils.d(TAG, "callDataCommand path:'" + postcard.getPath() + "'onArrival");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        LogUtils.d(TAG, "callDataCommand path:'" + postcard.getPath() + "'onInterrupt");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt")) {
                            onCommandFail(TYPE_DATA_COMMAND, context, IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt");
                        }
                    }
                }));
        if (routerService != null) {
            routerService.callCommand(context, commandName, args, callback);
        }
    }

    @Override
    public void callOpCommand(final Context context, String commandName,
                              Bundle args, final IRouterCallback callback) {
        if (!checkBundleValidity(TYPE_OP_COMMAND, context, callback)) {
            return;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getOpCommandPath())
                .navigation(context, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        LogUtils.d(TAG, "callOpCommand path:'" + postcard.getPath() + "'onFound");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        LogUtils.d(TAG, "callOpCommand path:'" + postcard.getPath() + "'onLost");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_LOST, "onLost")) {
                            onCommandFail(TYPE_OP_COMMAND, context, IRouterManager.FAIL_CODE_LOST, "onLost");
                        }
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        LogUtils.d(TAG, "callOpCommand path:'" + postcard.getPath() + "'onArrival");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        LogUtils.d(TAG, "callOpCommand path:'" + postcard.getPath() + "'onInterrupt");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt")) {
                            onCommandFail(TYPE_OP_COMMAND, context, IRouterManager.FAIL_CODE_INTERRUPT, "onInterrupt");
                        }
                    }
                }));
        if (routerService != null) {
            routerService.callCommand(context, commandName, args, callback);
        }
    }

    @Override
    public <R> R callUiCommandDirect(final Context context, String commandName, Bundle args) {
        if (!checkBundleValidity(TYPE_UI_COMMAND, context, null)) {
            return null;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getUiCommandPath())
                .navigation(context, null));
        if (routerService != null) {
            return routerService.callCommandDirect(context, commandName, args);
        }
        return null;
    }

    @Override
    public <R> R callDataCommandDirect(final Context context, String commandName, Bundle args) {
        if (!checkBundleValidity(TYPE_DATA_COMMAND, context, null)) {
            return null;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getDataCommandPath())
                .navigation(context, null));
        if (routerService != null) {
            return routerService.callCommandDirect(context, commandName, args);
        }
        return null;
    }

    @Override
    public <R> R callOpCommandDirect(final Context context, String commandName, Bundle args) {
        if (!checkBundleValidity(TYPE_OP_COMMAND, context, null)) {
            return null;
        }
        IARouterService routerService = ((IARouterService) ARouter.getInstance().build(getOpCommandPath())
                .navigation(context, null));
        if (routerService != null) {
            return routerService.callCommandDirect(context, commandName, args);
        }
        return null;
    }

    private boolean checkBundleValidity(final String commandType, final Context context,
                                        final IRouterCallback callback) {
        if (!ConfigBundleSwitcher.isBundleOpen(getBundleKey())) {
            LogUtils.releaseLog(TAG, getBundleKey() + " is not opened");
            if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INVALID,
                    context.getString(R.string.router_bundle_not_open))) {
                onCommandFail(commandType, context, IRouterManager.FAIL_CODE_INVALID,
                        context.getString(R.string.router_bundle_not_open));
            }
            return false;
        }
        return true;
    }

    public abstract String getBundleKey();

    public abstract String getUiCommandPath();

    public abstract String getDataCommandPath();

    public abstract String getOpCommandPath();

    protected abstract void onCommandFail(String commandType, Context context, int failCode, String message);
}
