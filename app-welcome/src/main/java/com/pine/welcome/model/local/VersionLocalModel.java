package com.pine.welcome.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.database.DbRequestManager;
import com.pine.base.database.IDbCommand;
import com.pine.base.database.callback.DbJsonCallback;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.WelcomeConstants;
import com.pine.welcome.bean.VersionEntity;
import com.pine.welcome.model.IVersionModel;

import org.json.JSONObject;

import java.util.HashMap;

public class VersionLocalModel implements IVersionModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_QUERY_VERSION_INFO = 1;

    @Override
    public boolean requestUpdateVersionData(@NonNull IModelAsyncResponse<VersionEntity> callback) {
        DbJsonCallback dbCallback = handleDbResponse(callback);
        return DbRequestManager.setJsonRequest(IDbCommand.REQUEST_APP_VERSION,
                new HashMap<String, String>(), TAG, DB_QUERY_VERSION_INFO, false,
                dbCallback);
    }

    private <T> DbJsonCallback handleDbResponse(final IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (DB_QUERY_VERSION_INFO == what) {
                    if (jsonObject.optBoolean(WelcomeConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(WelcomeConstants.DATA), new TypeToken<VersionEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
                return callback.onFail(e);
            }

            @Override
            public void onCancel(int what) {
                callback.onCancel();
            }
        };
    }
}
