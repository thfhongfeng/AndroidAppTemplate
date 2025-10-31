package com.pine.template.welcome.updater;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.template.base.BaseApplication;
import com.pine.template.base.BaseKeyConstants;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.ProgressDialog;
import com.pine.template.welcome.R;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.DownloadRequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.RequestMethod;
import com.pine.tool.request.callback.DownloadCallback;
import com.pine.tool.request.response.IAsyncResponse;
import com.pine.tool.util.InstallUtil;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tanghongfeng on 2018/9/25
 */

public class ApkVersionManager {
    private final static String TAG = LogUtils.makeLogTag(ApkVersionManager.class);

    private final static int HTTP_REQUEST_DOWNLOAD = 1;
    private static volatile ApkVersionManager mInstance;
    public final Object CANCEL_SIGN = new Object();
    private String mDownloadDir = PathUtils.getExternalAppCachePath() + "/updateApk";
    private VersionModel mVersionModel;

    private CountDownTimer mCountDownTimer;
    private Dialog mUpdateConfirmDialog;
    private ProgressDialog mUpdateProgressDialog;

    private boolean mFullScreen = false;
    private boolean mSilentUpdate = true;

    private ApkVersionManager() {
        mVersionModel = new VersionModel();
    }

    public static ApkVersionManager getInstance() {
        if (mInstance == null) {
            synchronized (ApkVersionManager.class) {
                if (mInstance == null) {
                    mInstance = new ApkVersionManager();
                }
            }
        }
        return mInstance;
    }

    public void onClear() {
        if (mUpdateConfirmDialog != null && mUpdateConfirmDialog.isShowing()) {
            mUpdateConfirmDialog.dismiss();
            mUpdateConfirmDialog = null;
        }
        if (mUpdateProgressDialog != null && mUpdateProgressDialog.isShowing()) {
            mUpdateProgressDialog.dismiss();
            mUpdateProgressDialog = null;
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private VersionEntity mLastNewVersionEntity;
    private long mLastCheckUpdate;

    private long BG_IDLE_CHECK_INTERVAL = 3 * 60 * 60 * 1000;

    private MessageQueue.IdleHandler mBgCheckUpdater = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            long now = System.currentTimeMillis();
            if (now - mLastCheckUpdate > BG_IDLE_CHECK_INTERVAL
                    && BaseApplication.mCurResumedActivity != null) {
                boolean keep = false;
                long lastCheckUpdateCalTime = mLastCheckUpdate;
                if (mLastNewVersionEntity != null && mLastNewVersionEntity.isForce()) {
                    // 如果是强制更新，则过三十分钟后还要进行更新检查
                    keep = true;
                    lastCheckUpdateCalTime = mLastCheckUpdate + 30 * 60 * 1000;
                }
                checkAndUpdateApk(BaseApplication.mCurResumedActivity, false, true, true, true, null);
                if (mLastNewVersionEntity != null && mLastNewVersionEntity.isForce()) {
                    mLastCheckUpdate = lastCheckUpdateCalTime;
                }
                LogUtils.d(TAG, "idle bg check update work active now:" + now
                        + ", lastCheckUpdateCalTime:" + lastCheckUpdateCalTime
                        + ", BG_IDLE_CHECK_INTERVAL:" + BG_IDLE_CHECK_INTERVAL + ", keep idle work:" + keep
                        + ", cur activity:" + BaseApplication.mCurResumedActivity);
                return keep;
            }
            return true;
        }
    };

