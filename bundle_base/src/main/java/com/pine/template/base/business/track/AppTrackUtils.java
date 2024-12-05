package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.DeviceConfig;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.business.track.entity.AppTracksHeader;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.remote.BaseRouterClient;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.NetWorkUtils;

public class AppTrackUtils {
    public static void setBaseInfoAndIp(@NonNull Context context, AppTrack appTrack) {
        AccountBean accountBean = BaseRouterClient.getLoginAccount(context, null);
        String accountId = "";
        String name = "";
        int accountType = 0;
        if (accountBean != null) {
            accountId = accountBean.getId();
            name = accountBean.getName();
            accountType = accountBean.getAccountType();
        }
        appTrack.setAccountId(accountId);
        appTrack.setUserName(name);
        appTrack.setAccountType(accountType);

        appTrack.setVersionCode(AppUtils.getVersionCode());
        appTrack.setVersionName(AppUtils.getVersionName());

        String ip = NetWorkUtils.getIpAddress();
        appTrack.setIp(TextUtils.isEmpty(ip) ? "" : ip);
    }

    public static AppTracksHeader getTrackHeader(Context context) {
        AppTracksHeader header = new AppTracksHeader();
        header.setDeviceId(DeviceConfig.getDeviceUniqueNum(context));
        header.setDeviceModel(AppUtils.getDeviceModel());
        header.setPkgName(context.getPackageName());
        return header;
    }

    public static int getMaxStoreCount() {
        return ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_APP_TRACK_MAX_COUNT, 100000);
    }

    public static int getModuleMaxCount(String moduleTag) {
        switch (moduleTag) {
            case TrackModuleTag.MODULE_BUSINESS_RECORD:
                return 50000;
            case TrackModuleTag.MODULE_STATE_INFO:
                return 20000;
            default:
                return 10000;
        }
    }
}
