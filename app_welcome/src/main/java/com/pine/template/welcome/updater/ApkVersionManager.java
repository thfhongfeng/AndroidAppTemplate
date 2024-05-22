package com.pine.template.welcome.updater;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pine.template.base.BaseKeyConstants;
import com.pine.template.base.R;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.ProgressDialog;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.DownloadRequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.RequestMethod;
import com.pine.tool.request.callback.DownloadCallback;
import com.pine.tool.util.InstallUtil;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void checkAndUpdateApk(final Activity activity,
                                  boolean silentUpdate, final IUpdateCallback callback) {
        checkAndUpdateApk(activity, false, silentUpdate, callback);
    }

    public void checkAndUpdateApk(final Activity activity, boolean fullScreen,
                                  boolean silentUpdate, final IUpdateCallback callback) {
        mFullScreen = fullScreen;
        mSilentUpdate = silentUpdate;
        HashMap<String, String> params = new HashMap<>();
        mVersionModel.requestUpdateVersionData(params, new IModelAsyncResponse<VersionEntity>() {
            @Override
            public void onResponse(VersionEntity versionEntity) {
                if (versionEntity != null) {
                    if (versionEntity.isNewVersion()) {
                        if (callback != null) {
                            callback.onNewVersionFound(versionEntity);
                        }
                        showVersionUpdateConfirmDialog(activity, versionEntity, callback);
                    } else {
                        if (callback != null) {
                            callback.onNoNewVersion();
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onNoNewVersion();
                    }
                }
            }

            @Override
            public boolean onFail(Exception e) {
                if (callback != null) {
                    callback.onNoNewVersion();
                }
                return true;
            }

            @Override
            public void onCancel() {
                if (callback != null) {
                    callback.onNoNewVersion();
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
        mUpdateConfirmDialog.setContentView(R.layout.base_dialog_version_update_confirm);
        mUpdateConfirmDialog.setCanceledOnTouchOutside(false);
        mUpdateConfirmDialog.setCancelable(false);
        mUpdateConfirmDialog.setOwnerActivity(activity);
        ((TextView) mUpdateConfirmDialog.findViewById(R.id.reason_tv))
                .setText(String.format(activity.getString(R.string.base_new_version_available),
                        versionEntity.getVersionName()));
        mUpdateConfirmDialog.findViewById(R.id.cancel_btn_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateConfirmDialog.dismiss();
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
        int timeStamp = versionEntity.isForce() ? 3000 : 300000;
        mCountDownTimer = new CountDownTimer(timeStamp, 1000) {

            @Override
            public void onFinish() {
                if (mUpdateConfirmDialog != null && mUpdateConfirmDialog.isShowing()) {
                    if (versionEntity.isForce()) {
                        mUpdateConfirmDialog.findViewById(R.id.confirm_ll).performClick();
                    } else {
                        mUpdateConfirmDialog.findViewById(R.id.cancel_btn_tv).performClick();
                    }
                    mUpdateConfirmDialog.findViewById(R.id.count_time_tv).setVisibility(View.GONE);
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (mUpdateConfirmDialog != null && mUpdateConfirmDialog.isShowing()) {
                    ((TextView) mUpdateConfirmDialog.findViewById(R.id.count_time_tv))
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
            if (callback != null) {
                callback.onUpdateErr(1,
                        activity.getString(R.string.base_version_get_download_path_fail, mDownloadDir),
                        versionEntity);
            }
            return;
        }
        String fileName = versionEntity.getFileName();
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
                if (callback != null) {
                    callback.onUpdateErr(0, "", versionEntity);
                }
            }

            @Override
            public boolean onError(int what, Exception e) {
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
        void onNoNewVersion();

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
