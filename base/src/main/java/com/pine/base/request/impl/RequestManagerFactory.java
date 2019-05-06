package com.pine.base.request.impl;

import android.content.Context;

import com.pine.base.request.IRequestManager;
import com.pine.base.request.impl.database.DbRequestManager;
import com.pine.base.request.impl.http.HttpRequestManagerFactory;
import com.pine.config.BuildConfig;

import java.util.HashMap;

public class RequestManagerFactory {
    private RequestManagerFactory() {

    }

    public static IRequestManager getRequestManager(Context context, HashMap<String, String> head) {
        switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
            case "local":
                return DbRequestManager.getInstance().init(context, head);
            default:
                return HttpRequestManagerFactory.getRequestManager(context, head);
        }
    }
}
