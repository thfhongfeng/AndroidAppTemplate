package com.pine.template.face.utils;

import com.pine.app.lib.face.detect.CameraConfig;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.template.base.DeviceConfig;
import com.pine.template.face.R;
import com.pine.tool.util.AppUtils;

public class DetectConfigUtils {
    public static void mergeDeviceCameraConfig(DetectConfig config) {
        config.cameraType = DeviceConfig.camera_type;
        config.rlMirror = DeviceConfig.camera_rlMirror;
        config.preWidth = DeviceConfig.camera_pre_width;
        config.preHeight = DeviceConfig.camera_pre_height;
        config.takePicRlMirror = DeviceConfig.take_pic_rlMirror;
        config.picWidth = DeviceConfig.pic_width;
        config.picHeight = DeviceConfig.pic_height;
        config.takePicRotation = DeviceConfig.take_pic_rotation;
        config.displayFixTranslationX = DeviceConfig.display_fix_translation_x;
        config.displayFixTranslationY = DeviceConfig.display_fix_translation_y;
        config.deviceFixOrientation = DeviceConfig.device_fix_orientation;
    }

    public static DetectConfig makeDetectConfig(String savePicPath) {
        DetectConfig config = new DetectConfig(savePicPath);
        config.cameraType = CameraConfig.FRONT;
        config.Simple = 0.5f;//图片检测时的压缩取样率，0~1，越小检测越流畅
        config.MinDetectTime = 100;
        config.MaxDetectTime = 1000;//进入智能休眠检测，以1秒一次的这个速度检测
        config.EnableIdleSleepOption = true;//启用智能休眠检测机制
        config.IdleSleepOptionJudgeTime = 1000 * 60 * 3;//多少毫秒内没有检测到人脸，进入智能休眠检测
        config.delayForSaveFlow = 3000;
        config.faceRangeRatio = AppUtils.isPortScreen() ? 0.8f : 0.6f;
        config.matchCenterDiffFactor = 0.2f;
        config.centerDiffTipResId = R.string.face_tip_identity_face_rang_center_not_match;
        config.edgeSmallTipResId = R.string.face_tip_identity_face_rang_rect_not_match_small;
        config.edgeBigTipResId = R.string.face_tip_identity_face_rang_rect_not_match_big;
        config.liveConfidenceEnable = false;
        if (config.liveConfidenceEnable) {
            // 目前只有DETECT_PROVIDER_MINI_VISION支持活体检测
            config.cameraDetectProvider = DetectConfig.DETECT_PROVIDER_MINI_VISION;
        }
        config.zoomOffset = 0;
        DetectConfigUtils.mergeDeviceCameraConfig(config);
        return config;
    }
}
