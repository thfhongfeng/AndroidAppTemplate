package com.pine.tool;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.pine.tool.helper.LanguageHelper;

/**
 * Created by tanghongfeng on 2019/11/1.
 */

public class BasementApplication extends Application {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.getInstance().attachBaseContext(newBase));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageHelper.getInstance().setupAppLanguageLocal(this);
    }
}