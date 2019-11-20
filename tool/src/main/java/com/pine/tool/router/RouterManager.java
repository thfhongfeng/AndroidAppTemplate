package com.pine.tool.router;

import android.app.Application;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pine.tool.util.AndroidClassUtils;
import com.pine.tool.util.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class RouterManager {
    private static String mCommandPackage = "com.pine.base.router.command";
    private static volatile boolean mIsInit;
    private static volatile IRouterManagerFactory mRouterManagerFactory;

    private static volatile List<String> mCommandClassNameList = new ArrayList<>();

    public static List<String> getCommandClassNameList() {
        return mCommandClassNameList;
    }

    public static void init(Application application, String commandPackage, @NonNull IRouterManagerFactory factory) {
        mCommandPackage = commandPackage;
        try {
            mCommandClassNameList = AndroidClassUtils.getFileNameByPackageName(AppUtils.getApplicationContext(),
                    mCommandPackage);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRouterManagerFactory = factory;
        mIsInit = true;
    }

    public static IRouterManager getInstance(String bundleKey) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        } else {
            return mRouterManagerFactory.makeRouterManager(bundleKey);
        }
    }

    public static boolean isBundleEnable(String bundleKey) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        } else {
            return mRouterManagerFactory.isBundleEnable(bundleKey);
        }
    }
}
