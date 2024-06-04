package com.pine.tool.camera;

public class CameraConfig {
    public final static String DEFAULT = "default";
    public final static String FRONT = "front";
    public final static String BACK = "back";
    public final static String EXTERNAL = "external";

    // 相机相关配置
    public String cameraType = FRONT;//摄像头类型默认是前置摄像头（如果设置了cameraId，则cameraId的方式打开摄像头）
    public int cameraIndex = -1;//摄像头ID索引
    public boolean rlMirror; // 左右镜像

    public int preWidth = -1; // 固定预览宽度，preWidth和preHeight有一个小于等于0，则自动计算合适的预览尺寸
    public int preHeight = -1; // 固定预览高度，preWidth和preHeight有一个小于等于0，则自动计算合适的预览尺寸
    public boolean takePicRlMirror = false;// 照片左右镜像
    public int picWidth = -1; // 固定照片宽度，picWidth和picHeight有一个小于等于0，则自动计算合适的预览尺寸
    public int picHeight = -1; // 固定照片高度，picWidth和picHeight有一个小于等于0，则自动计算合适的预览尺寸
    public int takePicRotation = -1;// 照片方向：-1-自动；0，90，180，270对应旋转角度

    // 用于摄像头预览偏移修正。有些时候摄像头不是在设备的中间位置，而是有偏移。这种情况下，用户想要预览图像居中，
    // 人就必须正对摄像头而不是正对屏幕。为了更好的体验，可以通过设置该值进行修正，达到想要的效果
    public int displayFixTranslationX = 0;
    public int displayFixTranslationY = 0;

    public int zoomOffset; // zoom偏差值

    // 设备固定方向(用于某些设备是通过代码直接修改屏幕方向的情况)：-1-自动；0，90，180，270对应旋转角度
    public int deviceFixOrientation = -1;

    public String savePicFilePath = "";//照片（原照片）保存路径

    @Override
    public String toString() {
        return "CameraConfig{" +
                "cameraType='" + cameraType + '\'' +
                ", cameraIndex=" + cameraIndex +
                ", rlMirror=" + rlMirror +
                ", preWidth=" + preWidth +
                ", preHeight=" + preHeight +
                ", takePicRlMirror=" + takePicRlMirror +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", takePicRotation=" + takePicRotation +
                ", displayFixTranslationX=" + displayFixTranslationX +
                ", displayFixTranslationY=" + displayFixTranslationY +
                ", zoomOffset=" + zoomOffset +
                ", deviceFixOrientation=" + deviceFixOrientation +
                ", savePicFilePath='" + savePicFilePath + '\'' +
                '}';
    }
}
