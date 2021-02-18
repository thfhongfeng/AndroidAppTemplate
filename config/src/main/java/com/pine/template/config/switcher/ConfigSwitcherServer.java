package com.pine.template.config.switcher;

import com.pine.template.config.ConfigApplication;
import com.pine.template.config.ConfigKey;
import com.pine.template.config.bean.ConfigSwitcherEntity;
import com.pine.template.config.model.ConfigSwitcherModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSwitcherServer {
    private static final String TAG = LogUtils.makeLogTag(ConfigSwitcherServer.class);

    private static volatile ConfigSwitcherServer mInstance;
    private ConfigSwitcherModel mConfigSwitcherModel = new ConfigSwitcherModel();

    private Map<String, Boolean> mGuestConfigStateMap = new HashMap();
    private Map<String, Boolean> mUserConfigStateMap = new HashMap();

    private ConfigSwitcherServer() {
        synchronized (mGuestConfigStateMap) {
            mGuestConfigStateMap.put(ConfigKey.BUNDLE_WELCOME_KEY, true);
            mGuestConfigStateMap.put(ConfigKey.BUNDLE_LOGIN_KEY, true);
            mGuestConfigStateMap.put(ConfigKey.BUNDLE_MAIN_KEY, true);
            mGuestConfigStateMap.put(ConfigKey.BUNDLE_USER_KEY, true);
            mGuestConfigStateMap.put(ConfigKey.BUNDLE_DB_SEVER_KEY, true);
        }

        synchronized (mUserConfigStateMap) {
            mUserConfigStateMap.putAll(mGuestConfigStateMap);
        }
    }

    public static ConfigSwitcherServer getInstance() {
        if (mInstance == null) {
            synchronized (ConfigSwitcherServer.class) {
                if (mInstance == null) {
                    mInstance = new ConfigSwitcherServer();
                }
            }
        }
        return mInstance;
    }

    public void setEnable(String key, boolean enable) {
        if (ConfigApplication.isLogin()) {
            synchronized (mUserConfigStateMap) {
                mUserConfigStateMap.put(key, enable);
            }
        } else {
            synchronized (mGuestConfigStateMap) {
                mGuestConfigStateMap.put(key, enable);
            }
        }
        LogUtils.releaseLog(TAG, "Set " + key + " func " + (enable ? "open" : "close"));
    }

    public boolean isEnable(String key) {
        if (ConfigApplication.isLogin()) {
            synchronized (mUserConfigStateMap) {
                return mUserConfigStateMap.containsKey(key) && mUserConfigStateMap.get(key);
            }
        } else {
            synchronized (mGuestConfigStateMap) {
                return mGuestConfigStateMap.containsKey(key) && mGuestConfigStateMap.get(key);
            }
        }
    }

    public void setConfig(boolean isLogin, List<ConfigSwitcherEntity> switcherEntityList) {
        if (isLogin) {
            synchronized (mUserConfigStateMap) {
                mUserConfigStateMap.clear();
                if (switcherEntityList != null && switcherEntityList.size() > 0) {
                    for (int i = 0; i < switcherEntityList.size(); i++) {
                        mUserConfigStateMap.put(switcherEntityList.get(i).getConfigKey(), switcherEntityList.get(i).getState() == 1);
                    }
                }
            }
        } else {
            synchronized (mGuestConfigStateMap) {
                mGuestConfigStateMap.clear();
                if (switcherEntityList != null && switcherEntityList.size() > 0) {
                    for (int i = 0; i < switcherEntityList.size(); i++) {
                        mGuestConfigStateMap.put(switcherEntityList.get(i).getConfigKey(), switcherEntityList.get(i).getState() == 1);
                    }
                }
            }
        }
    }

    public void setupConfigSwitcher(final boolean isLogin, final IConfigSwitcherCallback callback) {
        final String versionName = AppUtils.getVersionName();
        final int versionCode = AppUtils.getVersionCode();
        HashMap<String, String> params = new HashMap<>();
        params.put("versionName", versionName);
        params.put("versionCode", versionCode + "");
        mConfigSwitcherModel.requestBundleSwitcherData(params, new IModelAsyncResponse<ArrayList<ConfigSwitcherEntity>>() {
            @Override
            public void onResponse(ArrayList<ConfigSwitcherEntity> switcherEntities) {
                if (switcherEntities != null) {
                    setConfig(isLogin, switcherEntities);
                }
                if (callback != null) {
                    callback.onSetupComplete();
                }
            }

            @Override
            public boolean onFail(Exception e) {
                if (callback != null) {
                    return callback.onSetupFail();
                }
                return false;
            }

            @Override
            public void onCancel() {
                if (callback != null) {
                    callback.onSetupFail();
                }
            }
        });
    }

    public interface IConfigSwitcherCallback {
        void onSetupComplete();

        boolean onSetupFail();
    }
}
