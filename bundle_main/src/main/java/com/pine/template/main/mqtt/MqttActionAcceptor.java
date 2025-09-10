package com.pine.template.main.mqtt;

import android.content.Context;

import com.pine.app.lib.mqtt.framework.AcceptAction;
import com.pine.app.lib.mqtt.framework.ReplyData;
import com.pine.app.lib.mqtt.framework.Topic;
import com.pine.app.lib.mqtt.framework.listener.MqttRespond;
import com.pine.template.base.DeviceConfig;
import com.pine.template.main.mqtt.entity.GoPageInfo;
import com.pine.template.main.mqtt.mode.DeviceOpMode;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class MqttActionAcceptor {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = AppUtils.getApplicationContext();

    private boolean checkOpParams(Topic topic, ReplyData replyData, DeviceOpMode mode) {
        if (mode == null) {
            replyData.setCode(MqttRespond.ERR_PARAM_ILLEGAL);
            return false;
        } else {
            if (!topic.isGroup && !DeviceConfig.isMyDeviceId(mContext, mode.getOpTarget())) {
                replyData.setCode(MqttRespond.ERR_PARAM_ILLEGAL);
                return false;
            }
            return true;
        }
    }

    // 跳转页面
    @AcceptAction(acceptAction = "goPage")
    public ReplyData goPage(Topic topic, DeviceOpMode<GoPageInfo> mode) {
        DeviceOpMode<Boolean> replyMode = new DeviceOpMode();
        ReplyData<DeviceOpMode<Boolean>> replyData = ReplyData.build(replyMode);
        if (checkOpParams(topic, replyData, mode)) {
            GoPageInfo data = mode.getData();
            LogUtils.d(TAG, "Remote Op -> goPage, data:" + data);
            if (data == null) {
                replyData.setCode(MqttRespond.ERR_PARAM_ILLEGAL);
                return replyData;
            }
            // do go page action
            replyMode.setData(true);
            replyData.setSuccess(true);
            replyMode.setOpSuccess(true);
        }
        return replyData;
    }
}
