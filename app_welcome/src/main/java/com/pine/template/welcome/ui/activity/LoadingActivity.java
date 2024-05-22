package com.pine.template.welcome.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.app.template.app_welcome.router.RouterMainCommand;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.welcome.R;
import com.pine.template.welcome.WelUrlConstants;
import com.pine.template.welcome.WelcomeApplication;
import com.pine.template.welcome.WelcomeKeyConstants;
import com.pine.template.welcome.databinding.LoadingActivityBinding;
import com.pine.template.welcome.remote.WelcomeRouterClient;
import com.pine.template.welcome.updater.ApkVersionManager;
import com.pine.template.welcome.updater.VersionEntity;
import com.pine.template.welcome.vm.LoadingVm;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.io.File;

@PermissionsAnnotation(Permissions = {Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE})
public class LoadingActivity extends BaseMvvmFullScreenActivity<LoadingActivityBinding, LoadingVm> {
    private final static int REQUEST_CODE_USER_PRIVACY = 9998;
    private final static int REQUEST_CODE_GO_ASSIGN_UI = 9999;
    private final static int LOADING_STAY_MIN_TIME = 1000;

    public final static boolean ENABLE_LOADING_GO_ASSIGN = true;
    public final static boolean ENABLE_LOADING_AUTO_LOGIN = true;
    public final static boolean ENABLE_LOADING_GO_WELCOME = false;

    private long mStartTimeMillis;

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        boolean isTaskRoot = isTaskRoot();
        boolean isGoAssignActivityAction = isGoAssignActivityAction();
        boolean interrupt = !isTaskRoot && !isGoAssignActivityAction && ENABLE_LOADING_GO_ASSIGN;
        LogUtils.d(TAG, "isTaskRoot:" + isTaskRoot
                + ", isGoAssignActivityAction:" + isGoAssignActivityAction
                + ", ENABLE_LOADING_GO_ASSIGN:" + ENABLE_LOADING_GO_ASSIGN
                + ", interrupt:" + interrupt);
        return interrupt;
    }

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getCheckVersionData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean check) {
                AppTrackManager.getInstance().init(WelcomeApplication.mApplication,
                        WelUrlConstants.APP_TRACK());
                if (!ConfigSwitcherServer
                        .isEnable(BuildConfigKey.ENABLE_AUTO_CHECK_UPDATE)) {
                    autoLogin(-1);
                    return;
                }
                ApkVersionManager.getInstance().checkAndUpdateApk(LoadingActivity.this, true, true,
                        new ApkVersionManager.IUpdateCallback() {
                            @Override
                            public void onNoNewVersion() {
                                autoLogin(-1);
                            }

                            @Override
                            public void onNewVersionFound(VersionEntity versionEntity) {

                            }

                            @Override
                            public boolean installApk(VersionEntity versionEntity, File apkFile) {
                                return false;
                            }

                            @Override
                            public void onUpdateComplete(VersionEntity versionEntity) {
                                finish();
                            }

                            @Override
                            public void onUpdateErr(int errCode, String errMsg,
                                                    VersionEntity versionEntity) {
                                if (errCode == 0) {
                                    showShortToast(R.string.base_new_version_update_cancel);
                                } else if (errCode == 1) {
                                    showShortToast(errMsg);
                                } else if (errCode == 2) {
                                    showShortToast(getString(
                                            R.string.base_new_version_download_extra_fail, errMsg));
                                } else if (errCode == 3) {
                                    showShortToast(R.string.base_new_version_download_fail);
                                } else if (errCode == 4) {
                                    showShortToast(R.string.base_new_version_install_fail);
                                } else {
                                    showShortToast(errMsg);
                                }
                                if (versionEntity.isForce()) {
                                    finish();
                                } else {
                                    autoLogin(-1);
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_loading;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!SharePreferenceUtils.readBooleanFromConfig(WelcomeKeyConstants.USER_PRIVACY_AGREE, false)
                && ENABLE_LOADING_GO_ASSIGN) {
            startActivityForResult(new Intent(this, UserPrivacyActivity.class), REQUEST_CODE_USER_PRIVACY);
        } else {
            doneAppStartTask();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_USER_PRIVACY == requestCode && resultCode == RESULT_OK) {
            doneAppStartTask();
        } else if (REQUEST_CODE_GO_ASSIGN_UI == requestCode) {
            goWelcomeActivity();
        } else {
            finish();
        }
    }

    private void doneAppStartTask() {
        mStartTimeMillis = System.currentTimeMillis();
        mViewModel.setupConfigSwitcher();
    }

    @Override
    protected void onDestroy() {
        ApkVersionManager.getInstance().onClear();
        super.onDestroy();
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    public void autoLogin(final int delayTogo) {
        if (!ConfigSwitcherServer.isEnable(BuildConfigKey.BUNDLE_LOGIN) ||
                WelcomeApplication.isLogin() || !ENABLE_LOADING_AUTO_LOGIN) {
            gotoNext(delayTogo);
            return;
        }
        WelcomeRouterClient.autoLogin(this, null, new IRouterCallback() {
            @Override
            public void onSuccess(Bundle responseBundle) {
                gotoNext(delayTogo);
            }

            @Override
            public boolean onFail(int failCode, String errorInfo) {
                gotoNext(delayTogo);
                return true;
            }
        });
    }

    private void gotoNext(int delayTogo) {
        long delay = LOADING_STAY_MIN_TIME - (System.currentTimeMillis() - mStartTimeMillis);
        delay = delay > delayTogo ? delay : delayTogo > 0 ? delayTogo : 0;
        if (isGoAssignActivityAction()) {
            goAssignActivity();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goWelcomeActivity();
                }
            }, delay);
        }
    }

    private void goWelcomeActivity() {
        if (!ENABLE_LOADING_GO_WELCOME) {
            goMainHomeActivity();
            return;
        }
        Intent intent = new Intent(LoadingActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goMainHomeActivity() {
        WelcomeRouterClient.goMainHomeActivity(LoadingActivity.this, null, new IRouterCallback() {
            @Override
            public void onSuccess(Bundle responseBundle) {
                LogUtils.d(TAG, "onSuccess " + RouterMainCommand.goMainHomeActivity);
                finish();
                return;
            }

            @Override
            public boolean onFail(int failCode, String errorInfo) {
                return false;
            }
        });
    }

    private boolean isGoAssignActivityAction() {
        Intent startupIntent = getIntent().getParcelableExtra(WelcomeKeyConstants.STARTUP_INTENT);
        boolean isGoAssignActivityAction = Intent.ACTION_VIEW.equals(startupIntent.getAction());
        LogUtils.d(TAG, "gotoNext startupIntent: " + startupIntent
                + ", isGoAssignActivityAction: " + isGoAssignActivityAction);
        return isGoAssignActivityAction;
    }

    private void goAssignActivity() {
        Intent startupIntent = getIntent().getParcelableExtra(WelcomeKeyConstants.STARTUP_INTENT);
        if (Intent.ACTION_VIEW.equals(startupIntent.getAction())) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(WelcomeKeyConstants.STARTUP_INTENT, startupIntent);
            bundle.putInt(WelcomeKeyConstants.REQUEST_CODE, REQUEST_CODE_GO_ASSIGN_UI);
            if (startupIntent.getType() != null) {

            } else {
                goWelcomeActivity();
            }
        } else {
            goWelcomeActivity();
        }
    }
}
