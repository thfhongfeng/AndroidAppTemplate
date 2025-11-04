package com.pine.template.welcome.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.app.template.app_welcome.router.RouterMainCommand;
import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.welcome.R;
import com.pine.template.welcome.WelBaseActivity;
import com.pine.template.welcome.WelUrlConstants;
import com.pine.template.welcome.WelcomeApplication;
import com.pine.template.welcome.databinding.LoadingActivityBinding;
import com.pine.template.welcome.remote.WelcomeRouterClient;
import com.pine.template.welcome.track.TrackRecordHelper;
import com.pine.template.welcome.updater.ApkVersionManager;
import com.pine.template.welcome.updater.VersionEntity;
import com.pine.template.welcome.vm.LoadingVm;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;

import java.io.File;

@PermissionsAnnotation(Permissions = {Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE})
public class LoadingActivity extends WelBaseActivity<LoadingActivityBinding, LoadingVm> {
    @Override
    public String makeUiName() {
        return "Loading";
    }

    private final static int LOADING_STAY_MIN_TIME = 1000;

    // 检查网络状态间隔
    private final static int LOADING_CHECK_NET_PER_DELAY = 1 * 1000;
    private int mNetCheckCount;
    private Handler mNetCheckHandler = new Handler(Looper.getMainLooper());

    // 是否允许自动登录
    public static boolean ENABLE_LOADING_AUTO_LOGIN = false;
    // 是否允许先跳转到welcome界面
    public static boolean ENABLE_LOADING_GO_WELCOME = false;

    private long mStartTimeMillis;

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
                ApkVersionManager.getInstance().checkAndUpdateApk(LoadingActivity.this, false, true, true,
                        new ApkVersionManager.IUpdateCallback() {
                            @Override
                            public void onNoNewVersion(String cause) {
                                TrackRecordHelper.getInstance().recordAppUpdateCheck(null, cause);
                                autoLogin(-1);
                            }

                            @Override
                            public void onNewVersionFound(VersionEntity versionEntity) {
                                TrackRecordHelper.getInstance().recordAppUpdateCheck(versionEntity.getVersionName(), null);
                            }

                            @Override
                            public boolean installApk(VersionEntity versionEntity, File apkFile) {
                                return false;
                            }

                            @Override
                            public void onUpdateComplete(VersionEntity versionEntity) {
                                LogUtils.d(TAG, "onUpdateComplete versionEntity:" + versionEntity);
                                TrackRecordHelper.getInstance().recordAppUpdateSuccess(versionEntity.getVersionName());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onUpdateErr(int errCode, String errMsg,
                                                    VersionEntity versionEntity) {
                                LogUtils.d(TAG, "onUpdateErr errCode:" + errCode + ", errMsg:" + errMsg
                                        + ", versionEntity:" + versionEntity);
                                if (errCode == 0) {
                                    showShortToast(R.string.base_new_version_update_cancel);
                                    ApkVersionManager.getInstance().scheduleBgUpdateCheckIfNeed(versionEntity);
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

    private int mLoadingCheckNetMaxCount = 30;

    @Override
    protected void init(Bundle savedInstanceState) {
        boolean enableLoadingCheckNet = ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_LOADING_CHECK_NET, true);
        ENABLE_LOADING_AUTO_LOGIN = ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_LOADING_AUTO_LOGIN, false);
        ENABLE_LOADING_GO_WELCOME = ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_LOADING_GO_WELCOME, false);
        if (enableLoadingCheckNet) {
            mLoadingCheckNetMaxCount = ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_LOADING_CHECK_NET_MAX_COUNT, 30);
            scheduleNetCheck();
        } else {
            doneAppStartTask();
        }
    }

    private void scheduleNetCheck() {
        mNetCheckCount++;
        if (NetWorkUtils.checkNetWork() || mNetCheckCount > mLoadingCheckNetMaxCount) {
            mBinding.tvToast.setText("");
            mNetCheckHandler.removeCallbacksAndMessages(null);
            doneAppStartTask();
            return;
        }
        mBinding.tvToast.setText(R.string.wel_loading_waiting_for_network);
        mNetCheckHandler.removeCallbacksAndMessages(null);
        mNetCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scheduleNetCheck();
            }
        }, LOADING_CHECK_NET_PER_DELAY);
    }

    private void doneAppStartTask() {
        mStartTimeMillis = System.currentTimeMillis();
        mViewModel.setupConfigSwitcher();
    }

    @Override
    protected void onDestroy() {
        mNetCheckHandler.removeCallbacksAndMessages(null);
        ApkVersionManager.getInstance().onClear();
        super.onDestroy();
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    public void autoLogin(final int delayTogo) {
        if (WelcomeApplication.isLogin() || !ENABLE_LOADING_AUTO_LOGIN) {
            gotoNext(delayTogo);
            return;
        }
        WelcomeRouterClient.autoLogin(this, null, new IRouterCallback() {
            @Override
            public boolean onSuccess(Bundle responseBundle) {
                gotoNext(delayTogo);
                return true;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goWelcomeActivity();
            }
        }, delay);
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
            public boolean onSuccess(Bundle responseBundle) {
                LogUtils.d(TAG, "onSuccess " + RouterMainCommand.goMainHomeActivity);
                finish();
                return true;
            }

            @Override
            public boolean onFail(int failCode, String errorInfo) {
                return false;
            }
        });
    }
}
