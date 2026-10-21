package com.pine.template.face;

import com.pine.app.lib.face.matcher.FaceMatcher;
import com.pine.app.lib.face.matcher.opencv.OcvFaceMatcher;
import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class FaceApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(FaceApplication.class);

    public static void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    public static void attach() {
        LogUtils.d(TAG, "attach");
//        FaceMatcher.getInstance().initFaceMatcher(mApplication);
        FaceMatcher.getInstance().initFaceMatcher(mApplication, new OcvFaceMatcher(OcvFaceMatcher.FEATURE_TYPE_ARC_NET));
    }
}
