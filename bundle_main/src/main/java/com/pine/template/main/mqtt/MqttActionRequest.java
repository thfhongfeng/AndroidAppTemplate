package com.pine.template.main.mqtt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pine.app.lib.mqtt.framework.MqttManager;
import com.pine.app.lib.mqtt.framework.listener.MqttRespond;
import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.main.mqtt.mode.KeepAliveMode;

public class MqttActionRequest {
    private final String TAG = this.getClass().getSimpleName();

    protected Gson sGson = new GsonBuilder().disableHtmlEscaping().create();

    private static MqttActionRequest instance;

    private MqttActionRequest() {
    }

    public static synchronized MqttActionRequest getInstance() {
        if (instance == null) {
            instance = new MqttActionRequest();
        }
        return instance;
    }

    protected boolean isPrepared() {
        return MqttClient.getInstance().isPrepared();
    }

    protected boolean isPrepared(MqttRespond.IMqttRespond callback) {
        return MqttClient.getInstance().isPrepared(callback);
    }

    private void saveTtl(int ttl) {
        if (ttl <= 0) {
            ttl = 60;
        }
        ConfigSwitcherServer.saveConfig(BuildConfigKey.CACHE_MQTT_TTL_INTERVAL,
                String.valueOf(ttl));
    }

    protected boolean keepLive(String tag, KeepAliveMode mode, MqttRespond.IMqttRespond<KeepAliveMode> callback) {
        if (!isPrepared(callback)) {
            return false;
        }
        MqttManager.getInstance().request(tag, RequestAction.A_KEEP_LIVE,
                MqttConfigBuilder.get2CSubject(), MqttConfigBuilder.get2CSubjectFlag(), mode, new MqttRespond.IMqttRespond<String>() {
                    @Override
                    public void onReply(String data) {
                        KeepAliveMode mode = sGson.fromJson(data,
                                new TypeToken<KeepAliveMode>() {
                                }.getType());
                        saveTtl(mode == null ? 0 : mode.getTtl());
                        if (callback != null) {
                            callback.onReply(mode);
                        }
                    }

                    @Override
                    public void onFail(int errCode) {
                        if (callback != null) {
                            callback.onFail(errCode);
                        }
                    }
                });
        return true;
    }
}
