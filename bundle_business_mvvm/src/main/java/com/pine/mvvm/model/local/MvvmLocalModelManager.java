package com.pine.mvvm.model.local;

import com.pine.mvvm.model.IMvvmModelManager;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.mvvm.model.IMvvmTravelNoteModel;

public class MvvmLocalModelManager implements IMvvmModelManager {
    private static MvvmLocalModelManager mInstance;

    private MvvmLocalModelManager() {

    }

    public static synchronized MvvmLocalModelManager getInstance() {
        if (mInstance == null) {
            mInstance = new MvvmLocalModelManager();
        }
        return mInstance;
    }

    @Override
    public IMvvmShopModel getMvvmShopModel() {
        return new MvvmShopLocalModel();
    }

    @Override
    public IMvvmTravelNoteModel getMvvmTravelNoteModel() {
        return new MvvmTravelNoteLocalModel();
    }
}
