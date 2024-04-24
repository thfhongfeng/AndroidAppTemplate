package com.pine.app.lib.face.detect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.detect.serial.ISerialActionProxy;

public interface IFaceDetectView {
    String TAG = "FaceDetectView";

    void init(@NonNull DetectConfig config, final IOnFacePicListener listener);

    void resetDetectConfig(@NonNull DetectConfig config);

    /*
    faceMantleBgColor 为镂空圆的半径，单位px
    faceMantleRadius 为透明色背景
    faceMantleRx 为圆心的x轴坐标，单位px
    faceMantleRy 为圆心的y轴坐标，单位px
    */
    void setFaceMantleAttr(int faceMantleBgColor, int faceMantleRadius,
                           int faceMantleRx, int faceMantleRy);

    /**
     * @param faceMantleRxWeight 默认在中心x位置权重（0~1），没有指定faceMantleRx，使用此值进行计算
     * @param faceMantleRyWeight 默认在中心y位置权重（0~1），没有指定faceMantleRy，使用此值进行计算
     */
    void setFaceMantleCenter(float faceMantleRxWeight, float faceMantleRyWeight);

    void setSerialActionProxy(@Nullable ISerialActionProxy proxy);

    void onSerialActionDone(int actionStep, boolean success);

    boolean startFaceDetect();

    void stopFaceDetect();

    void startCameraPreview();

    void stopCameraPreview();

    boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback);

    void completeRecording(boolean resumePreview);

    void stopRecording(boolean resumePreview);

    void stop();

    void release();
}
