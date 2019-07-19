package com.pine.welcome.contract;

import com.pine.base.widget.dialog.ProgressDialog;
import com.pine.tool.architecture.mvp.contract.IContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface ILoadingContract {
    interface Ui extends IContract.Ui {
        void showVersionUpdateConfirmDialog(String newVersionName);

        void showVersionUpdateProgressDialog(ProgressDialog.IDialogActionListener listener);

        void updateVersionUpdateProgressDialog(int progress);

        void dismissVersionUpdateProgressDialog();
    }

    interface Presenter extends IContract.Presenter {

        void setupConfigSwitcher();

        void updateVersion(boolean isForce);

        void autoLogin(int delayTogoWelcome);
    }
}
