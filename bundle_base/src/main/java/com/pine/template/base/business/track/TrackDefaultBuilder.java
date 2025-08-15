package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.business.track.entity.AppTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TrackDefaultBuilder {
    public static final String MODULE_DEFAULT = "app_track_default";
    public static final String MODULE_BASE = "app_track_base";

    ///////////////////////////////////////////////////////////////////////////////
    public static final String MODULE_BUSINESS_RECORD = "module_business_record";
    public static final String MODULE_ADMIN_RECORD = "module_admin_record";
    public static final String MODULE_REMOTE_RECORD = "module_remote_record";
    public static final String MODULE_STATE_INFO = "module_state_info";

    ////////////////////////////////////////////////////////////////////////////////
    // MODULE_BASE
    public static final String TEST = "OpTest";
    // MODULE_ADMIN_RECORD
    public static final String ADMIN_GO_SETTINGS = "OpAdminGoSettings";
    public static final String ADMIN_FINISH_APP = "OpAdminFinishApp";
    // MODULE_REMOTE_RECORD
    public static final String REMOTE_REBOOT = "OpRemoteReboot";
    public static final String REMOTE_RESET_PWD = "OpRemoteResetPwd";
    public static final String REMOTE_CONFIG_DEVICE = "OpRemoteConfigDevice";
    // MODULE_STATE_INFO
    public static final String APP_UPDATE = "AppUpdate";
    public static final String APP_START = "AppStart";
    public static final String APP_STOP = "AppStop";
    public static final String APP_UI_ENTER = "AppUiEnter";
    public static final String APP_UI_EXIT = "AppUiExit";
    public static final String INFO_STATE_REG_ACTIVE = "InfoRegActive";
    public static final String INFO_MQTT_STATE_CONNECT = "InfoMqttConnect";
    public static final String INFO_MQTT_STATE_KEEP_LIVE = "InfoMqttKeepLive";
    public static final String INFO_MQTT_STATE_RECONNECT_BY_CHECKER = "InfoMqttReconnectByChecker";
    public static final String INFO_MQTT_STATE_DISCONNECT = "InfoMqttDisconnect";
    public static final String INFO_MQTT_STATE_SERVICE = "InfoMqttService";
    public static final String INFO_NET_STATE_CONNECT = "InfoNetConnect";
    public static final String INFO_NET_STATE_CONNECT_SIGNAL = "InfoNetConnectSignal";
    public static final String INFO_NET_STATE_DISCONNECT = "InfoNetDisconnect";
    public static final String INFO_STORAGE_NOT_ENOUGH = "InfoStorageNotEnough";

    public static void buildDefaultModuleMap(@NonNull ConcurrentHashMap<String, TrackModuleInfo> map,
                                             @NonNull ConcurrentHashMap<String, TrackActionInfo> actionMap) {
        TrackModuleInfo info = new TrackModuleInfo(MODULE_DEFAULT, "默认模块", true);
        map.put(MODULE_DEFAULT, info);
        info = new TrackModuleInfo(MODULE_BASE, "基础模块", true);
        List<TrackActionInfo> actionInfoList = getBaseActions(actionMap);
        info.setActions(actionInfoList);
        map.put(MODULE_BASE, info);

        info = new TrackModuleInfo(MODULE_ADMIN_RECORD, "管理模块", true);
        actionInfoList = getAdminActions(actionMap);
        info.setActions(actionInfoList);
        map.put(MODULE_ADMIN_RECORD, info);
        info = new TrackModuleInfo(MODULE_REMOTE_RECORD, "远程控制模块", true);
        actionInfoList = getRemoteOpActions(actionMap);
        info.setActions(actionInfoList);
        map.put(MODULE_REMOTE_RECORD, info);
        info = new TrackModuleInfo(MODULE_STATE_INFO, "应用状态模块", true);
        actionInfoList = getAppStateActions(actionMap);
        info.setActions(actionInfoList);
        map.put(MODULE_STATE_INFO, info);
        info = new TrackModuleInfo(MODULE_BUSINESS_RECORD, "业务模块", true);
        map.put(MODULE_BUSINESS_RECORD, info);
    }

    public static List<String> buildDefaultTrackModuleList() {
        List<String> list = new ArrayList<>();
        list.add(MODULE_ADMIN_RECORD);
        list.add(MODULE_BUSINESS_RECORD);
        list.add(MODULE_REMOTE_RECORD);
        list.add(MODULE_STATE_INFO);
        return list;
    }

    private static List<TrackActionInfo> getBaseActions(
            @NonNull ConcurrentHashMap<String, TrackActionInfo> actionMap) {
        List<TrackActionInfo> list = new ArrayList<>();
        TrackActionInfo action = new TrackActionInfo(TEST, "测试");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        return list;
    }

    private static List<TrackActionInfo> getAdminActions(
            @NonNull ConcurrentHashMap<String, TrackActionInfo> actionMap) {
        List<TrackActionInfo> list = new ArrayList<>();
        TrackActionInfo action = new TrackActionInfo(ADMIN_GO_SETTINGS, "管理员进设置");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(ADMIN_FINISH_APP, "管理员退出应用");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        return list;
    }

    private static List<TrackActionInfo> getAppStateActions(
            @NonNull ConcurrentHashMap<String, TrackActionInfo> actionMap) {
        List<TrackActionInfo> list = new ArrayList<>();
        TrackActionInfo action = new TrackActionInfo(APP_UI_ENTER, "进入界面");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(APP_UI_EXIT, "退出界面");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(APP_UPDATE, "应用更新");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(APP_START, "应用启动");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(APP_STOP, "应用退出");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_STATE_REG_ACTIVE, "激活状态");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_MQTT_STATE_CONNECT, "MQTT连接");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_MQTT_STATE_KEEP_LIVE, "MQTT心跳");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_MQTT_STATE_RECONNECT_BY_CHECKER, "MQTT检查机制");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_MQTT_STATE_DISCONNECT, "MQTT断开");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_MQTT_STATE_SERVICE, "MQTT服务");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_NET_STATE_CONNECT, "网络连接");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_NET_STATE_CONNECT_SIGNAL, "网络信号变化");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_NET_STATE_DISCONNECT, "网络断开");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(INFO_STORAGE_NOT_ENOUGH, "存储不足");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        return list;
    }

    private static List<TrackActionInfo> getRemoteOpActions(
            @NonNull ConcurrentHashMap<String, TrackActionInfo> actionMap) {
        List<TrackActionInfo> list = new ArrayList<>();
        TrackActionInfo action = new TrackActionInfo(REMOTE_REBOOT, "远程重启设备");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(REMOTE_RESET_PWD, "远程重置密码");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        action = new TrackActionInfo(REMOTE_CONFIG_DEVICE, "远程配置设备");
        actionMap.put(action.getActionName(), action);
        list.add(action);
        return list;
    }

    public static AppTrack getDeleteOldDataTrackForDbOut(Context context, String moduleTag, int count) {
        AppTrack appTrack = new AppTrack();
        appTrack.setModuleTag(MODULE_BASE);
        appTrack.setTrackType(9999);
        appTrack.setCurClass(AppTrackRepository.class.getSimpleName());
        appTrack.setActionName("db_exceeded_del");
        appTrack.setActionData("delete " + (TextUtils.isEmpty(moduleTag) ? "" : moduleTag + " ")
                + count + " tracks for db exceeded");
        appTrack.setActionInStamp(System.currentTimeMillis());
        appTrack.setActionOutStamp(System.currentTimeMillis());
        AppTrackManager.getInstance().getTrackAdapter().setupBaseInfoAndIp(context, appTrack);
        return appTrack;
    }

    public static AppTrack getDeleteOldDataTrackForStorageOut(Context context, int count) {
        AppTrack appTrack = new AppTrack();
        appTrack.setModuleTag(MODULE_BASE);
        appTrack.setTrackType(9999);
        appTrack.setCurClass(AppTrackRepository.class.getSimpleName());
        appTrack.setActionName("db_exceeded_del");
        appTrack.setActionData("delete " + count + " tracks for storage exceeded");
        appTrack.setActionInStamp(System.currentTimeMillis());
        appTrack.setActionOutStamp(System.currentTimeMillis());
        AppTrackManager.getInstance().getTrackAdapter().setupBaseInfoAndIp(context, appTrack);
        return appTrack;
    }
}