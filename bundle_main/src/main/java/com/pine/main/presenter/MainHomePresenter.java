package com.pine.main.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.ConfigKey;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.main.bean.MainBusinessItemEntity;
import com.pine.main.contract.IMainHomeContract;
import com.pine.main.model.MainHomeModel;
import com.pine.base.router.command.RouterMvcCommand;
import com.pine.base.router.command.RouterMvpCommand;
import com.pine.base.router.command.RouterMvvmCommand;
import com.pine.tool.architecture.mvp.presenter.Presenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MainHomePresenter extends Presenter<IMainHomeContract.Ui> implements IMainHomeContract.Presenter {
    private MainHomeModel mHomeModel;

    public MainHomePresenter() {
        mHomeModel = new MainHomeModel();
    }

    @Override
    public void loadBusinessBundleData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        try {
            if (ConfigSwitcherServer.getInstance().isEnable(ConfigKey.BUNDLE_BUSINESS_MVC_KEY)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvc");
                jsonObject.put("bundle", ConfigKey.BUNDLE_BUSINESS_MVC_KEY);
                jsonObject.put("command", RouterMvcCommand.goMvcHomeActivity);
                jsonArray.put(jsonObject);
            }
            if (ConfigSwitcherServer.getInstance().isEnable(ConfigKey.BUNDLE_BUSINESS_MVP_KEY)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvp");
                jsonObject.put("bundle", ConfigKey.BUNDLE_BUSINESS_MVP_KEY);
                jsonObject.put("command", RouterMvpCommand.goMvpHomeActivity);
                jsonArray.put(jsonObject);
            }
            if (ConfigSwitcherServer.getInstance().isEnable(ConfigKey.BUNDLE_BUSINESS_MVVM_KEY)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvvm");
                jsonObject.put("bundle", ConfigKey.BUNDLE_BUSINESS_MVVM_KEY);
                jsonObject.put("command", RouterMvvmCommand.goMvvmHomeActivity);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<MainBusinessItemEntity> entityList = new Gson().fromJson(jsonArray.toString(),
                new TypeToken<ArrayList<MainBusinessItemEntity>>() {
                }.getType());
        getUi().setBusinessBundleData(entityList);
    }
}
