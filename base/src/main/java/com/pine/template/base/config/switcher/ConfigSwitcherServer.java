package com.pine.template.base.config.switcher;

import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.BaseApplication;
import com.pine.template.base.KeyConstants;
import com.pine.template.base.config.bean.ConfigSwitcherEntity;
import com.pine.template.base.config.bean.ConfigSwitcherInfo;
import com.pine.template.base.config.model.ConfigSwitcherModel;
import com.pine.tool.request.response.IAsyncResponse;
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

    private static final String DEFAULT_CONFIG_FILE = "config.ini";

    private static volatile ConfigSwitcherServer mInstance;
    private ConfigSwitcherModel mConfigSwitcherModel = new ConfigSwitcherModel();

    private Map<String, ConfigSwitcherEntity> mInitConfigStateMap = new HashMap();
    private Map<String, ConfigSwitcherEntity> mGuestConfigStateMap = new HashMap();
    private Map<String, ConfigSwitcherEntity> mUserConfigStateMap = new HashMap();

    public synchronized static void init() {
        init(DEFAULT_CONFIG_FILE);
    }

    public synchronized static void init(@NonNull String configFileName) {
        if (mInstance == null) {
            mInstance = new ConfigSwitcherServer();
            mInstance.initLocalConfig(configFileName);
        }
    }

    private ConfigSwitcherServer() {

    }

    boolean isAssertFileExists(AssetManager assetManager, String filename) {
        try {
            String[] files = assetManager.list("");
            for (String file : files) {
                if (file.equals(filename)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private synchronized void initLocalConfig(String configFileName) {
        AssetManager assetManager = AppUtils.getApplication().getResources().getAssets();
        if (TextUtils.isEmpty(configFileName) || !isAssertFileExists(assetManager, configFileName)) {
            LogUtils.d(TAG, "use config file:" + configFileName
                    + " not exist, use default config file:" + DEFAULT_CONFIG_FILE);
            configFileName = DEFAULT_CONFIG_FILE;
        }
        try {
            Properties properties = new Properties();
            LogUtils.d(TAG, "use config file:" + configFileName);
            // 打开INI文件的InputStream
            InputStream inputStream = assetManager.open(configFileName);
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
                SharePreferenceUtils.readMapFromConfig(KeyConstants.USER_CONFIG_KEY,
                        String.class, ConfigSwitcherEntity.class);
        if (userConfigMap == null || userConfigMap.size() < 1) {
            checkInitConfigAndCopy(mInitConfigStateMap, mUserConfigStateMap);
        } else {
            checkInitConfigAndCopy(userConfigMap, mUserConfigStateMap);
        }

        Map<String, ConfigSwitcherEntity> guestConfigMap =
                SharePreferenceUtils.readMapFromConfig(KeyConstants.GUEST_CONFIG_KEY, String.class,
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
        if (BaseApplication.isLogin()) {
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
        if (BaseApplication.isLogin()) {
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
        if (BaseApplication.isLogin()) {
            Set<String> keySet = configMap.keySet();
            for (String key : keySet) {
                mUserConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                        ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
            }
            SharePreferenceUtils.saveToConfig(KeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        } else {
            Set<String> keySet = configMap.keySet();
            for (String key : keySet) {
                mGuestConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                        ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
            }
            SharePreferenceUtils.saveToConfig(KeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
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
        if (BaseApplication.isLogin()) {
            mUserConfigStateMap.put(key,
                    new ConfigSwitcherEntity(key, value, configType));
            SharePreferenceUtils.saveToConfig(KeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        } else {
            mGuestConfigStateMap.put(key,
                    new ConfigSwitcherEntity(key, value, configType));
            SharePreferenceUtils.saveToConfig(KeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
        }
    }

    public synchronized void saveConfigMapAllStateImpl(@NonNull HashMap<String, String> configMap) {
        if (configMap == null || configMap.size() < 1) {
            return;
        }
        Set<String> keySet = configMap.keySet();
        for (String key : keySet) {
            mUserConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                    ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
            mGuestConfigStateMap.put(key, new ConfigSwitcherEntity(key, configMap.get(key),
                    ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER));
        }
        SharePreferenceUtils.saveToConfig(KeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        SharePreferenceUtils.saveToConfig(KeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
    }

    public synchronized void saveConfigAllStateImpl(@NonNull String key, String value) {
        saveConfigAllStateImpl(key, value, ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER);
    }

    public synchronized void saveConfigAllStateImpl(@NonNull String key, String value, int configType) {
        if (TextUtils.isEmpty(value)) {
            ConfigSwitcherEntity entity = mInitConfigStateMap.get(key);
            if (entity != null) {
                value = entity.getValue();
            } else {
                value = "";
            }
        }
        mUserConfigStateMap.put(key,
                new ConfigSwitcherEntity(key, value, configType));
        SharePreferenceUtils.saveToConfig(KeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);

        mGuestConfigStateMap.put(key,
                new ConfigSwitcherEntity(key, value, configType));
        SharePreferenceUtils.saveToConfig(KeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
    }

    public synchronized boolean updateRemoteConfigImpl(@NonNull ConfigSwitcherInfo switcherInfo) {
        String version = SharePreferenceUtils.readStringFromConfig(KeyConstants.CONFIG_REMOTE_VERSION_CODE, "");
        if (TextUtils.isEmpty(switcherInfo.getVersion()) || TextUtils.equals(version, switcherInfo.getVersion())) {
            return false;
        }
        List<ConfigSwitcherEntity> list = switcherInfo.getLocalConfigList();
        if (list == null) {
            return false;
        }
        boolean guestChange = false;
        boolean userChange = false;
        for (int i = 0; i < list.size(); i++) {
            ConfigSwitcherEntity entity = list.get(i);
            if (entity == null) {
                continue;
            }
            entity.setConfigType(ConfigSwitcherEntity.CONFIG_TYPE_REMOTE);
            if (switcherInfo.isAllState() || switcherInfo.isGuestState()) {
                if (shouldOverrideConfig(entity, mGuestConfigStateMap.get(entity.getKey()))) {
                    mGuestConfigStateMap.put(entity.getKey(), entity);
                    guestChange = true;
                }
            }
            if (switcherInfo.isAllState() || switcherInfo.isLoginState()) {
                if (shouldOverrideConfig(entity, mUserConfigStateMap.get(entity.getKey()))) {
                    mUserConfigStateMap.put(entity.getKey(), entity);
                    userChange = true;
                }
            }
        }
        SharePreferenceUtils.saveToConfig(KeyConstants.CONFIG_REMOTE_VERSION_CODE, version);

        if (guestChange) {
            SharePreferenceUtils.saveToConfig(KeyConstants.GUEST_CONFIG_KEY, mGuestConfigStateMap);
        }
        if (userChange) {
            SharePreferenceUtils.saveToConfig(KeyConstants.USER_CONFIG_KEY, mUserConfigStateMap);
        }
        return guestChange || userChange;
    }

    private boolean shouldOverrideConfig(ConfigSwitcherEntity srcEntity, ConfigSwitcherEntity targetEntity) {
        if (srcEntity == null) {
            return false;
        }
        if (targetEntity == null || srcEntity.isForce()
                || targetEntity.getConfigType() != ConfigSwitcherEntity.CONFIG_TYPE_LOCAL_USER) {
            return true;
        }
        return false;
    }

    public void setupConfigSwitcherImpl(final String configUrl,
                                        @NonNull HashMap<String, String> params,
                                        final IConfigSwitcherCallback callback) {
        if (!ENABLE_REMOTE_LOADING_CONFIG_SWITCH) {
            callback.onSetupComplete(false);
            return;
        }
        String version = SharePreferenceUtils.readStringFromConfig(KeyConstants.CONFIG_REMOTE_VERSION_CODE, "");
        if (!TextUtils.isEmpty(version)) {
            params.put("version", version);
        }
        mConfigSwitcherModel.requestBundleSwitcherData(configUrl, params,
                new IAsyncResponse<ConfigSwitcherInfo>() {
                    @Override
                    public void onResponse(ConfigSwitcherInfo switcherInfo) {
                        boolean change = false;
                        if (switcherInfo != null) {
                            change = updateRemoteConfigImpl(switcherInfo);
                        }
                        if (callback != null) {
                            callback.onSetupComplete(change);
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

    public static void saveConfigMapAllState(@NonNull HashMap<String, String> configMap) {
        mInstance.saveConfigMapAllStateImpl(configMap);
    }

    public static void saveConfigAllState(@NonNull String key, String value) {
        mInstance.saveConfigAllStateImpl(key, value);
    }

    public static void saveConfigAllState(@NonNull String key, String value, int configType) {
        mInstance.saveConfigAllStateImpl(key, value, configType);
    }

    public static void setupConfigSwitcher(String configUrl,
                                           @NonNull HashMap<String, String> params,
                                           final IConfigSwitcherCallback callback) {
        mInstance.setupConfigSwitcherImpl(configUrl, params, callback);
    }

    public static void updateRemoteConfig(@NonNull ConfigSwitcherInfo switcherInfo,
                                          final IConfigSwitcherCallback callback) {
        boolean change = mInstance.updateRemoteConfigImpl(switcherInfo);
        if (callback != null) {
            callback.onSetupComplete(change);
        }
    }

    public interface IConfigSwitcherCallback {
        void onSetupComplete(boolean change);

        boolean onSetupFail();
    }

    // for test env
    public static boolean switchToTestEnv(String configFileName) {
        if (TextUtils.isEmpty(configFileName)) {
            return false;
        }
        AssetManager assetManager = AppUtils.getApplication().getResources().getAssets();
        try {
            Properties properties = new Properties();
            properties.load(assetManager.open(configFileName));
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
