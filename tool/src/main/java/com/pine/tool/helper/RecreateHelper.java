package com.pine.tool.helper;

import com.pine.tool.ui.Activity;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;
import java.util.Set;

public class RecreateHelper {
    private final String TAG = this.getClass().getSimpleName();

    private static volatile RecreateHelper instance;

    public synchronized static RecreateHelper getInstance() {
        if (instance == null) {
            instance = new RecreateHelper();
        }
        return instance;
    }

    private RecreateHelper() {
    }

    private HashMap<Integer, Boolean> mRecreateMap = new HashMap<>();

    public void addActivityTag(Activity activity) {
        int hashcode = activity.hashCode();
        LogUtils.d(TAG, activity + " addActivityTag hashcode:" + hashcode);
        mRecreateMap.put(hashcode, false);
    }

    public void setEffectiveImmediately(boolean enable) {
        Set<Integer> keySet = mRecreateMap.keySet();
        for (int key : keySet) {
            mRecreateMap.put(key, enable);
        }
    }

    public boolean calRecreateActivity(Activity activity, int hashcode) {
        boolean recreate = mRecreateMap.containsKey(hashcode) && mRecreateMap.get(hashcode);
        LogUtils.d("ActivityLifecycle", activity + "calRecreateActivity recreate:" + recreate);
        if (recreate) {
            mRecreateMap.remove(hashcode);
            activity.recreate();
        }
        return recreate;
    }
}
