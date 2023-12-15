package com.pine.template.base.component.share.manager;

import android.content.Context;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/10/9
 */

public class TencentShareManager {
    private final static String TAG = LogUtils.makeLogTag(TencentShareManager.class);
    private static volatile TencentShareManager mInstance;
    private String QQ_FOR_APP_ID = "";
    private String WX_FOR_APP_ID = "";
    private String WX_SECRET_KEY = "";
    private int ICON_ID;
    private String APP_NAME_ID = "";
    private String HOST = "";

    private TencentShareManager() {
    }

    protected static TencentShareManager getInstance() {
        if (mInstance == null) {
            synchronized (TencentShareManager.class) {
                if (mInstance == null) {
                    mInstance = new TencentShareManager();
                }
            }
        }
        return mInstance;
    }

    public void init(String qq_for_app_id, String wx_for_app_id, String wx_secret_key,
                     int icon, String appName, String baseUrl) {
        QQ_FOR_APP_ID = qq_for_app_id;
        WX_FOR_APP_ID = wx_for_app_id;
        WX_SECRET_KEY = wx_secret_key;
        ICON_ID = icon;
        APP_NAME_ID = appName;
        HOST = baseUrl;
    }

    private boolean isInit() {
        return QQ_FOR_APP_ID.length() != 0 && WX_FOR_APP_ID.length() != 0 &&
                WX_SECRET_KEY.length() != 0 && APP_NAME_ID.length() != 0 && HOST.length() != 0;
    }

    /**
     * 分享微信朋友 or 朋友圈
     *
     * @param isTimeline true为朋友  false为朋友圈
     * @param url
     */
    public boolean shareWebPageToWX(Context context, boolean isTimeline, String url, String title, String description) {
        if (!isInit()) {
            LogUtils.d(TAG, "TencentShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27  
        return true;
    }

    public boolean shareWebPageToQQ(Context context, String title, String description, String url) {
        if (!isInit()) {
            LogUtils.d(TAG, "TencentShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27
        return true;
    }
}
