package com.pine.mvvm.model;

import com.pine.config.BuildConfig;
import com.pine.mvvm.model.local.MvvmLocalModelManager;
import com.pine.mvvm.model.net.MvvmNetModelManager;

public class MvvmModelFactory {
    public static IMvvmModelManager getInstance() {
        switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
            case "local":
                return MvvmLocalModelManager.getInstance();
            default:
                return MvvmNetModelManager.getInstance();
        }
    }

    public static IMvvmShopModel getMvvmShopModel() {
        return getInstance().getMvvmShopModel();
    }

    public static IMvvmTravelNoteModel getMvvmTravelNoteModel() {
        return getInstance().getMvvmTravelNoteModel();
    }
}
