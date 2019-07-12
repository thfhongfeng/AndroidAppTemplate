package com.pine.main.contract;

import com.pine.main.bean.MainBusinessItemEntity;
import com.pine.tool.architecture.mvp.contract.IContract;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMainHomeContract {
    interface Ui extends IContract.Ui {
        void setBusinessBundleData(ArrayList<MainBusinessItemEntity> list);
    }

    interface Presenter extends IContract.Presenter {
        void loadBusinessBundleData();
    }
}
