package com.pine.main.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.presenter.BasePresenter;
import com.pine.config.ConfigBundleKey;
import com.pine.main.bean.MainBusinessItemEntity;
import com.pine.main.contract.IMainHomeContract;
import com.pine.main.model.IMainHomeModel;
import com.pine.main.model.MainModelFactory;
import com.pine.router.command.RouterDemoCommand;
import com.pine.router.command.RouterMvcCommand;
import com.pine.router.command.RouterMvpCommand;
import com.pine.router.command.RouterMvvmCommand;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MainHomePresenter extends BasePresenter<IMainHomeContract.Ui> implements IMainHomeContract.Presenter {
    private IMainHomeModel mHomeModel;

    public MainHomePresenter() {
        mHomeModel = MainModelFactory.getMainHomeModel();
    }

    @Override
    public void loadBusinessBundleData() {
        String res = "[{name:'Business Mvc',bundle:" + ConfigBundleKey.BUSINESS_MVC_BUNDLE_KEY
                + ",command:" + RouterMvcCommand.goMvcHomeActivity + "},"
                + "{name:'Business Mvp',bundle:" + ConfigBundleKey.BUSINESS_MVP_BUNDLE_KEY
                + ",command:" + RouterMvpCommand.goMvpHomeActivity + "},"
                + "{name:'Business Mvvm',bundle:" + ConfigBundleKey.BUSINESS_MVVM_BUNDLE_KEY
                + ",command:" + RouterMvvmCommand.goMvvmHomeActivity + "},"
                + "{name:'Business Demo',bundle:" + ConfigBundleKey.BUSINESS_DEMO_BUNDLE_KEY
                + ",command:" + RouterDemoCommand.goDemoHomeActivity + "}]";
        ArrayList<MainBusinessItemEntity> entityList = new Gson().fromJson(res, new TypeToken<ArrayList<MainBusinessItemEntity>>() {
        }.getType());
        getUi().setBusinessBundleData(entityList);
    }
}
