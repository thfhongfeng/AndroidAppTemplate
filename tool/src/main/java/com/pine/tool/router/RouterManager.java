package com.pine.tool.router;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.pine.tool.util.AndroidClassUtils;
import com.pine.tool.util.AppUtils;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class RouterManager {
    private static volatile boolean mIsInit;
    private static volatile IRouterManagerFactory mRouterManagerFactory;
    private static volatile IRouterManager mRouterManagerImpl;

    public static void init(Application application, @NonNull String commandPackage, @NonNull IRouterManagerFactory factory) {
        try {
            List<String> commandClassNameList = AndroidClassUtils.getFileNameByPackageName(AppUtils.getApplicationContext(),
                    commandPackage);
            mRouterManagerFactory = factory;
            mRouterManagerImpl = factory.makeRouterManager();
            mRouterManagerImpl.init(application, commandClassNameList);
            mIsInit = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void callUiCommand(final Context context, final String bundleKey, String commandName,
                                     Bundle args, final IRouterCallback callback) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        mRouterManagerImpl.callCommand(context, bundleKey, RouterCommandType.TYPE_UI_COMMAND, commandName, args, callback);
    }

    public static void callDataCommand(final Context context, final String bundleKey, String commandName,
                                       Bundle args, final IRouterCallback callback) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        mRouterManagerImpl.callCommand(context, bundleKey, RouterCommandType.TYPE_DATA_COMMAND, commandName, args, callback);
    }

    public static void callOpCommand(final Context context, final String bundleKey, String commandName,
                                     Bundle args, final IRouterCallback callback) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        mRouterManagerImpl.callCommand(context, bundleKey, RouterCommandType.TYPE_OP_COMMAND, commandName, args, callback);
    }

    public static void callCommand(final Context context, final String bundleKey, final String commandType, String commandName,
                                   Bundle args, final IRouterCallback callback) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        mRouterManagerImpl.callCommand(context, bundleKey, commandType, commandName, args, callback);
    }

    public <R> R callUiCommandDirect(final Context context, final String bundleKey, String commandName, Bundle args) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        return mRouterManagerImpl.callCommandDirect(context, bundleKey, RouterCommandType.TYPE_UI_COMMAND, commandName, args);
    }

    public static <R> R callDataCommandDirect(final Context context, final String bundleKey, String commandName, Bundle args) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        return mRouterManagerImpl.callCommandDirect(context, bundleKey, RouterCommandType.TYPE_DATA_COMMAND, commandName, args);
    }

    public static <R> R callOpCommandDirect(final Context context, final String bundleKey,
                                            String commandName, Bundle args) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        return mRouterManagerImpl.callCommandDirect(context, bundleKey, RouterCommandType.TYPE_OP_COMMAND, commandName, args);
    }

    public static <R> R callCommandDirect(final Context context, final String bundleKey, final String commandType,
                                          String commandName, Bundle args) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        return mRouterManagerImpl.callCommandDirect(context, bundleKey, commandType, commandName, args);
    }

    public static boolean isBundleEnable(String bundleKey) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        }
        return mRouterManagerFactory.isBundleEnable(bundleKey);
    }
}
