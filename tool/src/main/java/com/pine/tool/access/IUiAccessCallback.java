package com.pine.tool.access;

import android.app.Activity;
import android.support.v4.app.Fragment;

public interface IUiAccessCallback {
    void onAccessForbidden(Activity activity, String args);

    void onAccessForbidden(Fragment fragment, String args);
}
