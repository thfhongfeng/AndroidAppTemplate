package com.pine.mvvm.model.net;

import com.pine.mvvm.model.IMvvmModelManager;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.mvvm.model.IMvvmTravelNoteModel;

public class MvvmNetModelManager implements IMvvmModelManager {
    private static MvvmNetModelManager mInstance;

    private MvvmNetModelManager() {

    }

    public static synchronized MvvmNetModelManager getInstance() {
        if (mInstance == null) {
            mInstance = new MvvmNetModelManager();
        }
        return mInstance;
    }

    @Override
    public IMvvmShopModel getMvvmShopModel() {
        return new MvvmShopModel();
    }

    @Override
    public IMvvmTravelNoteModel getMvvmTravelNoteModel() {
        return new MvvmTravelNoteModel();
    }
}
