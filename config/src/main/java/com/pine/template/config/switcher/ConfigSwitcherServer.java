package com.pine.template.config.switcher;

import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.config.ConfigApplication;
import com.pine.template.config.SPKeyConstants;
import com.pine.template.config.bean.ConfigSwitcherEntity;
import com.pine.template.config.bean.ConfigSwitcherInfo;
import com.pine.template.config.model.ConfigSwitcherModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigSwitcherServer {
    private static final String TAG = LogUtils.makeLogTag(ConfigSwitcherServer.class);

    public final static boolean ENABLE_REMOTE_LOADING_CONFIG_SWITCH = false;

    private static volatile ConfigSwitcherServer mInstance;
    private ConfigSwitcherModel mConfigSwitcherModel = new ConfigSwitcherModel();

    private Map<String, ConfigSwitcherEntity> mInitConfigStateMap = new HashMap();
    private Map<String, ConfigSwitcherEntity> mGuestConfigStateMap = new HashMap();
    private Map<String, ConfigSwitcherEntity> mUserConfigStateMap = new HashMap();

    public static void init() {
        if (mInstance == null) {
            synchronized (ConfigSwitcherServer.class) {
                if (mInstance == null) {
                    mInstance = new ConfigSwitcherServer();
                }
            }
        }
    }

    private ConfigSwitcherServer() {
        initLocalConfig();
    }

    private synchronized void initLocalConfig() {
        AssetManager assetManager = AppUtils.getApplication().getResources().getAssets();
        try {
            Properties properties = new Properties();
            // 打开INI文件的InputStream
            InputStream inputStream = assetManager.open("config.ini");
            // 使用指定的字符编码创建InputStreamReader
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            properties.load(reader);
            Set keySet = properties.keySet();
            for (Object object : keySet) {
                String propKey = object.toString();
                String propValue = properties.getProperty(propKey);
                ConfigSwitcherEntity entity = new ConfigSwitcherEntity(propKey,
                        propValue, ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_INIT);
                mInitConfigStateMap.put(propKey, entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Map<String, ConfigSwitcherEntity> userConfigMap =
                SharePreferenceUtils.readMapFromConfig(SPKeyConstants.USER_CONFIG_KEY,
                        String.class, ConfigSwitcherEntity.class);
        if (userConfigMap == null || userConfigMap.size() < 1) {
            checkInitConfigAndCopy(mInitConfigStateMap, mUserConfigStateMap);
        } else {
            checkInitConfigAndCopy(userConfigMap, mUserConfigStateMap);
        }

        Map<String, ConfigSwitcherEntity> guestConfigMap =
                SharePreferenceUtils.readMapFromConfig(SPKeyConstants.GUEST_CONFIG_KEY, String.class,
                        ConfigSwitcherEntity.class);
        if (guestConfigMap == null || guestConfigMap.size() < 1) {
            checkInitConfigAndCopy(mInitConfigStateMap, mGuestConfigStateMap);
        } else {
            checkInitConfigAndCopy(guestConfigMap, mGuestConfigStateMap);
        }
    }

    private synchronized void checkInitConfigAndCopy(@NonNull Map<String, ConfigSwitcherEntity> srcMap,
                                                     @NonNull Map<String, ConfigSwitcherEntity> targetMap) {
        if (srcMap != mInitConfigStateMap) {
            Set<String> keySet = mInitConfigStateMap.keySet();
            for (String key : keySet) {
                if (!srcMap.containsKey(key) || srcMap.get(key) == null
                        || srcMap.get(key).getConfigType() == ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_INIT) {
                    srcMap.put(key, mInitConfigStateMap.get(key).clone());
                }
            }
        }
        targetMap.clear();
        Set<String> keySet = srcMap.keySet();
        for (String key : keySet) {
            targetMap.put(key, srcMap.get(key).clone());
        }
    }

    public synchronized boolean isEnableImpl(@NonNull String key, boolean defaultValue) {
        if (ConfigApplication.isLogin()) {
            if (mUserConfigStateMap.containsKey(key)) {
                return mUserConfigStateMap.get(key).isEnable();
            }
        } else {
            if (mGuestConfigStateMap.containsKey(key)) {
                return mGuestConfigStateMap.get(key).isEnable();
            }
        }
        return defaultValue;
    }

    public synchronized boolean isInitEnableImpl(@NonNull String key, boolean defaultValue) {
        if (mInitConfigStateMap.containsKey(key)) {
            return mInitConfigStateMap.get(key).isEnable();
        }
        return defaultValue;
    }

    public synchronized String getConfigImpl(@NonNull String key, String defaultValue) {
        if (ConfigApplication.isLogin()) {
            if (mUserConfigStateMap.containsKey(key)) {
                return mUserConfigStateMap.get(key).getValue();
            }
        } else {
            if (mGuestConfigStateMap.containsKey(key)) {
                return mGuestConfigStateMap.get(key).getValue();
            }
        }
        return defaultValue;
    }

    public synchronized String getInitConfigImpl(@NonNull String key, String defaultValue) {
        if (mInitConfigStateMap.containsKey(key)) {
            return mInitConfigStateMap.get(key).getValue();
        }
        return defaultValue;
    }

    public synchronized void saveConfigMapImpl(@NonNull HashMap<String, String> configMap) {
        if (configMap == null || configMap.size() < 1) {
            return;
        }
        if (ConfigApplication.isLogin()) {
            Set<String> keySet = configMap.keySet();
            for (String key : keySet) {
                mUserConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                        ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
            }
            SharePreferenceUtils.saveToConfig(SPKeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        } else {
            Set<String> keySet = configMap.keySet();
            for (String key : keySet) {
                mGuestConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                        ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
            }
            SharePreferenceUtils.saveToConfig(SPKeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
        }
    }

    public synchronized void saveConfigImpl(@NonNull String key, String value) {
        saveConfigImpl(key, value, ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER);
    }

    public synchronized void saveConfigImpl(@NonNull String key, String value, int configType) {
        if (TextUtils.isEmpty(value)) {
            ConfigSwitcherEntity entity = mInitConfigStateMap.get(key);
            if (entity != null) {
                value = entity.getValue();
            } else {
                value = "";
            }
        }
        if (ConfigApplication.isLogin()) {
            mUserConfigStateMap.put(key,
                    new ConfigSwitcherEntity(key, value, configType));
            SharePreferenceUtils.saveToConfig(SPKeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        } else {
            mGuestConfigStateMap.put(key,
                    new ConfigSwitcherEntity(key, value, configType));
            SharePreferenceUtils.saveToConfig(SPKeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
        }
    }

    private synchronized void updateRemoteConfig(boolean isLogin, @NonNull ConfigSwitcherInfo switcherInfo) {
        String version = SharePreferenceUtils.readStringFromConfig(SPKeyConstants.CONFIG_REMOTE_VERSION_CODE, "");
        if (TextUtils.isEmpty(switcherInfo.getVersion()) || TextUtils.equals(version, switcherInfo.getVersion())) {
            return;
        }
        List<ConfigSwitcherEntity> list = switcherInfo.getConfigList();
        if (list == null) {
            return;
        }
        if (isLogin) {
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    ConfigSwitcherEntity entity = list.get(i);
                    if (entity == null) {
                        continue;
                    }
                    entity.setConfigType(ConfigSwitcherEntity.CONFIG_TYPE_REMOTE);
                    if (shouldOverrideConfig(entity, mUserConfigStateMap.get(entity.getKey()))) {
                        mUserConfigStateMap.put(entity.getKey(), entity);
                    }
                }
                SharePreferenceUtils.saveToConfig(SPKeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
                SharePreferenceUtils.saveToConfig(SPKeyConstants.CONFIG_REMOTE_VERSION_CODE, version);
            }
        } else {
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    ConfigSwitcherEntity entity = list.get(i);
                    if (entity == null) {
                        continue;
                    }
                    entity.setConfigType(ConfigSwitcherEntity.CONFIG_TYPE_REMOTE);
                    if (shouldOverrideConfig(entity, mGuestConfigStateMap.get(entity.getKey()))) {
                        mGuestConfigStateMap.put(entity.getKey(), entity);
                    }
                }
                SharePreferenceUtils.saveToConfig(SPKeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
                SharePreferenceUtils.saveToConfig(SPKeyConstants.CONFIG_REMOTE_VERSION_CODE, version);
            }
        }
    }

    private boolean shouldOverrideConfig(ConfigSwitcherEntity srcEntity, ConfigSwitcherEntity targetEntity) {
        if (srcEntity == null) {
            return false;
        }
        if (srcEntity.isForce() || targetEntity.getConfigType() != ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER) {
            return true;
        }
        return false;
    }

    public void setupConfigSwitcherImpl(final boolean isLogin, @NonNull HashMap<String, String> params,
                                        final IConfigSwitcherCallback callback) {
        if (!ENABLE_REMOTE_LOADING_CONFIG_SWITCH) {
            callback.onSetupComplete();
            return;
        }
        String version = SharePreferenceUtils.readStringFromConfig(SPKeyConstants.CONFIG_REMOTE_VERSION_CODE, "");
        if (!TextUtils.isEmpty(version)) {
            params.put("version", version);
        }
        params.put("isLogin", String.valueOf(isLogin));
        mConfigSwitcherModel.requestBundleSwitcherData(params,
                new IModelAsyncResponse<ConfigSwitcherInfo>() {
                    @Override
                    public void onResponse(ConfigSwitcherInfo switcherInfo) {
                        if (switcherInfo != null) {
                            updateRemoteConfig(isLogin, switcherInfo);
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

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////


    public static boolean isEnable(@NonNull String key) {
        return mInstance.isEnableImpl(key, false);
    }

    public static boolean isEnable(@NonNull String key, boolean defaultValue) {
        return mInstance.isEnableImpl(key, defaultValue);
    }

    public static boolean isInitEnable(@NonNull String key) {
        return mInstance.isInitEnableImpl(key, false);
    }

    public static boolean isInitEnable(@NonNull String key, boolean defaultValue) {
        return mInstance.isInitEnableImpl(key, defaultValue);
    }

    public static String getConfig(@NonNull String key) {
        return getConfig(key, "");
    }

    public static String getConfig(@NonNull String key, String defaultValue) {
        return mInstance.getConfigImpl(key, defaultValue);
    }

    public static String getInitConfig(@NonNull String key) {
        return mInstance.getInitConfigImpl(key, "");
    }

    public static String getInitConfig(@NonNull String key, String defaultValue) {
        return mInstance.getInitConfigImpl(key, defaultValue);
    }

    public static int getConfigInt(@NonNull String key) {
        return getConfigInt(key, 0);
    }

    public static int getConfigInt(@NonNull String key, int defaultValue) {
        String value = getConfig(key);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInitConfigInt(@NonNull String key) {
        String value = getInitConfig(key);
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float getConfigFloat(@NonNull String key) {
        return getConfigFloat(key, 0f);
    }

    public static float getConfigFloat(@NonNull String key, float defaultValue) {
        String value = getConfig(key);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float getInitConfigFloat(@NonNull String key) {
        String value = getInitConfig(key);
        if (TextUtils.isEmpty(value)) {
            return 0f;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }

    public static void saveConfigMap(@NonNull HashMap<String, String> configMap) {
        mInstance.saveConfigMapImpl(configMap);
    }

    public static void saveConfig(@NonNull String key, String value) {
        mInstance.saveConfigImpl(key, value);
    }

    public static void saveConfig(@NonNull String key, String value, int configType) {
        mInstance.saveConfigImpl(key, value, configType);
    }

    public static void setupConfigSwitcher(final boolean isLogin, @NonNull HashMap<String, String> params,
                                           final IConfigSwitcherCallback callback) {
        mInstance.setupConfigSwitcherImpl(isLogin, params, callback);
    }

    public interface IConfigSwitcherCallback {
        void onSetupComplete();

        boolean onSetupFail();
    }

    // for test env
    public static boolean switchToTestEnv() {
        AssetManager assetManager = AppUtils.getApplication().getResources().getAssets();
        try {
            Properties properties = new Properties();
            properties.load(assetManager.open("test_config.ini"));
            Set keySet = properties.keySet();
            for (Object object : keySet) {
                String propKey = object.toString();
                String propValue = properties.getProperty(propKey);
                saveConfig(propKey, propValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
