package com.pine.app.lib.face.detect;

public class DetectConfig extends CameraConfig {
    public static final String DETECT_PROVIDER_GOOGLE = "google";
    public static final String DETECT_PROVIDER_OPENCV = "opencv";
    public static final String DETECT_PROVIDER_MINI_VISION = "miniVision";

    /*=====================================================*/
    // 此块配置主要用于多Camera界面人脸检测时保持所有DetectView的与人的可探测距离一致。
    // 比如录入人脸和人脸身份认证两个DetectView，保证这两个界面的可探测距离一致，可以提高人脸比对时的准确率
    // 可探测距离就是识别人脸时人与摄像头的距离

    // 用于统一指定人脸检测标准框与预览框宽高最小值的比例（主要多个Detect界面时对人脸检测标准框进行统一），
    // 人脸检测标准框与matchRangDiffFactor最终确定该次检测是否符合标准，从而确定是否是有效检测
    // 该值同时确定了FrameHole的半径（关联hole_radius）
    public float faceRangeRatio = 0.6f;
    // 是否自动计算合适的人脸检测标准框
    public boolean autoCalFaceRang = false;
    /**
     * 摄像头动态（区别于图片人脸检测，图片人脸检测和识别均采用的是opencv）人脸检测功能提供者（google，opencv, miniVision）
     * 摄像头动态人脸检测采用google速度和资源消耗较小，但功能和准确度也相对较弱
     */
    public String cameraDetectProvider = DETECT_PROVIDER_OPENCV;
    /*=====================================================*/
    // 人脸识别相关配置

    // 人脸检测阈值
    public float confidenceThreshold = 0.6f;
    // 是否开启活体人脸检测
    public boolean liveConfidenceEnable = false;
    // 是否对所有人脸进行活体检测（false则只检查最大的那个人脸）
    public boolean liveCheckForAllFace = false;
    // 活体人脸检测阈值
    public float liveConfidenceThreshold = 0.5f;
    // 是否显示置信值在检测框上
    public boolean showConfidenceTxt = true;

    public float matchEdgeDiffFactor = 0.2f;//人脸检测标准框边框位置差异百分比（决定人脸会被检测到的位置误差）
    public float matchCenterDiffFactor = 0.2f;//人脸检测标准框中心位置差异百分比（决定人脸会被检测到的位置误差）
    public int edgeBigTipResId = -1;    //人脸检测标准框比实际人脸范围大且超过差异百分比（matchEdgeDiffFactor）的提醒文本资源id
    public int edgeSmallTipResId = -1;  //人脸检测标准框比实际人脸范围小且超过差异百分比（matchEdgeDiffFactor）的提醒文本资源id
    public int centerDiffTipResId = -1; //人脸检测中心位与比实际人脸中心位置超过差异百分比（matchCenterDiffFactor）的提醒文本资源id
    public volatile long PreDetectTime = 0;//上一次检测时刻
    public volatile long PreFaceTime = 0;//上一次检测到人脸的时刻
    //图片检测时的压缩取样率，0~1，越小检测越流畅，
    //仅针对通过摄像头采样直接获取的bitmap，并且该bitmap参数应用的了采样率；
    //而通过onPreviewFrame得到的yuv数据没有适配Simple，所以不适用，相关计算也需要忽略该值。
    public float Simple = 0.5f;
    public boolean EnableIdleSleepOption = false;//是启动空闲休眠机制
    public long IdleSleepOptionJudgeTime = 60 * 1000;//这个时间后，如果都没有检测到人脸，将进入空闲休眠检测状态
    public long MinDetectTime = 200; //最小检测时间，越小检测频率越高，可能会导致耗时卡顿加大
    //最大检测时间，需要比MinDetectTime大，启动EnableIdleSleepOption后
    //在IdleSleepOptionJudgeTime时间内没有检测到人脸，将使用MaxDetectTime进行检测人脸
    //此操作用于缓解cpu
    public long MaxDetectTime = 2 * 1000;
    private volatile boolean EnableFaceDetect = false;//是否开启人脸检测
    public int DETECT_FACE_NUM = 3;//需要检测的人脸个数
    public long delayForSaveFlow = 3000; // 开始人脸检测后多久开始有效的人脸检测保存流程(单位毫秒)
    public long saveCompressPicMaxSize = 0;//人脸照片（压缩照片）最大大小（0表示不压缩）

    public DetectConfig(String savePicFilePath) {
        this.savePicFilePath = savePicFilePath;
    }

    public void setEnableFaceDetect(boolean enableFaceDetect) {
        EnableFaceDetect = enableFaceDetect;
    }

