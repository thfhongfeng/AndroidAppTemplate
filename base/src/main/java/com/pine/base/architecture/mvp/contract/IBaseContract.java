package com.pine.base.architecture.mvp.contract;

import android.app.Activity;

/**
 * Created by tanghongfeng on 2018/9/21
 */

public interface IBaseContract {
    interface Ui {
        Activity getContextActivity();

        void startLoadingUi();

        void finishLoadingUi();
    }

    interface Presenter {

    }
}
