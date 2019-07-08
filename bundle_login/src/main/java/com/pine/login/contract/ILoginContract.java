package com.pine.login.contract;

import android.support.annotation.NonNull;

import com.pine.tool.architecture.mvp.contract.IBaseContract;
import com.pine.tool.bean.BaseInputParam;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface ILoginContract {
    interface Ui extends IBaseContract.Ui {
        @NonNull
        BaseInputParam getUserMobileParam(String key);

        @NonNull
        BaseInputParam getUserPasswordParam(String key);
    }

    interface Presenter extends IBaseContract.Presenter {
        void login();

        void goRegister();
    }
}
