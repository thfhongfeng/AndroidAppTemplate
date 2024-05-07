package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppTrackManager {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackManager mInstance;
    private Context mContext;
    private TrackHelper mHelper;
    private boolean mIsInit;
    // 总开关
    private boolean mEnable = false;
    // 模块开关(value表示是否上传该模块的本地记录)
    private Map<String, Boolean> mOpenModuleMap = new HashMap<>();

    private AppTrackManager() {
        mOpenModuleMap.put(TrackModuleTag.MODULE_DEFAULT, true);
        mOpenModuleMap.put(TrackModuleTag.MODULE_BASE, true);

        mOpenModuleMap.put(TrackModuleTag.MODULE_OPERATION_RECORD, false);
    }

    public synchronized static AppTrackManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppTrackManager();
        }
        return mInstance;
    }

    public void init(@NonNull Context application, @NonNull String uploadUrl) {
        mContext = application;
        mEnable = ConfigSwitcherServer.isEnable(BuildConfigKey.FUN_APP_TRACK)
                && ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_APP_TRACK);
        mHelper = new TrackHelper(application, uploadUrl, mOpenModuleMap);
        mIsInit = true;

        LogUtils.d(TAG, "init uploadUrl :" + uploadUrl + ", enable app track:" + mEnable);

        uploadAllExistTrack();
        doSetupJob();
    }

    private void doSetupJob() {
        if (!enableTrack()) {
            return;
        }
        int interval = ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_APP_TRACK_LOOP_INTERVAL);
        if (interval > 0) {
            startLoopUploadTrack(interval * 1000);
        }
    }

    public void enableAppTrack(boolean enable) {
        boolean change = mEnable != enable;
        mEnable = enable;
        if (change) {
            doSetupJob();
        }
    }

    /**
     * @param trackModule see {@link TrackModuleTag}
     */
    public void openModuleTrack(@NonNull String trackModule) {
        synchronized (mOpenModuleMap) {
            mOpenModuleMap.put(trackModule, true);
        }
    }

    /**
     * @param trackModule see {@link TrackModuleTag}
     */
    public void closeModuleTrack(@NonNull String trackModule) {
        synchronized (mOpenModuleMap) {
            mOpenModuleMap.put(trackModule, false);
        }
    }

    public void track(@NonNull Context context, @NonNull String trackModuleTag,
                      @NonNull AppTrack appTrack) {
        track(context, trackModuleTag, appTrack, false);
    }

    /**
     * @param context
     * @param trackModuleTag  see {@link TrackModuleTag}
     * @param appTrack
     * @param containBaseInfo
     */
    public void track(@NonNull Context context, @NonNull String trackModuleTag,
                      @NonNull AppTrack appTrack, boolean containBaseInfo) {
        track(context, trackModuleTag, appTrack, containBaseInfo, false);
    }

    public void track(@NonNull Context context, @NonNull String trackModuleTag,
                      @NonNull AppTrack appTrack, boolean containBaseInfo, boolean immediately) {
        if (!canTrack(trackModuleTag)) {
            return;
        }
        appTrack.setModuleTag(trackModuleTag);
        if (TextUtils.isEmpty(appTrack.getCurClass())) {
            appTrack.setCurClass("DefaultTrackClass");
        }
        if (!containBaseInfo) {
            AppTrackUtils.setBaseInfoAndIp(context, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public void recordOperation(@NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime) {
        recordOperation(curClass, actionName, actionData, recordTime, false, false);
    }

    public void recordOperation(@NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo) {
        recordOperation(curClass, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void recordOperation(@NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo, boolean immediately) {
        if (!canTrack(TrackModuleTag.MODULE_OPERATION_RECORD)) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        appTrack.setCurClass(curClass);
        appTrack.setTrackType(9899);
        appTrack.setActionName(actionName);
        appTrack.setModuleTag(TrackModuleTag.MODULE_OPERATION_RECORD);
        appTrack.setActionData(actionData);
        appTrack.setActionInStamp(recordTime);
        if (!containBaseInfo) {
            AppTrackUtils.setBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getOperationRecord(String actionName, int pageNo, int pageSize) {
        if (!canTrack(TrackModuleTag.MODULE_OPERATION_RECORD)) {
            return null;
        }
        return mHelper.getTrackList(TrackModuleTag.MODULE_OPERATION_RECORD, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getOperationRecord(List<String> actionNames, int pageNo, int pageSize) {
        if (!canTrack(TrackModuleTag.MODULE_OPERATION_RECORD)) {
            return null;
        }
        return mHelper.getTrackList(TrackModuleTag.MODULE_OPERATION_RECORD, actionNames, pageNo, pageSize);
    }

    /**
     * @param startTimeStamp include
     * @param endTimeStamp   exclude
     */
    public void uploadTrack(long startTimeStamp, long endTimeStamp) {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.uploadTrack(startTimeStamp, endTimeStamp);
    }

    public void uploadAllExistTrack() {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.uploadTrack(-1, -1);
    }

    public void startLoopUploadTrack(long delay) {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.startLoopUploadTrack(delay);
    }

    public void stopLoopUploadTrack() {
        if (!isInit()) {
            return;
        }
        mHelper.stopLoopUploadTrack();
    }

    public void clearAllWaitTrackTask() {
        if (!isInit()) {
            return;
        }
        mHelper.clearAllWaitTrackTask();
    }

    private boolean enableTrack() {
        if (!isInit()) {
            return false;
        }
        return mEnable;
    }

    private boolean enableUploadTrack() {
        if (!isInit()) {
            return false;
        }
        return mEnable && ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_UPLOAD_APP_TRACK);
    }

    private boolean canTrack(@NonNull String trackModule) {
        if (!isInit()) {
            return false;
        }
        synchronized (mOpenModuleMap) {
            return mEnable && mOpenModuleMap.containsKey(trackModule);
        }
    }

    private boolean isInit() {
        return mIsInit;
    }
}
