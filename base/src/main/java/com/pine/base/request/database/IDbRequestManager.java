package com.pine.base.request.database;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IDbRequestManager {
    String SESSION_ID = "JSESSIONID";
    String COOKIE_KEY = "Cookie";

    boolean execSQL(@NonNull Context context, String sql);

    @NonNull
    DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                           HashMap<String, HashMap<String, String>> header);

    enum ActionType {
        COMMON, // common
        RETRY_AFTER_RE_LOGIN, //  retry after re-login
        RETRY_WHEN_ERROR     // retry when error
    }
}
