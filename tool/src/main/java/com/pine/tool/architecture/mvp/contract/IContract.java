package com.pine.tool.architecture.mvp.contract;

import android.app.Activity;

/**
 * Created by tanghongfeng on 2018/9/21
 */

public interface IContract {
    interface Ui {
        Activity getContextActivity();

        void setLoadingUiVisibility(boolean visibility);
    }

    interface Presenter {

    }
}
