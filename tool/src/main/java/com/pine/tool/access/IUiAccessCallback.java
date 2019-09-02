package com.pine.tool.access;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IUiAccessCallback {
    void onAccessForbidden(Activity activity, String args);

    void onAccessForbidden(Fragment fragment, String args);
}
