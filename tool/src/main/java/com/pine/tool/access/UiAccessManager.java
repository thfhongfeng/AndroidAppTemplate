package com.pine.tool.access;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessManager {
    private static volatile UiAccessManager mInstance;
    private LinkedHashMap<String, IUiAccessExecutor> mAccessExecutorMap = new LinkedHashMap<String, IUiAccessExecutor>();

    private UiAccessManager() {
    }

    public static UiAccessManager getInstance() {
        if (mInstance == null) {
            synchronized (UiAccessManager.class) {
                if (mInstance == null) {
                    mInstance = new UiAccessManager();
                }
            }
        }
        return mInstance;
    }

    public void addAccessExecutor(String key, IUiAccessExecutor accessExecutor) {
        mAccessExecutorMap.put(key, accessExecutor);
    }

    public void removeAccessExecutor(IUiAccessExecutor accessExecutor) {
        mAccessExecutorMap.remove(accessExecutor);
    }

    public void clearAccessExecutor() {
        mAccessExecutorMap.clear();
    }

    public boolean checkCanAccess(@NonNull Activity activity, UiAccessTimeInterval accessTimeInterval,
                                  @NonNull String[] types, @NonNull String[] args,
                                  @NonNull HashMap<String, String> actionsMap) {
        if (activity == null || types == null || args == null ||
                types.length < 1 || types.length != args.length) {
            return true;
        }
        for (int i = 0; i < types.length; i++) {
            if (mAccessExecutorMap.get(types[i]) != null &&
                    !mAccessExecutorMap.get(types[i]).onExecute(activity, args[i], actionsMap, accessTimeInterval)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkCanAccess(@NonNull Fragment fragment, UiAccessTimeInterval accessTimeInterval,
                                  @NonNull String[] types, @NonNull String[] args,
                                  @NonNull HashMap<String, String> actionsMap) {
        if (fragment == null || types == null || args == null ||
                types.length < 1 || types.length != args.length) {
            return true;
        }
        for (int i = 0; i < types.length; i++) {
            if (mAccessExecutorMap.get(types[i]) != null &&
                    !mAccessExecutorMap.get(types[i]).onExecute(fragment, args[i], actionsMap, accessTimeInterval)) {
                return false;
            }
        }
        return true;
    }
}
