package com.pine.base.access;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.pine.tool.access.IUiAccessExecutor;
import com.pine.tool.access.UiAccessTimeInterval;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessVipLevelExecutor implements IUiAccessExecutor {
    public UiAccessVipLevelExecutor() {

    }

    @Override
    public boolean onExecute(Activity activity, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        return true;
    }

    @Override
    public boolean onExecute(Fragment fragment, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        return true;
    }
}
