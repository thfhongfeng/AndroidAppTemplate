package com.pine.app.lib.face.detect.normal.minivision;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.mv.engine.DetectionResult;
import com.mv.engine.EngineWrapper;
import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.normal.ICameraFaceDetector;

import java.util.ArrayList;
import java.util.List;

public class MiniVisionCameraFaceDetector implements ICameraFaceDetector {
    private static EngineWrapper mEngineWrapper;
    private static boolean mEnginePrepared = false;

    public MiniVisionCameraFaceDetector(Context context) {
        synchronized (MiniVisionCameraFaceDetector.class) {
            if (mEngineWrapper == null) {
                mEngineWrapper = new EngineWrapper(context.getAssets());
            }
            if (!mEnginePrepared) {
                mEnginePrepared = mEngineWrapper.init();
            }
        }
    }

    private DetectConfig mDetectConfig;
    private CameraSurfaceParams mCameraSurfaceParams;

    @Override
    public void onCameraInit(int captureBitmapWidth, int captureBitmapHeight,
                             CameraSurfaceParams surfaceParams, DetectConfig config) {
        Log.d(TAG, "onCameraInit config:" + config);
        mCameraSurfaceParams = surfaceParams;
        mDetectConfig = config;
    }

    @Override
    public FacePosDetail detectMainFace(byte[] yuv) {
        List<FacePosDetail> faces = detectFaces(yuv);
        if (faces == null || faces.size() < 1) {
            return null;
        }
        return faces.get(0);
    }

    @Override
    public List<FacePosDetail> detectFaces(byte[] yuv) {
        if (!mEnginePrepared || yuv == null) {
            return null;
        }
        int rotation = mCameraSurfaceParams.yuvDataRotate;
        boolean rlMirror = mCameraSurfaceParams.yuvDataRlMirror;
        List<DetectionResult> resultList = mEngineWrapper.detect(
                yuv, mCameraSurfaceParams.preWidth, mCameraSurfaceParams.preHeight,
                rlMirror, rotation, mDetectConfig.liveCheckForAllFace
        );
        List<FacePosDetail> facePosDetailList = new ArrayList<>();
        for (DetectionResult result : resultList) {
            FacePosDetail detail = toFaceFeature(result);
            detail.yuvFrameData = true;
            facePosDetailList.add(detail);
        }
        return facePosDetailList;
    }

    @Override
    public FacePosDetail detectMainFace(Bitmap captureBitmap) {
        return null;
    }

    @Override
    public List<FacePosDetail> detectFaces(Bitmap captureBitmap) {
        return null;
    }

    private FacePosDetail toFaceFeature(DetectionResult face) {
        if (face == null) {
            return null;
        }
        float frameZoomRatio = mCameraSurfaceParams.frameZoomRatio;
        FacePosDetail facePosDetail = new FacePosDetail();
        facePosDetail.confidence = face.getConfidence();
        facePosDetail.liveConfidence = face.getLiveConfidence();
        // yuv数据没有应用采样率，因此需要在后续计算中忽略
        facePosDetail.ignoreSimple = true;
        facePosDetail.eyesDist = Math.abs(face.getLeft() - face.getRight()) * frameZoomRatio / 2;
        facePosDetail.midPointX = (face.getLeft() + face.getRight()) * frameZoomRatio / 2;
        facePosDetail.midPointY = (face.getTop() + face.getBottom()) * frameZoomRatio / 2;
        facePosDetail.width = Math.abs(face.getLeft() - face.getRight());
        facePosDetail.height = Math.abs(face.getTop() - face.getBottom());

        FacePosDetail realFacePosDetail = facePosDetail.createBySimple(mDetectConfig.Simple);
        if (mCameraSurfaceParams.yuvDataRotate == 90 || mCameraSurfaceParams.yuvDataRotate == 270) {
            if (!mCameraSurfaceParams.yuvDataRlMirror) {
                realFacePosDetail.midPointY = mCameraSurfaceParams.frameHeight - realFacePosDetail.midPointY;
            }
        }
        int tX = mCameraSurfaceParams.translationX;
        int tY = mCameraSurfaceParams.translationY;
        if (mCameraSurfaceParams.yuvDataRlMirror) {
            realFacePosDetail.midPointX = mCameraSurfaceParams.frameWidth - realFacePosDetail.midPointX;
        }
        realFacePosDetail.midPointX = realFacePosDetail.midPointX + tX;
        realFacePosDetail.midPointY = realFacePosDetail.midPointY + tY;
        return realFacePosDetail;
    }
}
