package com.pine.welcome.vm;

import androidx.lifecycle.MutableLiveData;

import com.pine.base.BaseApplication;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.R;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.manager.ApkVersionManager;

/**
 * Created by tanghongfeng on 2019/10/28.
 */

public class LoadingVm extends ViewModel {

    public void setupConfigSwitcher() {
        ConfigSwitcherServer.getInstance().setupConfigSwitcher(BaseApplication.isLogin(),
                new ConfigSwitcherServer.IConfigSwitcherCallback() {
                    @Override
                    public void onSetupComplete() {
                        checkVersion();
                    }

                    @Override
                    public boolean onSetupFail() {
                        checkVersion();
                        return false;
                    }
                });
    }

    public void updateVersion(final boolean isForce) {
        ApkVersionManager.getInstance().startUpdate(new ApkVersionManager.UpdateListener() {

            @Override
            public void onDownloadStart(boolean isResume, long rangeSize, long allCount) {
                LogUtils.d(TAG, "onDownloadStart isResume:" + isResume +
                        ", rangeSize:" + rangeSize + ", allCount:" + allCount);
                setVersionUpdateForce(isForce);
            }

            @Override
            public void onDownloadProgress(int progress, long fileCount, long speed) {
                setVersionUpdateProgress(progress);
            }

            @Override
            public void onDownloadComplete(String filePath) {
                LogUtils.d(TAG, "onDownloadComplete filePath:" + filePath);
                if (ApkVersionManager.getInstance().installNewVersionApk()) {
                    setToastResId(R.string.wel_new_version_install_fail);
                    setVersionUpdateState(1, isForce);
                } else {
                    setToastResId(R.string.wel_new_version_install_fail);
                    setVersionUpdateState(-1, isForce);
                }
            }

            @Override
            public void onDownloadCancel() {
                LogUtils.d(TAG, "onDownloadCancel");
                setToastResId(R.string.wel_new_version_update_cancel);
                setVersionUpdateState(0, isForce);
            }

            @Override
            public void onDownloadError(Exception exception) {
                LogUtils.d(TAG, "onDownloadError onDownloadError:" + exception);
                if (exception instanceof MessageException) {
                    setToastRes(R.string.wel_new_version_download_extra_fail, "(" + exception.getMessage() + ")");
                } else {
                    setToastResId(R.string.wel_new_version_download_fail);
                }
                setVersionUpdateState(-2, isForce);
            }
        });
    }

    private void checkVersion() {
        ApkVersionManager.getInstance().checkVersion(new ApkVersionManager.ICheckCallback() {
            @Override
            public void onNewVersionFound(boolean force, VersionEntity versionEntity) {
                if (force) {
                    updateVersion(true);
                } else {
                    setNewVersionName(versionEntity.getVersionName());
                }
            }

            @Override
            public void onNoNewVersion() {
                setNewVersionName(null);
            }

            @Override
            public boolean onRequestFail() {
                setNewVersionName(null);
                return false;
            }
        });
    }

    public void cancelDownLoad() {
        RequestManager.cancelBySign(ApkVersionManager.getInstance().CANCEL_SIGN);
    }

    private MutableLiveData<String> newVersionNameData = new MutableLiveData<>();

    public void setNewVersionName(String newVersionName) {
        newVersionNameData.setValue(newVersionName);
    }

    public MutableLiveData<String> getNewVersionNameData() {
        return newVersionNameData;
    }

    private MutableLiveData<Boolean> versionUpdateForceData = new MutableLiveData<>();

    public void setVersionUpdateForce(boolean isForce) {
        versionUpdateForceData.setValue(isForce);
    }

    public MutableLiveData<Boolean> getVersionUpdateForceData() {
        return versionUpdateForceData;
    }

    private MutableLiveData<Integer> versionUpdateProgressData = new MutableLiveData<>();

    public void setVersionUpdateProgress(int progress) {
        versionUpdateProgressData.setValue(progress);
    }

    public MutableLiveData<Integer> getVersionUpdateProgressData() {
        return versionUpdateProgressData;
    }

    // -2:下载失败；-1:安装失败；0:取消；大于0:更新成功
    private ParametricLiveData<Integer, Boolean> versionUpdateStateData = new ParametricLiveData<>();

    public void setVersionUpdateState(int state, boolean isForce) {
        versionUpdateStateData.setValue(state, isForce);
    }

    public ParametricLiveData<Integer, Boolean> getVersionUpdateStateData() {
        return versionUpdateStateData;
    }
}
