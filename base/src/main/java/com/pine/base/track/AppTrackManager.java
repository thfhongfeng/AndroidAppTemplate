package com.pine.base.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.base.BaseSPKeyConstants;
import com.pine.base.db.entity.AppTrack;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppTrackManager {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackManager mInstance;
    private TrackHelper mHelper;
    private boolean mIsInit;
    // 总开关
    private boolean mEnable = false;
    // 模块开关
    private Map<String, Boolean> mOpenModuleMap = new HashMap<>();

    private AppTrackManager() {
        mOpenModuleMap.put(TrackModuleTag.MODULE_DEFAULT, true);
    }

    public synchronized static AppTrackManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppTrackManager();
        }
        return mInstance;
    }

    public void init(@NonNull Context application, @NonNull String uploadUrl) {
        LogUtils.d(TAG, "init uploadUrl :" + uploadUrl);
        mEnable = true;
        mHelper = new TrackHelper(application, uploadUrl, mOpenModuleMap);
        mIsInit = true;
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

    /**
     * @param trackModuleTag see {@link TrackModuleTag}
     * @param appTrack
     */
    public void track(@NonNull String trackModuleTag, @NonNull AppTrack appTrack) {
        if (!canTrack(trackModuleTag)) {
            return;
        }
        appTrack.setModuleTag(trackModuleTag);
        mHelper.track(appTrack);
    }

    /**
     * @param trackModuleTag
     * @param curClass
     * @param preClass
     * @param title
     * @param buttonName
     */
    public void trackButton(@NonNull String trackModuleTag, @NonNull String curClass, @NonNull String preClass,
                            @NonNull String title, @NonNull String buttonName) {
        if (!canTrack(trackModuleTag)) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        setUserInfoAndIp(appTrack);
        appTrack.setModuleTag(trackModuleTag);
        appTrack.setTrackType(0);
        appTrack.setCurClass(curClass);
        appTrack.setPreClass(preClass);
        appTrack.setTitle(title);
        appTrack.setButtonName(buttonName);
        long timeStamp = Calendar.getInstance().getTimeInMillis();
        appTrack.setTimeInStamp(timeStamp);
        appTrack.setTimeOutStamp(timeStamp);
        mHelper.track(appTrack);
    }

    /**
     * @param trackModuleTag
     * @param curClass
     * @param preClass
     * @param title
     * @param startTimeStamp
     * @param endTimeStamp
     */
    public void trackPageUi(@NonNull String trackModuleTag, @NonNull String curClass, @NonNull String preClass,
                            @NonNull String title, long startTimeStamp, long endTimeStamp) {
        if (!canTrack(trackModuleTag) || startTimeStamp < 0 || endTimeStamp < 0 || startTimeStamp > endTimeStamp) {
            return;
        }
        AppTrack appTrack = new AppTrack();
        setUserInfoAndIp(appTrack);
        appTrack.setModuleTag(trackModuleTag);
        appTrack.setTrackType(1);
        appTrack.setCurClass(curClass);
        appTrack.setPreClass(preClass);
        appTrack.setTitle(title);
        appTrack.setButtonName("");
        appTrack.setTimeInStamp(startTimeStamp);
        appTrack.setTimeOutStamp(endTimeStamp);
        mHelper.track(appTrack);
    }

    private void setUserInfoAndIp(AppTrack appTrack) {
        String accountId = SharePreferenceUtils.readStringFromAppLivedCache(BaseSPKeyConstants.ACCOUNT_ID, "");
        String name = SharePreferenceUtils.readStringFromAppLivedCache(BaseSPKeyConstants.ACCOUNT_NAME, "");
        int accountType = SharePreferenceUtils.readIntFromAppLivedCache(BaseSPKeyConstants.ACCOUNT_TYPE, 0);
        appTrack.setAccountId(TextUtils.isEmpty(accountId) ? "" : accountId);
        appTrack.setUserName(TextUtils.isEmpty(name) ? "" : name);
        appTrack.setAccountType(accountType);
        String ip = NetWorkUtils.getIpAddress();
        appTrack.setIp(TextUtils.isEmpty(ip) ? "" : ip);
    }

    /**
     * @param trackModuleTagList list for TrackModuleTag, see {@link TrackModuleTag}, null for all
     * @param startTimeStamp     include
     * @param endTimeStamp       exclude
     */
    public void uploadTrack(List<String> trackModuleTagList, long startTimeStamp, long endTimeStamp) {
        if (!isInit()) {
            return;
        }
        mHelper.uploadTrack(trackModuleTagList, startTimeStamp, endTimeStamp);
    }

    /**
     * @param trackModuleTagList list for TrackModuleTag, see {@link TrackModuleTag}, null for all
     */
    public void uploadAllExistTrack(List<String> trackModuleTagList) {
        if (!isInit()) {
            return;
        }
        mHelper.uploadTrack(trackModuleTagList, -1, -1);
    }

    private boolean canTrack(@NonNull String trackModule) {
        if (!isInit()) {
            return false;
        }
        synchronized (mOpenModuleMap) {
            return mEnable && mOpenModuleMap.containsKey(trackModule) && mOpenModuleMap.get(trackModule);
        }
    }

    private boolean isInit() {
        return mIsInit;
    }
}
