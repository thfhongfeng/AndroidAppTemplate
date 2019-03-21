package com.pine.main.model.local;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.main.bean.MainBusinessItemEntity;
import com.pine.main.model.IMainHomeModel;

import java.util.ArrayList;

public class MainHomeLocalModel implements IMainHomeModel {
    @Override
    public void requestBusinessListData(@NonNull IModelAsyncResponse<ArrayList<MainBusinessItemEntity>> callback) {

    }
}
