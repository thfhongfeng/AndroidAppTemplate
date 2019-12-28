package com.pine.tool.access;

import android.app.Activity;

import java.util.HashMap;

import androidx.fragment.app.Fragment;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IUiAccessExecutor {
    boolean onExecute(Activity activity, String arg, HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval);

    boolean onExecute(Fragment fragment, String arg, HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval);
}
