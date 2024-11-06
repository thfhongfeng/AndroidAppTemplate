package com.pine.app.lib.face.detect;

public class FaceBorder {
    public static final int CENTER_UNDEFINED = 0;

    public static final int CENTER_MATCH = 1;
    public static final int CENTER_NOT_MATCH = 2;

    public static final int RECT_UNDEFINED = 0;
    public static final int RECT_MATCH = 1;
    public static final int RECT_SMALL = 2;
    public static final int RECT_BIG = 3;

    public FaceRange faceRange = new FaceRange();
    public boolean mainFace = false;
    public float confidence = 0f;
    public float liveConfidence = 0f;

    // 进行FaceRange的matchDetect后赋值
    // 人脸中心点的匹配状态
    public int centerMatchState = CENTER_UNDEFINED;
    // 人脸边框的匹配状态（RECT_SMALL等）
    public int rectMatchState = RECT_UNDEFINED;
}