    public void scheduleBgUpdateCheckIfNeed() {
        boolean autoCheck = ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_AUTO_CHECK_UPDATE);
        LogUtils.d(TAG, "scheduleBgUpdateCheckIfNeed autoCheck:" + autoCheck
                + ", mLastNewVersionEntity:" + mLastNewVersionEntity);
        if (mLastNewVersionEntity != null && autoCheck) {
            Random rand = new Random();
            int num = rand.nextInt(500); // [0, 500)的整数
            BG_IDLE_CHECK_INTERVAL = 60 * 60 * 1000 + num * 60 * 1000;
            LogUtils.d(TAG, "scheduleBgUpdateCheck BG_IDLE_CHECK_INTERVAL:" + BG_IDLE_CHECK_INTERVAL);
            // idle队列中进行
            Looper.getMainLooper().getQueue().removeIdleHandler(mBgCheckUpdater);
            Looper.getMainLooper().getQueue().addIdleHandler(mBgCheckUpdater);
        }
    }

    public void checkAndUpdateApk(final Activity activity, boolean manualCheckUpdate, boolean fullScreen,
                                  boolean silentUpdate, final IUpdateCallback callback) {
        checkAndUpdateApk(activity, manualCheckUpdate, false, fullScreen, silentUpdate, callback);
    }

    public void checkAndUpdateApk(final Activity activity, boolean manualCheckUpdate, boolean bgIdleCheckMode, boolean fullScreen,
                                  boolean silentUpdate, final IUpdateCallback callback) {
        mLastCheckUpdate = System.currentTimeMillis();
        mFullScreen = fullScreen;
        mSilentUpdate = silentUpdate;
        HashMap<String, String> params = new HashMap<>();
        params.put("manualCheckUpdate", String.valueOf(manualCheckUpdate));
        mVersionModel.requestUpdateVersionData(params, new IAsyncResponse<VersionEntity>() {
            @Override
            public void onResponse(VersionEntity versionEntity) {
                LogUtils.d(TAG, "checkAndUpdateApk bgIdleCheckMode:" + bgIdleCheckMode
                        + ", manualCheckUpdate:" + manualCheckUpdate + ", versionEntity:" + versionEntity);
                versionEntity.setBgIdleCheck(bgIdleCheckMode);
                mLastNewVersionEntity = null;
                if (versionEntity != null) {
                    if (versionEntity.getBaseConfigInfo() != null) {
                        ConfigSwitcherServer.updateRemoteConfig(versionEntity.getBaseConfigInfo(), null);
                    }
                    if (versionEntity.isNewVersion()) {
                        if (callback != null) {
                            callback.onNewVersionFound(versionEntity);
                        }
                        mLastNewVersionEntity = versionEntity;
                        showVersionUpdateConfirmDialog(activity, versionEntity, callback);
                    } else {
                        if (callback != null) {
                            callback.onNoNewVersion(IUpdateCallback.NO_NEW_CAUSE_NO_FOUND);
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onNoNewVersion(IUpdateCallback.NO_NEW_CAUSE_NO_FOUND);
                    }
                }
            }

            @Override
            public boolean onFail(Exception e) {
                onClear();
                if (callback != null) {
                    callback.onNoNewVersion(IUpdateCallback.NO_NEW_CAUSE_REQUEST_FAIL);
                }
                return true;
            }

            @Override
            public void onCancel() {
                onClear();
                if (callback != null) {
                    callback.onNoNewVersion(IUpdateCallback.NO_NEW_CAUSE_REQUEST_CANCEL);
                }
            }
        });
    }

    private void showVersionUpdateConfirmDialog(final Activity activity,
                                                final @NonNull VersionEntity versionEntity,
                                                final IUpdateCallback callback) {
        if (activity.isDestroyed()) {
            return;
        }
        if (mUpdateConfirmDialog != null && mUpdateConfirmDialog.isShowing()) {
            mUpdateProgressDialog.dismiss();
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mUpdateConfirmDialog = new Dialog(activity);
        mUpdateConfirmDialog.setContentView(R.layout.wel_dialog_version_update_confirm);
        mUpdateConfirmDialog.setCanceledOnTouchOutside(false);
        mUpdateConfirmDialog.setCancelable(false);
        mUpdateConfirmDialog.setOwnerActivity(activity);
        ((TextView) mUpdateConfirmDialog.findViewById(R.id.reason_tv))
                .setText(String.format(activity.getString(R.string.base_new_version_available),
                        versionEntity.getVersionName()));
        mUpdateConfirmDialog.findViewById(R.id.cancel_btn_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
                if (callback != null) {
                    callback.onUpdateErr(0, "", versionEntity);
                }
            }
        });
        mUpdateConfirmDialog.findViewById(R.id.confirm_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateConfirmDialog.dismiss();
                startUpdate(activity, versionEntity, callback);
            }
        });
        if (mFullScreen) {
            fullScreenShowDialog(mUpdateConfirmDialog);
        } else {
            mUpdateConfirmDialog.show();
        }
        int timeStamp = 120000;
        if (versionEntity.isForce()) {
            timeStamp = 6000;
        } else if (versionEntity.isBgIdleCheck()) {
            timeStamp = 30000;
        }
        mCountDownTimer = new CountDownTimer(timeStamp, 1000) {

            @Override
            public void onFinish() {
                if (mUpdateConfirmDialog != null && mUpdateConfirmDialog.isShowing()) {
                    if (versionEntity.isForce() || versionEntity.isBgIdleCheck()) {
                        mUpdateConfirmDialog.findViewById(R.id.confirm_ll).performClick();
                        mUpdateConfirmDialog.findViewById(R.id.confirm_count_time_tv).setVisibility(View.VISIBLE);
                        mUpdateConfirmDialog.findViewById(R.id.cancel_count_time_tv).setVisibility(View.GONE);
                    } else {
                        mUpdateConfirmDialog.findViewById(R.id.cancel_ll).performClick();
                        mUpdateConfirmDialog.findViewById(R.id.confirm_count_time_tv).setVisibility(View.GONE);
                        mUpdateConfirmDialog.findViewById(R.id.cancel_count_time_tv).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (versionEntity.isForce() || versionEntity.isBgIdleCheck()) {
                    ((TextView) mUpdateConfirmDialog.findViewById(R.id.confirm_count_time_tv))
                            .setText("(" + millisUntilFinished / 1000 + ")");
                } else {
                    ((TextView) mUpdateConfirmDialog.findViewById(R.id.cancel_count_time_tv))
                            .setText("(" + millisUntilFinished / 1000 + ")");
                }
            }
        };
        mCountDownTimer.start();
    }

    private void fullScreenShowDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void showVersionUpdateProgressDialog(final Activity activity,
                                                 final @NonNull VersionEntity versionEntity,
                                                 final IUpdateCallback callback) {
        if (activity.isDestroyed()) {
            return;
        }
        if (mUpdateProgressDialog != null && mUpdateProgressDialog.isShowing()) {
            mUpdateProgressDialog.dismiss();
        }
        mUpdateProgressDialog = DialogUtils.createDownloadProgressDialog(activity,
                0, new ProgressDialog.IDialogActionListener() {
                    @Override
                    public void onCancel() {
                        RequestManager.cancelBySign(ApkVersionManager.getInstance().CANCEL_SIGN);
                        if (callback != null) {
                            callback.onUpdateErr(0, "", versionEntity);
                        }
                    }
                });
        mUpdateProgressDialog.show(mFullScreen);
    }

    private void startUpdate(final Activity activity,
                             final @NonNull VersionEntity versionEntity,
                             final IUpdateCallback callback) {
        if (activity.isDestroyed()) {
            return;
        }
        if (TextUtils.isEmpty(mDownloadDir)) {
            onClear();
            if (callback != null) {
                callback.onUpdateErr(1,
                        activity.getString(R.string.base_version_get_download_path_fail, mDownloadDir),
                        versionEntity);
            }
            return;
        }
        String fileName = versionEntity.getFileName();
        if (TextUtils.isEmpty(fileName)) {
            onClear();
            if (callback != null) {
                callback.onUpdateErr(3,
                        activity.getString(R.string.base_new_version_download_fail),
                        versionEntity);
            }
            return;
        }
        File oldFile = new File(mDownloadDir, fileName);
        if (versionEntity.isFileDownloaded(oldFile)) {
            onFileDownloadComplete(activity, versionEntity, oldFile.getPath(), callback);
            return;
        }
        deleteOldApk();
        DownloadRequestBean requestBean = new DownloadRequestBean(versionEntity.getDownloadUrl(),
                HTTP_REQUEST_DOWNLOAD, new HashMap<String, String>(), mDownloadDir, fileName);
        requestBean.setRequestMethod(RequestMethod.GET);
        requestBean.setSign(CANCEL_SIGN);
        RequestManager.setDownloadRequest(requestBean, new DownloadCallback() {
            @Override
            public void onStart(int what, boolean isResume, long rangeSize,
                                Map<String, List<String>> responseHeaders, long allCount) {
                showVersionUpdateProgressDialog(activity, versionEntity, callback);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                if (mUpdateProgressDialog != null) {
                    mUpdateProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onFinish(int what, String filePath) {
                onFileDownloadComplete(activity, versionEntity, filePath, callback);
            }

            @Override
            public void onCancel(int what) {
                onClear();
                if (callback != null) {
                    callback.onUpdateErr(0, "", versionEntity);
                }
            }

            @Override
            public boolean onError(int what, Exception e) {
                onClear();
                if (callback != null) {
                    if (e instanceof MessageException) {
                        callback.onUpdateErr(2, e.getMessage(), versionEntity);
                    } else {
                        callback.onUpdateErr(3, e.getMessage(), versionEntity);
                    }
                }
                return true;
            }
        });
    }

    private void onFileDownloadComplete(final Activity activity,
                                        final @NonNull VersionEntity versionEntity,
                                        String filePath,
                                        final IUpdateCallback callback) {
        SharePreferenceUtils.saveToConfig(BaseKeyConstants.APK_DOWNLOAD_FILE_PATH, filePath);
        boolean ret = installNewVersionApk(activity, versionEntity, callback);
        onClear();
        if (callback != null) {
            if (ret) {
                callback.onUpdateComplete(versionEntity);
            } else {
                callback.onUpdateErr(3, "", versionEntity);
            }
        }
    }

    private boolean installNewVersionApk(Activity activity,
                                         VersionEntity versionEntity, IUpdateCallback callback) {
        File file = ApkVersionManager.getInstance().getDownLoadFile();
        if (file != null && file.exists()) {
            if (callback != null && callback.installApk(versionEntity, file)) {
                return true;
            }
            return InstallUtil.installApk(activity, file.getPath(),
                    versionEntity.isForce() || mSilentUpdate);
        } else {
            return false;
        }
    }

    private void deleteOldApk() {
        File folder = new File(mDownloadDir);
        if (!folder.exists()) {
            return;
        } else {
            folder.delete();
        }
    }

    private String getDownLoadFilePath() {
        return SharePreferenceUtils.readStringFromConfig(BaseKeyConstants.APK_DOWNLOAD_FILE_PATH, "");
    }

    private File getDownLoadFile() {
        String apkFilePath = SharePreferenceUtils.readStringFromConfig(BaseKeyConstants.APK_DOWNLOAD_FILE_PATH, "");
        return new File(apkFilePath);
    }

    public interface IUpdateCallback {
        String NO_NEW_CAUSE_NO_FOUND = "NO_NEW_CAUSE_NO_FOUND";
        String NO_NEW_CAUSE_REQUEST_FAIL = "NO_NEW_CAUSE_REQUEST_FAIL";
        String NO_NEW_CAUSE_REQUEST_CANCEL = "NO_NEW_CAUSE_REQUEST_CANCEL";

        void onNoNewVersion(String cause);

        void onNewVersionFound(VersionEntity versionEntity);

        /**
         * 用于提供给监听者来自主安装
         *
         * @param versionEntity
         * @return false:回调者忽略，调用者使用默认安装方式；true:回调者进行了自主安装。调用者不做任何动作
         */
        boolean installApk(VersionEntity versionEntity, File apkFile);

        void onUpdateComplete(VersionEntity versionEntity);

        // state：0-取消；1-获取下载目录失败;2-下载失败;3-下载失败;4-更新安装失败
        void onUpdateErr(int errCode, String errMsg, VersionEntity versionEntity);
    }
}
