package com.pine.template.base.component.share.manager;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.pine.tool.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/10/9
 */

public class SinaShareManager {
    private final static String TAG = LogUtils.makeLogTag(SinaShareManager.class);
    private static volatile SinaShareManager mInstance;
    private boolean mIsInit;

    private SinaShareManager() {
    }

    protected static SinaShareManager getInstance() {
        if (mInstance == null) {
            synchronized (SinaShareManager.class) {
                if (mInstance == null) {
                    mInstance = new SinaShareManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, SinaShareManager.SinaConfig sinaConfig) {
        String scope = "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write";
        // TODO: 2022/9/27 init 
        mIsInit = true;
    }

    private boolean isInit() {
        return mIsInit;
    }

    public boolean shareTextToWeiBo(Activity activity, String title, String text, String url) {
        if (!isInit()) {
            LogUtils.d(TAG, "SinaShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27
        return true;
    }

    public boolean shareWebPageToWeiBo(Activity activity, String title, String text, String description,
                                       int resId, String url) {
        if (!isInit()) {
            LogUtils.d(TAG, "SinaShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27  
        return true;
    }

    public boolean shareImageToWeiBo(Activity activity, String title, String description, Uri uri) {
        if (!isInit()) {
            LogUtils.d(TAG, "SinaShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27  
        return true;
    }

    public boolean shareMultiImageToWeiBo(Activity activity, ArrayList<Uri> uriList) {
        if (!isInit()) {
            LogUtils.d(TAG, "SinaShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27  
        return true;
    }

    public boolean shareVideoToWeiBo(Activity activity, Uri uri) {
        if (!isInit()) {
            LogUtils.d(TAG, "SinaShareManager was not init");
            return false;
        }
        // TODO: 2022/9/27
        return true;
    }

    public static class SinaConfig {
        public String weboAppKey;
        public String weboRedirectKey;

        public SinaConfig(String weboAppKey, String weboRedirectKey) {
            this.weboAppKey = weboAppKey;
            this.weboRedirectKey = weboRedirectKey;
        }
    }
}
