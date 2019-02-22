package com.pine.router.impl.arouter.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.pine.config.ConfigBundleKey;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class ARouterBusinessMvpManager extends ARouterManager {
    public final static String AROUTER_UI_PATH = "/mvp/uiService";
    public final static String AROUTER_DATA_PATH = "/mvp/dataService";
    public final static String AROUTER_OP_PATH = "/mvp/uiService";

    private static volatile ARouterBusinessMvpManager mInstance;

    private ARouterBusinessMvpManager() {
    }

    public static ARouterBusinessMvpManager getInstance() {
        if (mInstance == null) {
            synchronized (ARouterBusinessMvpManager.class) {
                if (mInstance == null) {
                    mInstance = new ARouterBusinessMvpManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getBundleKey() {
        return ConfigBundleKey.BUSINESS_MVP_BUNDLE_KEY;
    }

    @Override
    public String getUiCommandPath() {
        return AROUTER_UI_PATH;
    }

    @Override
    public String getDataCommandPath() {
        return AROUTER_DATA_PATH;
    }

    @Override
    public String getOpCommandPath() {
        return AROUTER_OP_PATH;
    }

    @Override
    protected void onCommandFail(String commandType, Context context, int failCode, String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
