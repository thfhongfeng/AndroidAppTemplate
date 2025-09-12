package com.pine.template.main.mqtt;

import android.text.TextUtils;

import com.pine.app.lib.mqtt.framework.BaseParams;
import com.pine.app.lib.mqtt.framework.MqttConfig;
import com.pine.app.lib.mqtt.framework.SubscribeInfo;
import com.pine.app.lib.mqtt.framework.Topic;
import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.base.DeviceConfig;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.template.main.MainApplication;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MqttConfigBuilder {
    private static final String TAG = MqttConfigBuilder.class.getSimpleName();

    public final static String MQTT_TOPIC_HEAD = "persist.vendor.sc_mqtt_topic_head";
    public final static String MQTT_USERNAME = "persist.vendor.sc_mqtt_username";
    public final static String MQTT_PWD = "persist.vendor.sc_mqtt_pwd";

    public static MqttConfig buildConfig() {
        MqttConfig config = new MqttConfig();

        String topicHead = getMqttTopicHead();
        config.setTopicHead(topicHead);

        config.setHost(getMqttHost());

        String username = getMqttUsername();
        config.setUsername(username);

        String pwd = getMqttPwd();
        config.setPwd(pwd);

        config.setConnectionTimeout(10);
        config.setKeepAliveInterval(120);
        config.setMySubject(Topic.SubjectEnum.PHONE.getContent());
        config.setMyId(MainApplication.DEVICE_ID);
        config.setSubscribeBroadcastQos(1);
        config.setSubscribeMyQos(1);
        config.setBaseParams(buildBaseParams());

        List<SubscribeInfo.SubscribeItem> topic = new ArrayList<>();
        String deviceTypeGroup = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_DEVICE_TYPE_GROUP, "");
        if (!TextUtils.isEmpty(deviceTypeGroup)) {
            topic.add(new SubscribeInfo.SubscribeItem(deviceTypeGroup, 1));
        }
        config.setSubscribeGroupList(topic);
        return config;
    }

    public static Topic.SubjectEnum get2CSubject() {
        return Topic.SubjectEnum.SERVER;
    }

    public static String get2CSubjectFlag() {
        return "dcmrt";
    }

    public static String getMqttTopicHead() {
        return getMqttTopicHead(false);
    }

    public static String getMqttTopicHead(boolean initConfig) {
        String topicHead = "";
        if (initConfig) {
            topicHead = ConfigSwitcherServer.getInitConfig(BuildConfigKey.CONFIG_MQTT_TOPIC_HEAD);
        } else {
            topicHead = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_TOPIC_HEAD);
        }
        if (TextUtils.isEmpty(topicHead)) {
            try {
                topicHead = DeviceSdkManager.getInstance().getProperty(MQTT_TOPIC_HEAD, "");
            } catch (DeviceSdkException e) {
                LogUtils.w(TAG, "DeviceSdkException for getProperty, ignore");
            }
        }
        if (TextUtils.isEmpty(topicHead)) {
            topicHead = "aics_mrt";
        }
        // 测试模式
        if (BuildConfig.DEBUG && topicHead.endsWith("_test")) {
            String host = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_SERVER_URL);
            if (!TextUtils.isEmpty(host)) {
                String[] hostArr = host.replaceAll("/", "").split(":");
                if (hostArr.length >= 2) {
                    String ipAddress = hostArr[1];
                    if (!TextUtils.isEmpty(ipAddress)) {
                        String index = ipAddress.replaceAll("\\.", "_");
                        topicHead = topicHead + "_" + index;
                    }
                }
            }
        }
        return topicHead;
    }

    public static void setMqttTopicHead(String topicHead) {
        ConfigSwitcherServer.saveConfig(BuildConfigKey.CONFIG_MQTT_TOPIC_HEAD, topicHead);
    }

    public static String getMqttHost() {
        return getMqttHost(false);
    }

    public static String getMqttHost(boolean initConfig) {
        if (initConfig) {
            return ConfigSwitcherServer.getInitConfig(BuildConfigKey.CONFIG_MQTT_HOST, "tcp:mqtt.minicreate.com:1883");
        } else {
            return ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_HOST, "tcp:mqtt.minicreate.com:1883");
        }
    }

    public static void setMqttHost(String hostUrl) {
        ConfigSwitcherServer.saveConfig(BuildConfigKey.CONFIG_MQTT_HOST, hostUrl);
    }

    public static String getMqttUsername() {
        return getMqttUsername(false);
    }

    public static String getMqttUsername(boolean initConfig) {
        String username = "";
        if (initConfig) {
            username = ConfigSwitcherServer.getInitConfig(BuildConfigKey.CONFIG_MQTT_USERNAME);
        } else {
            username = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_USERNAME);
        }
        if (TextUtils.isEmpty(username)) {
            try {
                username = DeviceSdkManager.getInstance().getProperty(MQTT_USERNAME, "");
            } catch (DeviceSdkException e) {
                LogUtils.w(TAG, "DeviceSdkException for getProperty, ignore");
            }
        }
        if (TextUtils.isEmpty(username)) {
            username = "dcmips";
        }
        return username;
    }

    public static void setMqttUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return;
        }
        ConfigSwitcherServer.saveConfig(BuildConfigKey.CONFIG_MQTT_USERNAME, username);
    }

    public static String getMqttPwd() {
        return getMqttPwd(false);
    }

    public static String getMqttPwd(boolean initConfig) {
        String pwd = "";
        if (initConfig) {
            pwd = ConfigSwitcherServer.getInitConfig(BuildConfigKey.CONFIG_MQTT_PWD);
        } else {
            pwd = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_PWD);
        }
        if (TextUtils.isEmpty(pwd)) {
            try {
                pwd = DeviceSdkManager.getInstance().getProperty(MQTT_PWD, "");
            } catch (DeviceSdkException e) {
                LogUtils.w(TAG, "DeviceSdkException for getProperty, ignore");
            }
        }
        if (TextUtils.isEmpty(pwd)) {
            pwd = "123qwe!@#";
        }
        return pwd;
    }

    public static void setMqttPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return;
        }
        ConfigSwitcherServer.saveConfig(BuildConfigKey.CONFIG_MQTT_PWD, pwd);
    }

    public static boolean isMyDeviceTypeGroup(String group) {
        String deviceTypeGroup = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_MQTT_DEVICE_TYPE_GROUP, "");
        return TextUtils.isEmpty(deviceTypeGroup) || TextUtils.equals(deviceTypeGroup, group);
    }

    public static BaseParams buildBaseParams() {
        BaseParams baseParams = new BaseParams();
        baseParams.setFlagId(MainApplication.DEVICE_ID);
        baseParams.setAppKey(MainApplication.APP_KEY);
        baseParams.setSdkType(MainApplication.SDK_TYPE);
        baseParams.setVersionName(MainApplication.VERSION_NAME);
        baseParams.setVersionCode(MainApplication.VERSION_CODE);
        baseParams.setProductCustomer(DeviceConfig.getProductCustomer());
        return baseParams;
    }
}