package com.pine.tool.access;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IUiAccessExecutor {
    boolean onExecute(Activity activity, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval);

    boolean onExecute(Fragment fragment, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval);
}
