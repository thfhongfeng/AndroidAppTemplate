package com.pine.template.base.business.track;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.business.track.entity.AppTracksHeader;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;

public class DefaultAppTrackAdapter implements IAppTrackAdapter {
    public void setupBaseInfoAndIp(@NonNull Context context, AppTrack appTrack) {

    }

    public AppTracksHeader getTrackHeader(Context context) {
        AppTracksHeader header = new AppTracksHeader();
        return header;
    }

    public int getMaxStoreCount() {
        return ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_APP_TRACK_MAX_COUNT, 100000);
    }

    public int getModuleMaxCount(String moduleTag) {
        switch (moduleTag) {
            case TrackDefaultBuilder.MODULE_BUSINESS_RECORD:
                return 90000;
            case TrackDefaultBuilder.MODULE_STATE_INFO:
                return 80000;
            default:
                return 50000;
        }
    }
}
