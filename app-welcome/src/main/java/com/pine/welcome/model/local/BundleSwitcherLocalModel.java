package com.pine.welcome.model.local;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.welcome.bean.BundleSwitcherEntity;
import com.pine.welcome.model.IBundleSwitcherModel;

import java.util.ArrayList;

public class BundleSwitcherLocalModel implements IBundleSwitcherModel {
    @Override
    public boolean requestBundleSwitcherData(@NonNull IModelAsyncResponse<ArrayList<BundleSwitcherEntity>> callback) {
        return false;
    }
}