    public boolean getEnableFaceDetect() {
        return EnableFaceDetect;
    }

    public void merge(DetectConfig config) {
        cameraDetectProvider = config.cameraDetectProvider;
        confidenceThreshold = config.confidenceThreshold;
        liveConfidenceEnable = config.liveConfidenceEnable;
        liveCheckForAllFace = config.liveCheckForAllFace;
        liveConfidenceThreshold = config.liveConfidenceThreshold;
        showConfidenceTxt = config.showConfidenceTxt;
        savePicFilePath = config.savePicFilePath;
        saveCompressPicMaxSize = config.saveCompressPicMaxSize;
        cameraType = config.cameraType;
        PreDetectTime = config.PreDetectTime;
        PreFaceTime = config.PreFaceTime;
        Simple = config.Simple;
        faceRangeRatio = config.faceRangeRatio;
        autoCalFaceRang = config.autoCalFaceRang;
        matchEdgeDiffFactor = config.matchEdgeDiffFactor;
        matchCenterDiffFactor = config.matchCenterDiffFactor;
        edgeBigTipResId = config.edgeBigTipResId;
        edgeSmallTipResId = config.edgeSmallTipResId;
        centerDiffTipResId = config.centerDiffTipResId;
        MinDetectTime = config.MinDetectTime;
        MaxDetectTime = config.MaxDetectTime;
        EnableFaceDetect = config.EnableFaceDetect;
        delayForSaveFlow = config.delayForSaveFlow;
        EnableIdleSleepOption = config.EnableIdleSleepOption;
        rlMirror = config.rlMirror;
        preWidth = config.preWidth;
        preHeight = config.preHeight;
        takePicRlMirror = config.takePicRlMirror;
        picWidth = config.picWidth;
        picHeight = config.picHeight;
        takePicRotation = config.takePicRotation;
        displayFixTranslationX = config.displayFixTranslationX;
        displayFixTranslationY = config.displayFixTranslationY;
        deviceFixOrientation = config.deviceFixOrientation;
        zoomOffset = config.zoomOffset;
    }

    public void mergeChange(DetectConfig config) {
        merge(config);
    }

    public boolean hasDiffTip() {
        return edgeBigTipResId > -1 || edgeSmallTipResId > -1
                || centerDiffTipResId > -1;
    }

    @Override
    public String toString() {
        return "DetectConfig{" +
                "cameraType=" + cameraType +
                ", rlMirror=" + rlMirror +
                ", preWidth=" + preWidth +
                ", preHeight=" + preHeight +
                ", takePicRlMirror=" + takePicRlMirror +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", takePicRotation=" + takePicRotation +
                ", displayFixTranslationX=" + displayFixTranslationX +
                ", displayFixTranslationY=" + displayFixTranslationY +
                ", deviceFixOrientation=" + deviceFixOrientation +
                ", zoomOffset=" + zoomOffset +
                ", faceRangeRatio=" + faceRangeRatio +
                ", autoCalFaceRang=" + autoCalFaceRang +
                ", confidenceThreshold=" + confidenceThreshold +
                ", liveConfidenceEnable=" + liveConfidenceEnable +
                ", liveCheckForAllFace=" + liveCheckForAllFace +
                ", liveConfidenceThreshold=" + liveConfidenceThreshold +
                ", showConfidenceTxt=" + showConfidenceTxt +
                ", cameraDetectProvider='" + cameraDetectProvider + '\'' +
                ", matchEdgeDiffFactor=" + matchEdgeDiffFactor +
                ", matchCenterDiffFactor=" + matchCenterDiffFactor +
                ", edgeBigTipResId=" + edgeBigTipResId +
                ", edgeSmallTipResId=" + edgeSmallTipResId +
                ", centerDiffTipResId=" + centerDiffTipResId +
                ", PreDetectTime=" + PreDetectTime +
                ", PreFaceTime=" + PreFaceTime +
                ", Simple=" + Simple +
                ", EnableIdleSleepOption=" + EnableIdleSleepOption +
                ", IdleSleepOptionJudgeTime=" + IdleSleepOptionJudgeTime +
                ", MinDetectTime=" + MinDetectTime +
                ", MaxDetectTime=" + MaxDetectTime +
                ", EnableFaceDetect=" + EnableFaceDetect +
                ", DETECT_FACE_NUM=" + DETECT_FACE_NUM +
                ", delayForSaveFlow=" + delayForSaveFlow +
                ", savePicFilePath='" + savePicFilePath + '\'' +
                ", saveCompressPicMaxSize=" + saveCompressPicMaxSize +
                '}';
    }
}
