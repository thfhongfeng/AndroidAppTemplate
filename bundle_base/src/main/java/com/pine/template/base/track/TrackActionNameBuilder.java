package com.pine.template.base.track;

import java.util.ArrayList;
import java.util.List;

public class TrackActionNameBuilder {
    // MODULE_BASE
    public static final String TEST = "OpTest";

    // MODULE_BUSINESS_RECORD

    // MODULE_ADMIN_RECORD
    public static final String ADMIN_GO_SETTINGS = "OpAdminGoSettings";
    public static final String ADMIN_FINISH_APP = "OpAdminFinishApp";
    public static final String ADMIN_CLOCK_IN_SPM = "OpAdminClockInSpm";

    // MODULE_REMOTE_RECORD
    public static final String REMOTE_REBOOT = "OpRemoteReboot";
    public static final String REMOTE_RESET_PWD = "OpRemoteResetPwd";
    public static final String REMOTE_CONFIG_DEVICE = "OpRemoteConfigDevice";
    public static final String REMOTE_OPEN_LOCK = "OpRemoteOpenLock";

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

    private static List<String> getBaseActions() {
        ArrayList<String> actionNames = new ArrayList<>();
        actionNames.add(TEST);
        return actionNames;
    }

    private static List<String> getBusinessActions() {
        ArrayList<String> actionNames = new ArrayList<>();
        return actionNames;
    }

    private static List<String> getAdminActions() {
        ArrayList<String> actionNames = new ArrayList<>();
        actionNames.add(ADMIN_GO_SETTINGS);
        actionNames.add(ADMIN_FINISH_APP);
        actionNames.add(ADMIN_CLOCK_IN_SPM);
        return actionNames;
    }

    private static List<String> getRemoteActions() {
        ArrayList<String> actionNames = new ArrayList<>();
        actionNames.add(REMOTE_REBOOT);
        actionNames.add(REMOTE_RESET_PWD);
        actionNames.add(REMOTE_CONFIG_DEVICE);
        actionNames.add(REMOTE_OPEN_LOCK);
        return actionNames;
    }

    private static List<String> getStateActions() {
        ArrayList<String> actionNames = new ArrayList<>();
        actionNames.add(APP_UI_ENTER);
//        actionNames.add(APP_UI_EXIT);
        actionNames.add(APP_UPDATE);
        actionNames.add(APP_START);
        actionNames.add(APP_STOP);
        actionNames.add(INFO_STATE_REG_ACTIVE);
        actionNames.add(INFO_MQTT_STATE_CONNECT);
        actionNames.add(INFO_MQTT_STATE_KEEP_LIVE);
        actionNames.add(INFO_MQTT_STATE_RECONNECT_BY_CHECKER);
        actionNames.add(INFO_MQTT_STATE_DISCONNECT);
        actionNames.add(INFO_MQTT_STATE_SERVICE);
        actionNames.add(INFO_NET_STATE_CONNECT);
        actionNames.add(INFO_NET_STATE_CONNECT_SIGNAL);
        actionNames.add(INFO_NET_STATE_DISCONNECT);
        actionNames.add(INFO_STORAGE_NOT_ENOUGH);
        return actionNames;
    }

    public static String getDesc(String action) {
        switch (action) {
            case TEST:
                return "测试";
            case APP_UI_ENTER:
                return "进入界面";
            case APP_UI_EXIT:
                return "退出界面";
            case APP_UPDATE:
                return "应用更新";
            case APP_START:
                return "应用启动";
            case APP_STOP:
                return "应用退出";
            case ADMIN_GO_SETTINGS:
                return "管理员进设置";
            case ADMIN_FINISH_APP:
                return "管理员退出应用";
            case ADMIN_CLOCK_IN_SPM:
                return "维护打卡";
            case REMOTE_REBOOT:
                return "远程重启设备";
            case REMOTE_RESET_PWD:
                return "远程重置密码";
            case REMOTE_CONFIG_DEVICE:
                return "远程配置设备";
            case REMOTE_OPEN_LOCK:
                return "远程开锁";
            case INFO_STATE_REG_ACTIVE:
                return "激活状态";
            case INFO_MQTT_STATE_CONNECT:
                return "MQTT连接";
            case INFO_MQTT_STATE_KEEP_LIVE:
                return "MQTT心跳";
            case INFO_MQTT_STATE_RECONNECT_BY_CHECKER:
                return "MQTT检查机制";
            case INFO_MQTT_STATE_DISCONNECT:
                return "MQTT断开";
            case INFO_MQTT_STATE_SERVICE:
                return "MQTT服务";
            case INFO_NET_STATE_CONNECT:
                return "网络连接";
            case INFO_NET_STATE_CONNECT_SIGNAL:
                return "网络信号变化";
            case INFO_NET_STATE_DISCONNECT:
                return "网络断开";
            case INFO_STORAGE_NOT_ENOUGH:
                return "存储不足";
            default:
                return action;
        }
    }

    public static ArrayList<String> getAllActionNames() {
        ArrayList<String> actionNames = new ArrayList<>();
        actionNames.addAll(getBaseActions());
        actionNames.addAll(getBusinessActions());
        actionNames.addAll(getAdminActions());
        actionNames.addAll(getRemoteActions());
        actionNames.addAll(getStateActions());
        return actionNames;
    }

    public static ArrayList<TrackActionNameInfo> getAllActionInfos() {
        ArrayList<TrackActionNameInfo> actionInfos = new ArrayList<>();
        actionInfos.add(buildTrackActionInfo("module_business_record", "业务模块", getBusinessActions()));
        actionInfos.add(buildTrackActionInfo("module_admin_record", "管理模块", getAdminActions()));
        actionInfos.add(buildTrackActionInfo("module_remote_record", "远程控制模块", getRemoteActions()));
        actionInfos.add(buildTrackActionInfo("module_state_info", "应用状态模块", getStateActions()));
        actionInfos.add(buildTrackActionInfo("app_track_base", "其它模块", getBaseActions()));
        return actionInfos;
    }

    private static TrackActionNameInfo buildTrackActionInfo(String moduleName, String moduleDesc,
                                                            List<String> actionNames) {
        TrackActionNameInfo info = new TrackActionNameInfo(moduleName, moduleDesc);
        List<TrackActionName> list = new ArrayList<>();
        for (String action : actionNames) {
            TrackActionName trackActionName = new TrackActionName(action, getDesc(action));
            list.add(trackActionName);
        }
        info.setActions(list);
        return info;
    }

    public static ArrayList<String> buildActionValue(ArrayList<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionNames();
        }
        return actionNames;
    }

    public static String[] buildActionDesc(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionNames();
        }
        String[] descs = new String[actionNames.size()];
        for (int i = 0; i < actionNames.size(); i++) {
            descs[i] = getDesc(actionNames.get(i));
        }
        return descs;
    }

    public static String buildActionDescTxt(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionNames();
        }
        if (actionNames.size() < 1) {
            return "";
        }
        String txt = getDesc(actionNames.get(0));
        for (int i = 1; i < actionNames.size(); i++) {
            txt += "," + getDesc(actionNames.get(i));
        }
        return txt;
    }

    public static int[] buildAllSelect(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionNames();
        }
        int[] select = new int[actionNames.size()];
        for (int i = 0; i < actionNames.size(); i++) {
            select[i] = i;
        }
        return select;
    }
}
