package com.pine.base.request.impl.http;

import android.content.Context;

import com.pine.base.request.IRequestManager;
import com.pine.base.request.impl.http.nohttp.NoRequestManager;
import com.pine.config.BuildConfig;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class HttpRequestManagerFactory {
    private HttpRequestManagerFactory() {

    }

    public static IRequestManager getRequestManager(Context context, HashMap<String, String> head) {
        switch (BuildConfig.APP_THIRD_HTTP_REQUEST_PROVIDER) {
            case "nohttp":
                return NoRequestManager.getInstance().init(context, head);
            default:
                return NoRequestManager.getInstance().init(context, head);
        }
    }
}
