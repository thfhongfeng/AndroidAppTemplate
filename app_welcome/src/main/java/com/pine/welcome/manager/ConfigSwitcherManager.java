package com.pine.welcome.manager;

import com.pine.config.switcher.ConfigBundleSwitcher;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.bean.BundleSwitcherEntity;
import com.pine.welcome.model.SwitcherModel;

import java.util.ArrayList;

public class ConfigSwitcherManager {
    private final static String TAG = LogUtils.makeLogTag(ConfigSwitcherManager.class);

    private static volatile ConfigSwitcherManager mInstance;
    private SwitcherModel mSwitcherModel = new SwitcherModel();

    private ConfigSwitcherManager() {
    }

    public static ConfigSwitcherManager getInstance() {
        if (mInstance == null) {
            synchronized (ApkVersionManager.class) {
                if (mInstance == null) {
                    mInstance = new ConfigSwitcherManager();
                }
            }
        }
        return mInstance;
    }

    public void setupConfigSwitcher(final IConfigSwitcherCallback callback) {
        mSwitcherModel.requestBundleSwitcherData(new IModelAsyncResponse<ArrayList<BundleSwitcherEntity>>() {
            @Override
            public void onResponse(ArrayList<BundleSwitcherEntity> bundleSwitcherEntities) {
                if (bundleSwitcherEntities != null) {
                    for (int i = 0; i < bundleSwitcherEntities.size(); i++) {
                        ConfigBundleSwitcher.setBundleState(bundleSwitcherEntities.get(i).getConfigKey(),
                                bundleSwitcherEntities.get(i).getOpen() == 1);
                    }
                }
                if (callback != null) {
                    callback.onSetupComplete();
                }
                return;
            }

            @Override
            public boolean onFail(Exception e) {
                if (callback != null) {
                    callback.onSetupFail();
                }
                return true;
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

        void onSetupFail();
    }
}
