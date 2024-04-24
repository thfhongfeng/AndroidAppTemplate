package com.pine.template.face;

import com.pine.app.lib.face.detect.FaceDetectView;
import com.pine.template.base.BaseConstants;
import com.pine.tool.util.AppUtils;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public interface FaceConstants extends BaseConstants {
    public static int PAGE_SIZE = AppUtils.isPortScreen() ? 50 : 20;
    public static int PERSON_PAGE_SIZE = AppUtils.isPortScreen() ? 50 : 20;
    public static int RECORD_PAGE_SIZE = AppUtils.isPortScreen() ? 50 : 20;

    public static int DETECT_PROVIDER = FaceDetectView.DETECT_TYPE_NORMAL;
}
