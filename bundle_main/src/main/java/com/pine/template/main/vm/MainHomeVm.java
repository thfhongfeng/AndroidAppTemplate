package com.pine.template.main.vm;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.config.router.command.RouterFaceCommand;
import com.pine.template.config.router.command.RouterMvcCommand;
import com.pine.template.config.router.command.RouterMvpCommand;
import com.pine.template.config.router.command.RouterMvvmCommand;
import com.pine.template.main.bean.MainBusinessItemEntity;
import com.pine.template.main.model.MainHomeModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.router.RouterManager;

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
            if (RouterManager.isBundleEnable(BuildConfigKey.BIZ_BUNDLE_MVC)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvc");
                jsonObject.put("bundle", BuildConfigKey.BIZ_BUNDLE_MVC);
                jsonObject.put("command", RouterMvcCommand.goMvcHomeActivity);
                jsonArray.put(jsonObject);
            }
            if (RouterManager.isBundleEnable(BuildConfigKey.BIZ_BUNDLE_MVP)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvp");
                jsonObject.put("bundle", BuildConfigKey.BIZ_BUNDLE_MVP);
                jsonObject.put("command", RouterMvpCommand.goMvpHomeActivity);
                jsonArray.put(jsonObject);
            }
            if (RouterManager.isBundleEnable(BuildConfigKey.BIZ_BUNDLE_MVVM)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Mvvm");
                jsonObject.put("bundle", BuildConfigKey.BIZ_BUNDLE_MVVM);
                jsonObject.put("command", RouterMvvmCommand.goMvvmHomeActivity);
                jsonArray.put(jsonObject);
            }
            if (RouterManager.isBundleEnable(BuildConfigKey.BIZ_BUNDLE_FACE)) {
                jsonObject = new JSONObject();
                jsonObject.put("name", "Business Face");
                jsonObject.put("bundle", BuildConfigKey.BIZ_BUNDLE_FACE);
                jsonObject.put("command", RouterFaceCommand.goFaceHomeActivity);
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
