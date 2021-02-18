package com.pine.template.main.vm;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.template.base.router.command.RouterMvcCommand;
import com.pine.template.base.router.command.RouterMvpCommand;
import com.pine.template.base.router.command.RouterMvvmCommand;
import com.pine.template.config.ConfigKey;
import com.pine.template.config.switcher.ConfigSwitcherServer;
import com.pine.template.main.bean.MainBusinessItemEntity;
import com.pine.template.main.model.MainHomeModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainHomeVm extends ViewModel {
    private MainHomeModel mHomeModel = new MainHomeModel();

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
        setBusinessBundleList(entityList);
    }

    private MutableLiveData<ArrayList<MainBusinessItemEntity>> businessBundleListData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<MainBusinessItemEntity>> getBusinessBundleListData() {
        return businessBundleListData;
    }

    public void setBusinessBundleList(ArrayList<MainBusinessItemEntity> list) {
        businessBundleListData.setValue(list);
    }
}
