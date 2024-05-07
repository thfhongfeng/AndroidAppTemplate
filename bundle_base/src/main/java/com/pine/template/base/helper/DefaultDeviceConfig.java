package com.pine.template.base.helper;

import com.pine.tool.camera.CameraConfig;

public abstract class DefaultDeviceConfig {
    public static String camera_type = CameraConfig.FRONT; //摄像头类型默认是前置摄像头
    public static boolean camera_rlMirror = false;// 摄像头左右镜像
    public static int camera_pre_width = -1;// 固定预览宽度，preWidth和preHeight有一个小于等于0，则自动计算合适的预览尺寸
    public static int camera_pre_height = -1;// 固定预览高度，preWidth和preHeight有一个小于等于0，则自动计算合适的预览尺寸
    public static boolean take_pic_rlMirror = false;// 照片左右镜像
    public static int pic_width = -1;// 固定照片宽度，picWidth和picHeight有一个小于等于0，则自动计算合适的预览尺寸
    public static int pic_height = -1;// 固定照片高度，picWidth和picHeight有一个小于等于0，则自动计算合适的预览尺寸
    public static int take_pic_rotation = -1;// 照片方向：-1-自动；0，90，180，270对应旋转角度
    // 用于摄像头偏移修正。有些时候摄像头不是在设备的中间位置，而是有偏移。这种情况下，用户想要预览图像居中，
    // 人就必须正对摄像头而不是正对屏幕。为了更好的体验，可以通过设置该值进行修正，达到想要的效果
    public static int display_fix_translation_x = 0;
    public static int display_fix_translation_y = 0;

    public static int device_fix_orientation = -1;// 设备固定方向(用于某些设备是通过代码直接修改屏幕方向的情况)：-1-自动；0，90，180，270对应旋转角度

    public static String toStringStr() {
        return "DeviceConfig{" +
                ", camera_type=" + camera_type +
                ", camera_rlMirror=" + camera_rlMirror +
                ", display_fix_translation_x=" + display_fix_translation_x +
                ", display_fix_translation_y=" + display_fix_translation_y +
                ", device_fix_orientation=" + device_fix_orientation +
                '}';
    }
}

