package com.pine.login.contract;

import android.support.annotation.NonNull;

import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.bean.InputParam;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface ILoginContract {
    interface Ui extends IContract.Ui {
        @NonNull
        InputParam getUserMobileParam(String key);

        @NonNull
        InputParam getUserPasswordParam(String key);
    }

    interface Presenter extends IContract.Presenter {
        void login();

        void goRegister();
    }
}
