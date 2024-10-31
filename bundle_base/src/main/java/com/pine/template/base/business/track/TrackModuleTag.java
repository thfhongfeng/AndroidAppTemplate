package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import com.pine.template.base.business.track.entity.AppTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackModuleTag {
    public static final String MODULE_DEFAULT = "app_track_default";
    public static final String MODULE_BASE = "app_track_base";

    ///////////////////////////////////////////////////////////////////////////////
    public static final String MODULE_BUSINESS_RECORD = "module_business_record";
    public static final String MODULE_ADMIN_RECORD = "module_admin_record";
    public static final String MODULE_REMOTE_RECORD = "module_remote_record";

    public static void buildModuleMap(Map<String, Boolean> map) {
        map.put(MODULE_DEFAULT, true);
        map.put(MODULE_BASE, true);

        map.put(MODULE_ADMIN_RECORD, true);
        map.put(MODULE_REMOTE_RECORD, true);

        map.put(MODULE_BUSINESS_RECORD, false);
    }

    public static List<String> getOperationModuleList() {
        List<String> list = new ArrayList<>();
        list.add(MODULE_ADMIN_RECORD);
        list.add(MODULE_BUSINESS_RECORD);
        list.add(MODULE_REMOTE_RECORD);
        return list;
    }

    public static AppTrack getDeleteOldDataTrack(Context context, String moduleTag, int count) {
        AppTrack appTrack = new AppTrack();
        appTrack.setModuleTag(MODULE_BASE);
        appTrack.setTrackType(9999);
        appTrack.setCurClass(AppTrackRepository.class.getSimpleName());
        appTrack.setActionName("db_exceeded_del");
        appTrack.setActionData("delete " + (TextUtils.isEmpty(moduleTag) ? "" : moduleTag + " ")
                + count + " tracks for db exceeded");
        appTrack.setActionInStamp(System.currentTimeMillis());
        appTrack.setActionOutStamp(System.currentTimeMillis());
        AppTrackUtils.setBaseInfoAndIp(context, appTrack);
        return appTrack;
    }

    public static int getModuleMaxCount(String moduleTag) {
        switch (moduleTag) {
            case MODULE_BUSINESS_RECORD:
                return 50000;
            default:
                return 10000;
        }
    }
}