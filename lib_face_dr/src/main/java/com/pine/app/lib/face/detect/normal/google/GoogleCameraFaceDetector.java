package com.pine.app.lib.face.detect.normal.google;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.normal.ICameraFaceDetector;

import java.util.ArrayList;
import java.util.List;

public class GoogleCameraFaceDetector implements ICameraFaceDetector {
    private FaceDetector mFaceDetector;

    private DetectConfig mDetectConfig;
    private CameraSurfaceParams mCameraSurfaceParams;
    private int mWidth, mHeight, mMaxFaceNum;

    @Override
    public void onCameraInit(int captureBitmapWidth, int captureBitmapHeight,
                             CameraSurfaceParams surfaceParams, DetectConfig config) {
        mWidth = captureBitmapWidth;
        mHeight = captureBitmapHeight;
        mCameraSurfaceParams = surfaceParams;
        mDetectConfig = config;
        mMaxFaceNum = config.DETECT_FACE_NUM;
        mFaceDetector = new FaceDetector(mWidth, mHeight, mMaxFaceNum);
    }

    @Override
    public List<FacePosDetail> detectFaces(byte[] yuv) {
        return null;
    }

    @Override
    public FacePosDetail detectMainFace(byte[] yuv) {
        return null;
    }

    @Override
    public FacePosDetail detectMainFace(Bitmap captureBitmap) {
        List<FacePosDetail> faces = detectFaces(captureBitmap);
        if (faces == null || faces.size() < 1) {
            return null;
        }
        return faces.get(0);
    }

    @Override
    public List<FacePosDetail> detectFaces(Bitmap captureBitmap) {
        if (captureBitmap == null) {
            Log.d(TAG, "detectFace fail for captureBitmap is null");
            return null;
        }
        if (mFaceDetector == null) {
            Log.d(TAG, "detectFace fail for face detector not init");
            return null;
        }
        if (mFaceDetector == null || captureBitmap.getWidth() != mWidth
                || captureBitmap.getHeight() != mHeight) {
            Log.d(TAG, "detectFace fail for width:" + mWidth + ", height:" + mHeight
                    + " and captureBitmap width:" + captureBitmap.getWidth()
                    + ", captureBitmap height:" + captureBitmap.getHeight());
            return null;
        }
        FaceDetector.Face[] faces = new FaceDetector.Face[mMaxFaceNum];
        int detectedFaceNum = mFaceDetector.findFaces(captureBitmap, faces);
        if (detectedFaceNum > 0) {
            return toFaceFeatureList(faces);
        }
        return null;
    }

    private List<FacePosDetail> toFaceFeatureList(FaceDetector.Face[] faces) {
        List<FacePosDetail> list = new ArrayList<>();
        if (faces != null) {
            for (FaceDetector.Face face : faces) {
                FacePosDetail facePosDetail = toFaceFeature(face);
                if (facePosDetail != null) {
                    list.add(toFaceFeature(face));
                }
            }
        }
        return list;
    }

    private FacePosDetail toFaceFeature(FaceDetector.Face face) {
        if (face == null) {
            return null;
        }
        FacePosDetail facePosDetail = new FacePosDetail();
        float confidence = face.confidence() + 0.4f;
        confidence = confidence > 1.0f ? 1.0f : confidence;
        facePosDetail.confidence = confidence;
        facePosDetail.liveConfidence = confidence;
        facePosDetail.eyesDist = face.eyesDistance();
        PointF pointF = new PointF();
        face.getMidPoint(pointF);
        facePosDetail.midPointX = pointF.x;
        facePosDetail.midPointY = pointF.y;
        if (facePosDetail.width <= 0) {
            facePosDetail.width = facePosDetail.eyesDist * 5 / 2;
        }
        if (facePosDetail.height <= 0) {
            facePosDetail.height = facePosDetail.eyesDist * 4;
        }

        FacePosDetail realFacePosDetail = facePosDetail.createBySimple(mDetectConfig.Simple);
        int tX = mCameraSurfaceParams.translationX;
        int tY = mCameraSurfaceParams.translationY;
        if (mDetectConfig.rlMirror) {
            realFacePosDetail.midPointX = mCameraSurfaceParams.frameWidth - realFacePosDetail.midPointX;
        }
        realFacePosDetail.midPointX = realFacePosDetail.midPointX + tX;
        realFacePosDetail.midPointY = realFacePosDetail.midPointY + tY;

        return realFacePosDetail;
    }
}
