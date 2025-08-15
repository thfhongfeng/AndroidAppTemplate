package com.pine.template.base.business.track;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.business.track.entity.AppTracksHeader;

public interface IAppTrackAdapter {

    void setupBaseInfoAndIp(@NonNull Context context, AppTrack appTrack);

    AppTracksHeader getTrackHeader(Context context);

    int getMaxStoreCount();

    int getModuleMaxCount(String moduleTag);
}
