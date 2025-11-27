package com.pine.template.main.track;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.app.lib.mqtt.framework.MqttManager;
import com.pine.app.lib.mqtt.framework.service.MqttService;
import com.pine.template.base.bgwork.AppBgManager;
import com.pine.template.base.bgwork.network.NetworkType;
import com.pine.template.base.bgwork.network.OnNetworkChangedListener;
import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.business.track.TrackDefaultBuilder;
import com.pine.template.main.R;
import com.pine.tool.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackRecordHelper implements OnNetworkChangedListener {
    private final String TAG = this.getClass().getSimpleName();

    public static TrackRecordHelper instance;

    public static synchronized TrackRecordHelper getInstance() {
        if (instance == null) {
            instance = new TrackRecordHelper();
        }
        return instance;
    }

    private final String DEFAULT_CUR_CLASS = "TrackRecordHelper";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context mContext;

    private TrackRecordHelper() {

    }

    private boolean mInit;

    public boolean isInit() {
        return mInit;
    }

    public void init(@NonNull Context context) {
        mContext = context;

        recordInfoAppStart();
        AppBgManager.regNetworkChangedListener(this);
        MqttManager.getInstance().addConnectListener(String.valueOf(context.hashCode()),
                new MqttService.IConnectionCallback() {
                    @Override
                    public void onConnected() {
                        recordInfoMqttConnect();
                    }

                    @Override
                    public void onConnecting() {

                    }

                    @Override
                    public void onDisconnect(int errCode, Throwable cause) {
                        String reason = parseDisConnectErrCode(errCode, cause);
                        recordInfoMqttDisconnect(reason);
                    }

                    @Override
                    public void reConnectByChecker() {
                        recordInfoMqttReconnectByCheck();
                    }

                    @Override
                    public void onServerState(String state) {
                        recordInfoMqttService(state);
                    }
                });
        mInit = true;

        AppTrackManager.getInstance().scheduleStartJob();
    }

    public void release(@NonNull Context context) {
        tryRecordInfoNet(true);
        recordInfoAppStop();
        AppBgManager.unRegNetworkChangedListener(this);
        MqttManager.getInstance().removeConnectListener(String.valueOf(context.hashCode()));
        mInit = false;

        AppTrackManager.getInstance().doFinishJob();
    }

    private NetworkType mNetworkType;
    private int mSignalLevel = -1;

    public String getNetworkName() {
        if (mNetworkType == null) {
            return "无网络";
        }
        switch (mNetworkType) {
            case NETWORK_2G:
                return "2G";
            case NETWORK_3G:
                return "3G";
            case NETWORK_4G:
                return "4G";
            case NETWORK_5G:
                return "5G";
            case NETWORK_ETHERNET:
                return "以太网";
            case NETWORK_WIFI:
                return "wifi";
            default:
                return "-";
        }
    }

    public int getSignalLevel() {
        return mSignalLevel;
    }

    @Override
    public void onConnected(NetworkType networkType) {
        LogUtils.d(TAG, "Network connected networkType:" + networkType);
        mNetworkType = networkType;
        String signalStr = "";
        if (mSignalLevel >= 0 && mSignalLevel < 10) {
            signalStr = mContext.getString(R.string.main_info_net_state_signal, String.valueOf(mSignalLevel));
            mRecordFirstSignalOfConnect = true;
        } else {
            mRecordFirstSignalOfConnect = false;
        }
        recordInfoNetConnect(getNetworkName(), signalStr);
    }

    private List<Integer> mSignalChangeList = new ArrayList<>();
    private boolean mRecordFirstSignalOfConnect;

    private void onSignalChange(int level) {
        if (level < 0 || level > 10) {
            return;
        }
        mSignalLevel = level;
        mSignalChangeList.add(level);
        tryRecordInfoNet(false);
    }

    private void tryRecordInfoNet(boolean immediately) {
        if (mSignalChangeList.size() < 1) {
            return;
        }
        if (mSignalChangeList.size() > 15 || immediately || !mRecordFirstSignalOfConnect) {
            String levelStr = String.valueOf(mSignalChangeList.get(0));
            for (int i = 1; i < mSignalChangeList.size(); i++) {
                levelStr += ">" + mSignalChangeList.get(i);
            }
            recordInfoNetConnectSignal(getNetworkName(), levelStr);
            mSignalChangeList.clear();
            mRecordFirstSignalOfConnect = true;
        }
    }

    @Override
    public void onWifiSignalChange(int level) {
        onSignalChange(level);
    }

    @Override
    public void onMobileSignalChange(int level) {
        onSignalChange(level);
    }

    @Override
    public void onDisconnected() {
        LogUtils.d(TAG, "Network disconnected");
        recordInfoNetDisconnect();
        tryRecordInfoNet(true);
        mNetworkType = null;
    }

    public void addLeftTrackImmediately() {
        AppTrackManager.getInstance().addLeftTrackImmediately();
    }

    public void recordInfoAppStart() {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_app_start,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_START, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoAppStop() {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_app_stop,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_STOP, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttConnect() {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_connect,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_CONNECT, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttKeepLive(String sendCount, String replyCount) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_keep_live,
                mSimpleDateFormat.format(recordDate), sendCount, replyCount,
                String.valueOf(MqttManager.getInstance().isMqttConnected()));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_KEEP_LIVE, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttKeepLiveFail(String reason) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_keep_live_fail,
                mSimpleDateFormat.format(recordDate), reason);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_KEEP_LIVE, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttReconnectByCheck() {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_reconnect_by_check,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_CONNECT, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttDisconnect(String errMsg) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_disconnect,
                mSimpleDateFormat.format(recordDate), errMsg);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_DISCONNECT, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoMqttService(String state) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_mqtt_state_service,
                mSimpleDateFormat.format(recordDate), state);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_MQTT_STATE_SERVICE, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoNetConnect(String netType, String signal) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_net_state_connect,
                mSimpleDateFormat.format(recordDate), netType, signal);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_NET_STATE_CONNECT, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoNetConnectSignal(String netType, String signal) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_net_state_connect_signal,
                mSimpleDateFormat.format(recordDate), netType, signal);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_NET_STATE_CONNECT_SIGNAL, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoNetDisconnect() {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.main_info_net_state_disconnect,
                mSimpleDateFormat.format(recordDate));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_NET_STATE_DISCONNECT, actionData,
                recordDate.getTime(), true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    private String parseDisConnectErrCode(int errCode, Throwable cause) {
        switch (errCode) {
            case MqttService.IConnectionCallback.REASON_DEFAULT:
                return "REASON_DEFAULT";
            case MqttService.IConnectionCallback.REASON_EXCEPTION:
                return "REASON_EXCEPTION";
            case MqttService.IConnectionCallback.REASON_FUN_NOT_ENABLE:
                return "REASON_FUN_NOT_ENABLE";
            case MqttService.IConnectionCallback.REASON_NOT_INIT:
                return "REASON_NOT_INIT";
            case MqttService.IConnectionCallback.REASON_CONNECT_FAIL:
                return cause.getMessage();
            case MqttService.IConnectionCallback.REASON_NET_DISCONNECT:
                return "REASON_NET_DISCONNECT";
            case MqttService.IConnectionCallback.REASON_SUBSCRIBE_FAIL:
                return "REASON_SUBSCRIBE_FAIL";
            case MqttService.IConnectionCallback.REASON_RELEASE:
                return "REASON_RELEASE";
            default:
                return "UNKNOWN";
        }
    }
}
