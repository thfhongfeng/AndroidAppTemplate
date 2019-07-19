package com.pine.welcome.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pine.base.widget.dialog.ProgressDialog;
import com.pine.config.ConfigKey;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.router.IRouterCallback;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.R;
import com.pine.welcome.WelcomeApplication;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.contract.ILoadingContract;
import com.pine.welcome.manager.ApkVersionManager;
import com.pine.welcome.remote.WelcomeClientManager;
import com.pine.welcome.ui.activity.WelcomeActivity;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class LoadingPresenter extends Presenter<ILoadingContract.Ui> implements ILoadingContract.Presenter {
    private final static long LOADING_MAX_TIME = 2000;
    private long mStartTimeMillis;

    public LoadingPresenter() {
    }

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mStartTimeMillis = System.currentTimeMillis();
        return false;
    }

    @Override
    public void setupConfigSwitcher() {
        ConfigSwitcherServer.getInstance().setupConfigSwitcher(new ConfigSwitcherServer.IConfigSwitcherCallback() {
            @Override
            public void onSetupComplete() {
                if (isUiAlive()) {
                    checkVersion();
                }
            }

            @Override
            public boolean onSetupFail() {
                if (isUiAlive()) {
                    checkVersion();
                }
                return true;
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
                    if (ApkVersionManager.getInstance().installNewVersionApk(getContext())) {
                        finishUi();
                    } else {
                        showShortToast(R.string.wel_new_version_install_fail);
                        if (isForce) {
                            finishUi();
                        } else {
                            autoLogin(500);
                        }
                    }
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
                        autoLogin(-1);
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
                        autoLogin(500);
                    }
                }
            }
        });
    }

    @Override
    public void autoLogin(final int delayTogoWelcome) {
        if (!ConfigSwitcherServer.getInstance().isEnable(ConfigKey.BUNDLE_LOGIN_KEY) ||
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
        ApkVersionManager.getInstance().checkVersion(getContext(), new ApkVersionManager.ICheckCallback() {
            @Override
            public void onNewVersionFound(boolean force, VersionEntity versionEntity) {
                if (isUiAlive()) {
                    if (force) {
                        updateVersion(true);
                    } else {
                        getUi().showVersionUpdateConfirmDialog(versionEntity.getVersionName());
                    }
                }
            }

            @Override
            public void onNoNewVersion() {
                if (isUiAlive()) {
                    autoLogin(-1);
                }
            }

            @Override
            public boolean onRequestFail() {
                if (isUiAlive()) {
                    autoLogin(-1);
                }
                return true;
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
}
