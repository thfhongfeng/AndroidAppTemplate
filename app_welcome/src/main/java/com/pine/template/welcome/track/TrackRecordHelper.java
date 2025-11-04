package com.pine.template.welcome.track;

import android.content.Context;
import android.text.TextUtils;

import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.business.track.TrackDefaultBuilder;
import com.pine.template.welcome.R;
import com.pine.template.welcome.WelcomeApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackRecordHelper {
    private final String TAG = this.getClass().getSimpleName();

    public static TrackRecordHelper instance;

    public static synchronized TrackRecordHelper getInstance() {
        if (instance == null) {
            instance = new TrackRecordHelper();
        }
        return instance;
    }

    private final String DEFAULT_CUR_CLASS = "TrackRecordHelper";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context mContext;

    private TrackRecordHelper() {
        mContext = WelcomeApplication.mApplication;
    }

    private String mLastEnterUi;

    public void recordInfoAppEnterUi(String ui) {
        if (TextUtils.isEmpty(ui) || TextUtils.equals(mLastEnterUi, ui)) {
            return;
        }
        mLastEnterUi = ui;
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.wel_info_app_ui_enter,
                mSimpleDateFormat.format(recordDate), ui);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_UI_ENTER, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoAppExitUi(String ui) {
        if (TextUtils.isEmpty(ui)) {
            return;
        }
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.wel_info_app_ui_exit,
                mSimpleDateFormat.format(recordDate), ui);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_UI_EXIT, actionData,
                recordDate.getTime(), true);
    }

    public void recordAppUpdateCheck(String newVersion, String noNewCause) {
        Date recordDate = new Date();
        String resultMsg = "";
        if (TextUtils.isEmpty(newVersion)) {
            resultMsg = mContext.getString(R.string.wel_info_not_need_update, noNewCause);
        } else {
            resultMsg = mContext.getString(R.string.wel_info_need_update, newVersion);
        }
        String actionData = mContext.getString(R.string.wel_info_check_update,
                mSimpleDateFormat.format(recordDate), resultMsg);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_UPDATE, actionData,
                recordDate.getTime(), true);
    }

    public void recordAppUpdateSuccess(String version) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.wel_info_update_success,
                mSimpleDateFormat.format(recordDate), version);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_UPDATE, actionData,
                recordDate.getTime(), true, true);
    }

    public void recordAppUpdateFail(String errMsg) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.wel_info_update_fail,
                mSimpleDateFormat.format(recordDate), errMsg);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.APP_UPDATE, actionData,
                recordDate.getTime(), true);
    }
}
