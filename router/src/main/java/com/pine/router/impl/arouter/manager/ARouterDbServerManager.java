package com.pine.router.impl.arouter.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.pine.config.ConfigBundleKey;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class ARouterDbServerManager extends ARouterManager {
    public final static String AROUTER_UI_PATH = "/db/dataService";
    public final static String AROUTER_DATA_PATH = "/db/dataService";
    public final static String AROUTER_OP_PATH = "/db/dataService";

    private static volatile ARouterDbServerManager mInstance;

    private ARouterDbServerManager() {
    }

    public static ARouterDbServerManager getInstance() {
        if (mInstance == null) {
            synchronized (ARouterDbServerManager.class) {
                if (mInstance == null) {
                    mInstance = new ARouterDbServerManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getBundleKey() {
        return ConfigBundleKey.USER_BUNDLE_KEY;
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
