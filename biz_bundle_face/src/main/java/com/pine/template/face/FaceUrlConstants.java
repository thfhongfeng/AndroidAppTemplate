package com.pine.template.face;

import com.pine.template.base.BaseUrlConstants;
import com.pine.tool.util.PathUtils;

import java.io.File;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class FaceUrlConstants extends BaseUrlConstants {
    public static String IDENTITY_FACE_PATH() {
        return PathUtils.getExternalAppCachePath() + "/pic_identity_check.jpg";
    }

    public static String PERSON_FACE_PATH() {
        return PathUtils.getExternalAppCachePath() + "/face/face.jpg";
    }

    public static String PERSON_DB_FACE_DIR() {
        return PathUtils.getExternalAppCachePath() + "/dbFace";
    }

    public static String PERSON_DB_FACE_PATH(String fileName) {
        return PERSON_DB_FACE_DIR() + File.separator + fileName;
    }
}
