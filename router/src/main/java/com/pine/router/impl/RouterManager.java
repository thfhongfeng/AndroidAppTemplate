package com.pine.router.impl;

import android.content.pm.PackageManager;

import com.pine.config.BuildConfig;
import com.pine.router.impl.arouter.manager.ARouterManager;
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

    private static volatile List<String> mCommandClassNameList = new ArrayList<>();

    public static List<String> getCommandClassNameList() {
        return mCommandClassNameList;
    }

    public static void init(String commandPackage) {
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
        mIsInit = true;
    }

    public static IRouterManager getInstance(String bundleKey) {
        if (!mIsInit) {
            throw new IllegalArgumentException("RouterManager should be init first");
        } else {
            switch (BuildConfig.APP_THIRD_ROUTER_PROVIDER) {
                case "arouter":
                    return ARouterManager.getInstance(bundleKey);
                default:
                    return ARouterManager.getInstance(bundleKey);
            }
        }
    }
}
