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
        TrackModuleTag.buildModuleMap(mOpenModuleMap);
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

    public void addLeftTrackImmediately() {
        mHelper.addLeftTrackImmediately();
    }

    public void release() {
        stopLoopUploadTrack();
        clearAllWaitTrackTask();
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

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime) {
        recordOperation(moduleTag, curClass, actionName, actionData, recordTime, false, false);
    }

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo) {
        recordOperation(moduleTag, curClass, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo, boolean immediately) {
        if (!canTrack(moduleTag)) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        appTrack.setCurClass(curClass);
        appTrack.setTrackType(9899);
        appTrack.setActionName(actionName);
        appTrack.setModuleTag(moduleTag);
        appTrack.setActionData(actionData);
        appTrack.setActionInStamp(recordTime);
        if (!containBaseInfo) {
            AppTrackUtils.setBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getOperationRecord(String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9899, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getOperationRecord(List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9899, moduleTagList, actionNames, pageNo, pageSize);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime) {
        recordInfoState(moduleTag, curClass, actionName, actionData, recordTime, false, false);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo) {
        recordInfoState(moduleTag, curClass, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass,
                                @NonNull String actionName, @NonNull String actionData,
                                long recordTime,
                                boolean containBaseInfo, boolean immediately) {
        if (!canTrack(moduleTag)) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        appTrack.setCurClass(curClass);
        appTrack.setTrackType(9799);
        appTrack.setActionName(actionName);
        appTrack.setModuleTag(moduleTag);
        appTrack.setActionData(actionData);
        appTrack.setActionInStamp(recordTime);
        if (!containBaseInfo) {
            AppTrackUtils.setBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getInfoRecord(String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9799, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getInfoRecord(List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9799, moduleTagList, actionNames, pageNo, pageSize);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType,
                       @NonNull String actionName, @NonNull String actionData,
                       long recordTime) {
        record(moduleTag, curClass, trackType, actionName, actionData, recordTime, false, false);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType,
                       @NonNull String actionName, @NonNull String actionData,
                       long recordTime,
                       boolean containBaseInfo) {
        record(moduleTag, curClass, trackType, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType,
                       @NonNull String actionName, @NonNull String actionData,
                       long recordTime,
                       boolean containBaseInfo, boolean immediately) {
        if (!canTrack(moduleTag)) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        appTrack.setCurClass(curClass);
        appTrack.setTrackType(trackType);
        appTrack.setActionName(actionName);
        appTrack.setModuleTag(moduleTag);
        appTrack.setActionData(actionData);
        appTrack.setActionInStamp(recordTime);
        if (!containBaseInfo) {
            AppTrackUtils.setBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getRecord(int trackType, String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(trackType, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getRecord(int trackType, List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(trackType, moduleTagList, actionNames, pageNo, pageSize);
    }

    public List<AppTrack> getTrackListByStartTime(List<String> actionNames, long startTime, int count) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        return getTrackListByStartTime(moduleTagList, actionNames, startTime, count);
    }

    public List<AppTrack> getTrackListByStartTime(List<String> moduleTagList, List<String> actionNames, long startTime, int count) {
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.queryTrackListByStartTime(moduleTagList, actionNames, startTime, count);
    }

    public List<AppTrack> getTrackListByEndTime(List<String> actionNames, long endTime, int count) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        return getTrackListByEndTime(moduleTagList, actionNames, endTime, count);
    }

    public List<AppTrack> getTrackListByEndTime(List<String> moduleTagList, List<String> actionNames, long endTime, int count) {
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.queryTrackListByEndTime(moduleTagList, actionNames, endTime, count);
    }

    public int getRecordCount(List<String> actionNames, long startTime, long endTime) {
        List<String> moduleTagList = TrackModuleTag.getLogModuleList();
        return getRecordCount(moduleTagList, actionNames, startTime, endTime);
    }

    public int getRecordCount(List<String> moduleTagList, List<String> actionNames, long startTime, long endTime) {
        if (!canTrack(moduleTagList)) {
            return -1;
        }
        return mHelper.getTrackCount(moduleTagList, actionNames, startTime, endTime);
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

    private boolean canTrack(@NonNull List<String> trackModules) {
        if (!isInit()) {
            return false;
        }
        if (!mEnable) {
            return false;
        }
        synchronized (mOpenModuleMap) {
            for (String trackModule : trackModules) {
                if (!mOpenModuleMap.containsKey(trackModule)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInit() {
        return mIsInit;
    }
}
