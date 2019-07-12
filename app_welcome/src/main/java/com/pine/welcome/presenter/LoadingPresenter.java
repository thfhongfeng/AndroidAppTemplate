package com.pine.welcome.presenter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.pine.base.widget.dialog.ProgressDialog;
import com.pine.config.ConfigBundleKey;
import com.pine.config.switcher.ConfigBundleSwitcher;
import com.pine.router.IRouterCallback;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.R;
import com.pine.welcome.WelcomeApplication;
import com.pine.welcome.bean.BundleSwitcherEntity;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.contract.ILoadingContract;
import com.pine.welcome.manager.ApkVersionManager;
import com.pine.welcome.model.BundleSwitcherModel;
import com.pine.welcome.model.VersionModel;
import com.pine.welcome.remote.WelcomeClientManager;
import com.pine.welcome.ui.activity.WelcomeActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class LoadingPresenter extends Presenter<ILoadingContract.Ui> implements ILoadingContract.Presenter {
    private final static long LOADING_MAX_TIME = 2000;
    private BundleSwitcherModel mBundleSwitcherModel;
    private VersionModel mVersionModel;
    private long mStartTimeMillis;

    public LoadingPresenter() {
        mBundleSwitcherModel = new BundleSwitcherModel();
        mVersionModel = new VersionModel();
    }

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mStartTimeMillis = System.currentTimeMillis();
        return false;
    }

    @Override
    public void loadBundleSwitcherData() {
        mBundleSwitcherModel.requestBundleSwitcherData(new IModelAsyncResponse<ArrayList<BundleSwitcherEntity>>() {
            @Override
            public void onResponse(ArrayList<BundleSwitcherEntity> bundleSwitcherEntities) {
                if (bundleSwitcherEntities != null) {
                    for (int i = 0; i < bundleSwitcherEntities.size(); i++) {
                        ConfigBundleSwitcher.setBundleState(bundleSwitcherEntities.get(i).getConfigKey(),
                                bundleSwitcherEntities.get(i).isOpen());
                    }
                }
                if (isUiAlive()) {
                    checkVersion();
                }
                return;
            }

            @Override
            public boolean onFail(Exception e) {
                if (isUiAlive()) {
                    checkVersion();
                }
                return true;
            }

            @Override
            public void onCancel() {
                if (isUiAlive()) {
                    checkVersion();
                }
            }
        });
    }

    @Override
    public void updateVersion(final boolean isForce) {
        ApkVersionManager.getInstance().startUpdate(new ApkVersionManager.UpdateListener() {

            @Override
            public void onDownloadStart(boolean isResume, long rangeSize, long allCount) {
                LogUtils.d(TAG, "onDownloadStart isResume:" + isResume +
                        ", rangeSize:" + rangeSize + ", allCount:" + allCount);
                if (isUiAlive()) {
                    showVersionUpdateProgressDialog(isForce);
                }
            }

            @Override
            public void onDownloadProgress(int progress, long fileCount, long speed) {
                if (isUiAlive()) {
                    getUi().updateVersionUpdateProgressDialog(progress);
                }
            }

            @Override
            public void onDownloadComplete(String filePath) {
                LogUtils.d(TAG, "onDownloadComplete filePath:" + filePath);
                if (isUiAlive()) {
                    getUi().dismissVersionUpdateProgressDialog();
                    installNewVersionApk();
                }
            }

            @Override
            public void onDownloadCancel() {
                LogUtils.d(TAG, "onDownloadCancel");
                if (isUiAlive()) {
                    showShortToast(R.string.wel_new_version_update_cancel);
                    getUi().dismissVersionUpdateProgressDialog();
                    if (isForce) {
                        getActivity().finish();
                    } else {
                        autoLogin(1000);
                    }
                }
            }

            @Override
            public void onDownloadError(Exception exception) {
                LogUtils.d(TAG, "onDownloadError onDownloadError:" + exception);
                if (isUiAlive()) {
                    String msg = "";
                    if (exception instanceof BusinessException) {
                        msg = getContext().getString(R.string.wel_new_version_download_fail) +
                                "(" + exception.getMessage() + ")";
                    } else {
                        msg = getContext().getString(R.string.wel_new_version_download_fail);
                    }
                    showShortToast(msg);
                    getUi().dismissVersionUpdateProgressDialog();
                    if (isForce) {
                        getActivity().finish();
                    } else {
                        autoLogin(1000);
                    }
                }
            }
        });
    }

    @Override
    public void autoLogin(final int delayTogoWelcome) {
        if (!ConfigBundleSwitcher.isBundleOpen(ConfigBundleKey.LOGIN_BUNDLE_KEY) ||
                WelcomeApplication.isLogin()) {
            if (isUiAlive()) {
                goWelcomeActivity(delayTogoWelcome);
            }
            return;
        }
        WelcomeClientManager.autoLogin(getContext(), null, new IRouterCallback() {
            @Override
            public void onSuccess(Bundle responseBundle) {
                if (isUiAlive()) {
                    goWelcomeActivity(delayTogoWelcome);
                }
            }

            @Override
            public boolean onFail(int failCode, String errorInfo) {
                if (isUiAlive()) {
                    goWelcomeActivity(delayTogoWelcome);
                }
                return true;
            }
        });
    }

    private void goWelcomeActivity(int delayTogoWelcome) {
        long delay = delayTogoWelcome;
        if (delayTogoWelcome <= 0) {
            delay = LOADING_MAX_TIME - (System.currentTimeMillis() - mStartTimeMillis);
            delay = delay > 0 ? delay : 0;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUiAlive()) {
                    Intent intent = new Intent(getContext(), WelcomeActivity.class);
                    getContext().startActivity(intent);
                    finishUi();
                }
                return;
            }
        }, delay);
    }

    private void checkVersion() {
        mVersionModel.requestUpdateVersionData(new IModelAsyncResponse<VersionEntity>() {
            @Override
            public void onResponse(VersionEntity versionEntity) {
                if (isUiAlive() && versionEntity != null) {
                    ApkVersionManager.getInstance().setVersionEntity(versionEntity);
                    try {
                        PackageInfo packageInfo = getContext().getPackageManager()
                                .getPackageInfo(getContext().getPackageName(), 0);
                        if (packageInfo.versionCode < versionEntity.getVersionCode()) {
                            if (versionEntity.isForce()) {
                                updateVersion(true);
                            } else {
                                getUi().showVersionUpdateConfirmDialog(versionEntity.getVersionName());
                            }
                        } else {
                            autoLogin(-1);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        autoLogin(-1);
                    }
                }
            }

            @Override
            public boolean onFail(Exception e) {
                if (isUiAlive()) {
                    autoLogin(-1);
                }
                return false;
            }

            @Override
            public void onCancel() {
                if (isUiAlive()) {
                    autoLogin(-1);
                }
            }
        });
    }

    private void showVersionUpdateProgressDialog(boolean isForce) {
        getUi().showVersionUpdateProgressDialog(isForce ? null :
                new ProgressDialog.IDialogActionListener() {
                    @Override
                    public void onCancel() {
                        RequestManager.cancelBySign(ApkVersionManager.getInstance().CANCEL_SIGN);
                    }
                });
    }

    private void installNewVersionApk() {
        if (!isUiAlive()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = ApkVersionManager.getInstance().getDownLoadFile();
        if (file != null && file.exists()) {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            finishUi();
        } else {
            showShortToast(R.string.wel_new_version_install_fail);
            autoLogin(1000);
        }
    }
}
