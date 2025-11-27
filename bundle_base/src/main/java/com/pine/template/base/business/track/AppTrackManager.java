package com.pine.template.base.business.track;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AppTrackManager {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackManager mInstance;
    private Context mContext;
    private TrackHelper mHelper;
    private boolean mIsInit;
    // 总开关
    private boolean mEnable = false;
    // 要进行app记录的模块信息
    private ConcurrentHashMap<String, TrackModuleInfo> mModuleInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TrackActionInfo> mTrackActionInfoMap = new ConcurrentHashMap<>();
    private List<String> mTrackActionList = new ArrayList<>();
    private List<String> mTrackModuleList;

    private AppTrackManager() {
        TrackDefaultBuilder.buildDefaultModuleMap(mModuleInfoMap, mTrackActionInfoMap);
        for (TrackActionInfo actionInfo : mTrackActionInfoMap.values()) {
            mTrackActionList.add(actionInfo.getActionName());
        }
        mTrackModuleList = TrackDefaultBuilder.buildDefaultTrackModuleList();
    }

    public synchronized static AppTrackManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppTrackManager();
        }
        return mInstance;
    }

    private IAppTrackAdapter mAppTrackAdapter = new DefaultAppTrackAdapter();

    public void init(@NonNull Context application, String uploadUrl) {
        init(application, uploadUrl, new DefaultAppTrackAdapter());
    }

    public void init(@NonNull Context application, String uploadUrl, @NonNull IAppTrackAdapter adapter) {
        mContext = application;
        mAppTrackAdapter = adapter;
        mEnable = ConfigSwitcherServer.isEnable(BuildConfigKey.FUN_APP_TRACK) && ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_APP_TRACK);
        mHelper = new TrackHelper(application, uploadUrl);
        mIsInit = true;

        LogUtils.d(TAG, "init uploadUrl :" + uploadUrl + ", enable app track:" + mEnable);
    }

    public void attachModule(TrackModuleInfo moduleInfo) {
        if (moduleInfo == null || moduleInfo.getActions() == null
                || TextUtils.isEmpty(moduleInfo.getModuleName())) {
            return;
        }
        TrackModuleInfo exist = mModuleInfoMap.get(moduleInfo.getModuleName());
        LogUtils.d(TAG, "attachModule new:" + moduleInfo);
        LogUtils.d(TAG, "attachModule already exist:" + exist);
        if (exist != null) {
            exist.setModuleDesc(moduleInfo.getModuleDesc());
            List<TrackActionInfo> existList = exist.getActions();
            if (existList == null) {
                existList = new ArrayList<>();
                exist.setActions(existList);
            }
            HashMap<String, TrackActionInfo> existMap = new HashMap<>();
            for (TrackActionInfo action : existList) {
                existMap.put(action.getActionName(), action);
            }
            List<TrackActionInfo> list = moduleInfo.getActions();
            for (TrackActionInfo action : list) {
                if (!existMap.containsKey(action.getActionName())) {
                    LogUtils.d(TAG, "attachModule for action:" + action);
                    existList.add(action);
                    mTrackActionList.add(action.getActionName());
                    mTrackActionInfoMap.put(action.getActionName(), action);
                }
            }
        } else {
            mModuleInfoMap.put(moduleInfo.getModuleName(), moduleInfo);
            for (TrackActionInfo action : moduleInfo.getActions()) {
                mTrackActionList.add(action.getActionName());
                mTrackActionInfoMap.put(action.getActionName(), action);
            }
            mTrackModuleList.add(moduleInfo.getModuleName());
        }
    }

    private Handler mScheduleStartJobH = new Handler(Looper.getMainLooper());

    public void scheduleStartJob() {
        mScheduleStartJobH.postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadAllExistTrack();
                startScheduleJob();
            }
        }, 5000);
    }

    public void doFinishJob() {
        mScheduleStartJobH.removeCallbacksAndMessages(null);
        stopLoopUploadTrack();
        clearAllWaitTrackTask();
    }

    public IAppTrackAdapter getTrackAdapter() {
        return mAppTrackAdapter;
    }

    public int getMaxStoreCount() {
        return getTrackAdapter().getMaxStoreCount();
    }

    public int getModuleMaxCount(String moduleTag) {
        return getTrackAdapter().getModuleMaxCount(moduleTag);
    }

    public void addLeftTrackImmediately() {
        mHelper.addLeftTrackImmediately();
    }

    private void startScheduleJob() {
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
            startScheduleJob();
        }
    }

    public List<String> getUploadModuleList() {
        List<String> moduleList = new ArrayList<>();
        Set<String> keys = mModuleInfoMap.keySet();
        for (String key : keys) {
            TrackModuleInfo value = mModuleInfoMap.get(key);
            if (value != null && value.isCanUpload()) {
                moduleList.add(key);
            }
        }
        return moduleList;
    }

    public List<String> getTrackModuleList() {
        List<String> list = new ArrayList<>();
        list.addAll(mTrackModuleList);
        return list;
    }

    public List<TrackModuleInfo> getAllModuleInfoList() {
        List<TrackModuleInfo> list = new ArrayList<>();
        Set<String> keys = mModuleInfoMap.keySet();
        for (String key : keys) {
            TrackModuleInfo value = mModuleInfoMap.get(key);
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    public TrackModuleInfo getModuleInfo(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            return null;
        }
        return mModuleInfoMap.get(moduleName);
    }

    public List<TrackModuleInfo> getModuleInfoList(String... moduleNames) {
        if (moduleNames == null || moduleNames.length < 1) {
            return null;
        }
        List<TrackModuleInfo> list = new ArrayList<>();
        for (String key : moduleNames) {
            TrackModuleInfo value = mModuleInfoMap.get(key);
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    public List<TrackActionInfo> getAllActionInfoList() {
        List<TrackActionInfo> list = new ArrayList<>();
        Set<String> keys = mTrackActionInfoMap.keySet();
        for (String key : keys) {
            TrackActionInfo value = mTrackActionInfoMap.get(key);
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    public List<TrackActionInfo> getActionInfoListByModule(String... moduleNames) {
        if (moduleNames == null || moduleNames.length < 1) {
            return null;
        }
        List<TrackActionInfo> list = new ArrayList<>();
        List<TrackModuleInfo> moduleInfoList = getModuleInfoList(moduleNames);
        for (TrackModuleInfo moduleInfo : moduleInfoList) {
            if (moduleInfo != null && moduleInfo.getActions() != null) {
                list.addAll(moduleInfo.getActions());
            }
        }
        return list;
    }

    public List<String> getAllActionList() {
        return mTrackActionList;
    }

    public TrackActionInfo getActionInfo(String actionName) {
        if (TextUtils.isEmpty(actionName)) {
            return null;
        }
        return mTrackActionInfoMap.get(actionName);
    }

    public List<TrackActionInfo> getActionInfoList(String... actionNames) {
        if (actionNames == null || actionNames.length < 1) {
            return null;
        }
        List<TrackActionInfo> list = new ArrayList<>();
        for (String key : actionNames) {
            TrackActionInfo value = mTrackActionInfoMap.get(key);
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    public List<String> parseActionNames(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionList();
        }
        return actionNames;
    }

    public String[] parseActionDesc(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionList();
        }
        String[] descs = new String[actionNames.size()];
        for (int i = 0; i < actionNames.size(); i++) {
            TrackActionInfo actionInfo = mTrackActionInfoMap.get(actionNames.get(i));
            if (actionInfo != null) {
                descs[i] = actionInfo.getActionDesc();
            } else {
                descs[i] = "";
            }
        }
        return descs;
    }

    public int[] parseActionPos(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionList();
        }
        int[] select = new int[actionNames.size()];
        for (int i = 0; i < actionNames.size(); i++) {
            select[i] = i;
        }
        return select;
    }

    public String parseActionDescTxt(List<String> actionNames) {
        if (actionNames == null) {
            actionNames = getAllActionList();
        }
        if (actionNames.size() < 1) {
            return "";
        }
        String txt = "";
        for (int i = 0; i < actionNames.size(); i++) {
            TrackActionInfo actionInfo = mTrackActionInfoMap.get(actionNames.get(i));
            if (actionInfo != null) {
                txt = txt + "," + actionInfo.getActionDesc();
            }
        }
        if (!TextUtils.isEmpty(txt) && txt.length() > 1) {
            txt = txt.substring(1, txt.length());
        }
        return txt;
    }

    /**
     * @param trackModule see {@link TrackDefaultBuilder}
     */
    public void openModuleTrack(@NonNull String trackModule) {
        TrackModuleInfo info = mModuleInfoMap.get(trackModule);
        if (info != null) {
            info.setCanUpload(true);
        }
    }

    /**
     * @param trackModule see {@link TrackDefaultBuilder}
     */
    public void closeModuleTrack(@NonNull String trackModule) {
        TrackModuleInfo info = mModuleInfoMap.get(trackModule);
        if (info != null) {
            info.setCanUpload(false);
        }
    }

    public void track(@NonNull Context context, @NonNull String trackModuleTag, @NonNull AppTrack appTrack) {
        track(context, trackModuleTag, appTrack, false);
    }

    /**
     * @param context
     * @param trackModuleTag  see {@link TrackDefaultBuilder}
     * @param appTrack
     * @param containBaseInfo
     */
    public void track(@NonNull Context context, @NonNull String trackModuleTag, @NonNull AppTrack appTrack, boolean containBaseInfo) {
        track(context, trackModuleTag, appTrack, containBaseInfo, false);
    }

    public void track(@NonNull Context context, @NonNull String trackModuleTag, @NonNull AppTrack appTrack, boolean containBaseInfo, boolean immediately) {
        if (!canTrack(trackModuleTag)) {
            return;
        }
        appTrack.setModuleTag(trackModuleTag);
        if (TextUtils.isEmpty(appTrack.getCurClass())) {
            appTrack.setCurClass("DefaultTrackClass");
        }
        if (!containBaseInfo) {
            mAppTrackAdapter.setupBaseInfoAndIp(context, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime) {
        recordOperation(moduleTag, curClass, actionName, actionData, recordTime, false, false);
    }

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo) {
        recordOperation(moduleTag, curClass, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void recordOperation(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo, boolean immediately) {
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
            mAppTrackAdapter.setupBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getOperationRecord(String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9899, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getOperationRecord(List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9899, moduleTagList, actionNames, pageNo, pageSize);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime) {
        recordInfoState(moduleTag, curClass, actionName, actionData, recordTime, false, false);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo) {
        recordInfoState(moduleTag, curClass, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void recordInfoState(@NonNull String moduleTag, @NonNull String curClass, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo, boolean immediately) {
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
            mAppTrackAdapter.setupBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getInfoRecord(String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9799, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getInfoRecord(List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(9799, moduleTagList, actionNames, pageNo, pageSize);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType, @NonNull String actionName, @NonNull String actionData, long recordTime) {
        record(moduleTag, curClass, trackType, actionName, actionData, recordTime, false, false);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo) {
        record(moduleTag, curClass, trackType, actionName, actionData, recordTime, containBaseInfo, false);
    }

    public void record(@NonNull String moduleTag, @NonNull String curClass, int trackType, @NonNull String actionName, @NonNull String actionData, long recordTime, boolean containBaseInfo, boolean immediately) {
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
            mAppTrackAdapter.setupBaseInfoAndIp(mContext, appTrack);
        }
        mHelper.track(appTrack, immediately);
    }

    public List<AppTrack> getRecord(int trackType, String actionName, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(trackType, moduleTagList, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getRecord(int trackType, List<String> actionNames, int pageNo, int pageSize) {
        List<String> moduleTagList = getTrackModuleList();
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.getTrackList(trackType, moduleTagList, actionNames, pageNo, pageSize);
    }

    public List<AppTrack> getTrackListByStartTime(List<String> actionNames, long startTime, int count) {
        List<String> moduleTagList = getTrackModuleList();
        return getTrackListByStartTime(moduleTagList, actionNames, startTime, count);
    }

    public List<AppTrack> getTrackListByStartTime(List<String> moduleTagList, List<String> actionNames, long startTime, int count) {
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.queryTrackListByStartTime(moduleTagList, actionNames, startTime, count);
    }

    public List<AppTrack> getTrackListByEndTime(List<String> actionNames, long endTime, int count) {
        List<String> moduleTagList = getTrackModuleList();
        return getTrackListByEndTime(moduleTagList, actionNames, endTime, count);
    }

    public List<AppTrack> getTrackListByEndTime(List<String> moduleTagList, List<String> actionNames, long endTime, int count) {
        if (!canTrack(moduleTagList)) {
            return null;
        }
        return mHelper.queryTrackListByEndTime(moduleTagList, actionNames, endTime, count);
    }

    public int getRecordCount(List<String> actionNames, long startTime, long endTime) {
        List<String> moduleTagList = getTrackModuleList();
        return getRecordCount(moduleTagList, actionNames, startTime, endTime);
    }

    public int getRecordCount(List<String> moduleTagList, List<String> actionNames, long startTime, long endTime) {
        if (!canTrack(moduleTagList)) {
            return -1;
        }
        return mHelper.getTrackCount(moduleTagList, actionNames, startTime, endTime);
    }

    public int deleteForStorageOut(int delCount, int minLeft) {
        return mHelper.deleteForStorageOut(delCount, minLeft);
    }

    /**
     * @param startTimeStamp include
     * @param endTimeStamp   exclude
     */
    public void uploadTrack(long startTimeStamp, long endTimeStamp) {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.uploadTrack(startTimeStamp, endTimeStamp, getUploadModuleList());
    }

    public void uploadAllExistTrack() {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.uploadTrack(-1, -1, getUploadModuleList());
    }

    public void startLoopUploadTrack(long delay) {
        if (!enableUploadTrack()) {
            return;
        }
        mHelper.startLoopUploadTrack(delay, getUploadModuleList());
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
        return mEnable && mModuleInfoMap.containsKey(trackModule);
    }

    private boolean canTrack(@NonNull List<String> trackModules) {
        if (!isInit() || trackModules == null) {
            return false;
        }
        if (!mEnable) {
            return false;
        }
        for (String trackModule : trackModules) {
            if (!mModuleInfoMap.containsKey(trackModule)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInit() {
        return mIsInit;
    }
}
