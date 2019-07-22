package com.pine.config.switcher;

import com.pine.config.ConfigKey;
import com.pine.config.bean.ConfigSwitcherEntity;
import com.pine.config.model.ConfigSwitcherModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
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

    private volatile boolean mIsLogin;

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

    public void setLogin(boolean isLogin) {
        mIsLogin = isLogin;
    }


    public void setEnable(String key, boolean enable) {
        if (mIsLogin) {
            synchronized (mUserConfigStateMap) {
                mUserConfigStateMap.put(key, enable);
            }
        } else {
            synchronized (mGuestConfigStateMap) {
                mGuestConfigStateMap.put(key, enable);
            }
        }
        LogUtils.releaseLog(TAG, "Set " + key + " fun " + (enable ? "open" : "close"));
    }

    public boolean isEnable(String key) {
        if (mIsLogin) {
            synchronized (mUserConfigStateMap) {
                return mUserConfigStateMap.containsKey(key) && mUserConfigStateMap.get(key);
            }
        } else {
            synchronized (mGuestConfigStateMap) {
                return mGuestConfigStateMap.containsKey(key) && mGuestConfigStateMap.get(key);
            }
        }
    }

    public void setConfig(List<ConfigSwitcherEntity> switcherEntityList) {
        if (mIsLogin) {
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

    public void setupConfigSwitcher(final IConfigSwitcherCallback callback) {
        mConfigSwitcherModel.requestBundleSwitcherData(new IModelAsyncResponse<ArrayList<ConfigSwitcherEntity>>() {
            @Override
            public void onResponse(ArrayList<ConfigSwitcherEntity> switcherEntities) {
                if (switcherEntities != null) {
                    setConfig(switcherEntities);
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
