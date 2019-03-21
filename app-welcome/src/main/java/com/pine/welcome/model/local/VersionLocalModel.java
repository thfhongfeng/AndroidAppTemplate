package com.pine.welcome.model.local;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.model.IVersionModel;

public class VersionLocalModel implements IVersionModel {
    @Override
    public boolean requestUpdateVersionData(@NonNull IModelAsyncResponse<VersionEntity> callback) {
        return false;
    }
}
