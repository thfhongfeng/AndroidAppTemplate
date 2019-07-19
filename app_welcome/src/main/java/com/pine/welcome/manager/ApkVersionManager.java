package com.pine.welcome.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.config.SPKeyConstants;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.callback.DownloadCallback;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.pine.tool.util.SharePreferenceUtils;
import com.pine.welcome.R;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.model.VersionModel;

import java.io.File;

/**
 * Created by tanghongfeng on 2018/9/25
 */

public class ApkVersionManager {
    private final static String TAG = LogUtils.makeLogTag(ApkVersionManager.class);
    private final static int HTTP_REQUEST_DOWNLOAD = 1;
    private static volatile ApkVersionManager mInstance;
    public final Object CANCEL_SIGN = new Object();
    private String mDownloadDir = PathUtils.getExternalPublicPath(Environment.DIRECTORY_DOWNLOADS);
    private VersionEntity mVersionEntity;
    private VersionModel mVersionModel;

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

    public void setVersionEntity(VersionEntity entity) {
        mVersionEntity = entity;
    }

    public void checkVersion(@NonNull final Context context, final ICheckCallback callback) {
        mVersionModel.requestUpdateVersionData(new IModelAsyncResponse<VersionEntity>() {
            @Override
            public void onResponse(VersionEntity versionEntity) {
                if (versionEntity != null) {
                    ApkVersionManager.getInstance().setVersionEntity(versionEntity);
                    try {
                        PackageInfo packageInfo = context.getPackageManager()
                                .getPackageInfo(context.getPackageName(), 0);
                        if (packageInfo.versionCode < versionEntity.getVersionCode()) {
                            if (callback != null) {
                                callback.onNewVersionFound(versionEntity.getForce() == 1, versionEntity);
                            }
                        } else {
                            if (callback != null) {
                                callback.onNoNewVersion();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onRequestFail();
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
                    return callback.onRequestFail();
                }
                return false;
            }

            @Override
            public void onCancel() {
                if (callback != null) {
                    callback.onRequestFail();
                }
            }
        });
    }

    public void startUpdate(final UpdateListener listener) {
        if (TextUtils.isEmpty(mDownloadDir)) {
            if (listener != null) {
                listener.onDownloadError(new BusinessException(AppUtils.getApplication()
                        .getString(R.string.wel_version_get_download_path_fail, mDownloadDir)));
            }
            return;
        }
        deleteOldApk();
        RequestManager.setDownloadRequest(mVersionEntity.getPath(), mDownloadDir,
                mVersionEntity.getFileName(), HTTP_REQUEST_DOWNLOAD, CANCEL_SIGN, new DownloadCallback() {
                    @Override
                    public void onStart(int what, boolean isResume, long rangeSize, long allCount) {
                        if (listener != null) {
                            listener.onDownloadStart(isResume, rangeSize, allCount);
                        }
                    }

                    @Override
                    public void onProgress(int what, int progress, long fileCount, long speed) {
                        if (listener != null) {
                            listener.onDownloadProgress(progress, fileCount, speed);
                        }
                    }

                    @Override
                    public void onFinish(int what, String filePath) {
                        SharePreferenceUtils.saveToConfig(SPKeyConstants.APK_DOWNLOAD_FILE_PATH, filePath);
                        if (listener != null) {
                            listener.onDownloadComplete(filePath);
                        }
                    }

                    @Override
                    public void onCancel(int what) {
                        if (listener != null) {
                            listener.onDownloadCancel();
                        }
                    }

                    @Override
                    public boolean onError(int what, Exception e) {
                        if (listener != null) {
                            listener.onDownloadError(e);
                        }
                        return true;
                    }
                });
    }

    public boolean installNewVersionApk(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = ApkVersionManager.getInstance().getDownLoadFile();
        if (file != null && file.exists()) {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    private void deleteOldApk() {
        String apkFilePath = getDownLoadFilePath();
        if (TextUtils.isEmpty(apkFilePath)) {
            return;
        }
        File folder = new File(mDownloadDir);
        if (!folder.exists()) {
            return;
        }
        File downloadedFile = new File(apkFilePath);
        if (downloadedFile != null && downloadedFile.exists() && downloadedFile.isFile()) {
            downloadedFile.delete();
        }
    }

    public String getDownLoadFilePath() {
        return SharePreferenceUtils.readStringFromConfig(SPKeyConstants.APK_DOWNLOAD_FILE_PATH, "");
    }

    public File getDownLoadFile() {
        String apkFilePath = SharePreferenceUtils.readStringFromConfig(SPKeyConstants.APK_DOWNLOAD_FILE_PATH, "");
        return new File(apkFilePath);
    }

    public interface UpdateListener {

        void onDownloadStart(boolean isResume, long rangeSize, long allCount);

        void onDownloadProgress(int progress, long fileCount, long speed);

        void onDownloadComplete(String filePath);

        void onDownloadCancel();

        void onDownloadError(Exception exception);
    }

    public interface ICheckCallback {
        void onNewVersionFound(boolean force, VersionEntity versionEntity);

        void onNoNewVersion();

        boolean onRequestFail();
    }
}
