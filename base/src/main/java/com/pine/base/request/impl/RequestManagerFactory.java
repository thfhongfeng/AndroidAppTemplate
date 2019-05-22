package com.pine.base.request.impl;

import android.content.Context;

import com.pine.base.request.IRequestManager;
import com.pine.base.request.impl.database.DbRequestManager;
import com.pine.base.request.impl.http.HttpRequestManagerFactory;
import com.pine.config.BuildConfig;

import java.util.HashMap;

public class RequestManagerFactory {
    private static IRequestManager mRequestManager;

    private RequestManagerFactory() {

    }

    public static IRequestManager makeRequestManager(Context context, HashMap<String, String> head) {
        if (mRequestManager == null) {
            synchronized (RequestManagerFactory.class) {
                if (mRequestManager == null) {
                    switch (BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER) {
                        case "local":
                            mRequestManager = DbRequestManager.getInstance().init(context, head);
                            break;
                        default:
                            mRequestManager = HttpRequestManagerFactory.getRequestManager(context, head);
                            break;
                    }
                }
            }
        }
        return mRequestManager;
    }

    public static IRequestManager getRequestManager() {
        if (mRequestManager == null) {
            throw new IllegalStateException("IRequestManager must be made first before getting");
        }
        return mRequestManager;
    }
}
